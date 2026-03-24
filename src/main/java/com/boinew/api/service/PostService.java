package com.boinew.api.service;

import com.boinew.api.document.Post;
import com.boinew.api.dto.request.PostRequest;
import com.boinew.api.dto.response.PostResponse;
import com.boinew.api.entity.User;
import com.boinew.api.exception.ResourceNotFoundException;
import com.boinew.api.exception.UnauthorizedException;
import com.boinew.api.repository.mongo.PostRepository;
import com.boinew.api.repository.mysql.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse createPost(Long userId, PostRequest.Create req) {
        Post post = Post.builder()
                .userId(userId)
                .content(req.getContent())
                .mediaUrl(req.getMediaUrl())
                .mediaType(req.getMediaType())
                .visibility(req.getVisibility())
                .reactions(new HashMap<>())
                .commentCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postRepository.save(post);
        return toResponse(post, userId);
    }

    public PostResponse getPost(String postId, Long currentUserId) {
        Post post = findPost(postId);
        return toResponse(post, currentUserId);
    }

    public Page<PostResponse> getFeed(Long currentUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository
                .findByVisibilityOrderByCreatedAtDesc(Post.Visibility.PUBLIC, pageable)
                .map(p -> toResponse(p, currentUserId));
    }

    public Page<PostResponse> getUserPosts(Long userId, Long currentUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(p -> toResponse(p, currentUserId));
    }

    public PostResponse updatePost(String postId, Long userId, PostRequest.Update req) {
        Post post = findPost(postId);
        assertOwner(post, userId);

        post.setContent(req.getContent());
        if (req.getVisibility() != null) post.setVisibility(req.getVisibility());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
        return toResponse(post, userId);
    }

    public void deletePost(String postId, Long userId) {
        Post post = findPost(postId);
        assertOwner(post, userId);
        postRepository.delete(post);
    }

    public PostResponse reactToPost(String postId, Long userId, PostRequest.React req) {
        Post post = findPost(postId);
        String type = req.getReactionType().toLowerCase();

        // Remove user from all reaction types first (toggle support)
        post.getReactions().values().forEach(list -> list.remove(userId));

        // Add new reaction
        post.getReactions().computeIfAbsent(type, k -> new ArrayList<>()).add(userId);

        // Cleanup empty reaction lists
        post.getReactions().entrySet().removeIf(e -> e.getValue().isEmpty());

        postRepository.save(post);
        return toResponse(post, userId);
    }

    public PostResponse removeReaction(String postId, Long userId) {
        Post post = findPost(postId);
        post.getReactions().values().forEach(list -> list.remove(userId));
        post.getReactions().entrySet().removeIf(e -> e.getValue().isEmpty());
        postRepository.save(post);
        return toResponse(post, userId);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private Post findPost(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
    }

    private void assertOwner(Post post, Long userId) {
        if (!post.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to modify this post");
        }
    }

    private PostResponse toResponse(Post post, Long currentUserId) {
        User author = userRepository.findById(post.getUserId()).orElse(null);

        Map<String, Integer> reactionCounts = post.getReactions().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));

        String currentUserReaction = post.getReactions().entrySet().stream()
                .filter(e -> e.getValue().contains(currentUserId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        return PostResponse.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .authorName(author != null
                        ? author.getFirstName() + " " + author.getLastName() : "Unknown")
                .authorHeadline(author != null ? author.getHeadline() : null)
                .authorPhoto(author != null ? author.getProfilePhoto() : null)
                .content(post.getContent())
                .mediaUrl(post.getMediaUrl())
                .mediaType(post.getMediaType())
                .visibility(post.getVisibility())
                .reactionCounts(reactionCounts)
                .currentUserReaction(currentUserReaction)
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
