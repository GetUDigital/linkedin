package com.boinew.api.service;

import com.boinew.api.document.Comment;
import com.boinew.api.document.Post;
import com.boinew.api.dto.request.CommentRequest;
import com.boinew.api.exception.ResourceNotFoundException;
import com.boinew.api.exception.UnauthorizedException;
import com.boinew.api.repository.mongo.CommentRepository;
import com.boinew.api.repository.mongo.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Comment addComment(String postId, Long userId, CommentRequest req) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .parentId(req.getParentId())
                .content(req.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        Comment saved = commentRepository.save(comment);

        // Increment comment count on post
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return saved;
    }

    public List<Comment> getComments(String postId) {
        // Only top-level comments; replies fetched separately
        return commentRepository.findByPostIdAndParentIdIsNullOrderByCreatedAtAsc(postId);
    }

    public List<Comment> getReplies(String parentId) {
        return commentRepository.findByParentIdOrderByCreatedAtAsc(parentId);
    }

    public void deleteComment(String commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

        if (!comment.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own comments");
        }

        // Decrement post comment count
        postRepository.findById(comment.getPostId()).ifPresent(post -> {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postRepository.save(post);
        });

        commentRepository.delete(comment);
    }
}
