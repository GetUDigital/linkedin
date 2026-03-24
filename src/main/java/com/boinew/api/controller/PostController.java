package com.boinew.api.controller;

import com.boinew.api.config.CurrentUser;
import com.boinew.api.dto.request.PostRequest;
import com.boinew.api.dto.response.PostResponse;
import com.boinew.api.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Posts")
@SecurityRequirement(name = "bearerAuth")
public class PostController {

    private final PostService postService;
    private final CurrentUser currentUser;

    @Operation(summary = "Create a new post")
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest.Create req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(currentUser.getId(), req));
    }

    @Operation(summary = "Get public feed (paginated)")
    @GetMapping("/feed")
    public ResponseEntity<Page<PostResponse>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(postService.getFeed(currentUser.getId(), page, size));
    }

    @Operation(summary = "Get posts by a specific user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponse>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(postService.getUserPosts(userId, currentUser.getId(), page, size));
    }

    @Operation(summary = "Get a single post by ID")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable String postId) {
        return ResponseEntity.ok(postService.getPost(postId, currentUser.getId()));
    }

    @Operation(summary = "Update your post")
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable String postId,
            @Valid @RequestBody PostRequest.Update req) {
        return ResponseEntity.ok(postService.updatePost(postId, currentUser.getId(), req));
    }

    @Operation(summary = "Delete your post")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
        postService.deletePost(postId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "React to a post (like, celebrate, support, love, insightful, funny)")
    @PostMapping("/{postId}/react")
    public ResponseEntity<PostResponse> react(
            @PathVariable String postId,
            @Valid @RequestBody PostRequest.React req) {
        return ResponseEntity.ok(postService.reactToPost(postId, currentUser.getId(), req));
    }

    @Operation(summary = "Remove your reaction from a post")
    @DeleteMapping("/{postId}/react")
    public ResponseEntity<PostResponse> removeReaction(@PathVariable String postId) {
        return ResponseEntity.ok(postService.removeReaction(postId, currentUser.getId()));
    }
}
