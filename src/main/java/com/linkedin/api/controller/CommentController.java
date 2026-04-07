package com.linkedin.api.controller;

import com.linkedin.api.config.CurrentUser;
import com.linkedin.api.document.Comment;
import com.linkedin.api.dto.request.CommentRequest;
import com.linkedin.api.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;
    private final CurrentUser currentUser;

    @Operation(summary = "Add a comment to a post")
    @PostMapping
    public ResponseEntity<Comment> addComment(
            @PathVariable String postId,
            @Valid @RequestBody CommentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(postId, currentUser.getId(), req));
    }

    @Operation(summary = "Get top-level comments for a post")
    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable String postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    @Operation(summary = "Get replies to a comment")
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<Comment>> getReplies(@PathVariable String commentId) {
        return ResponseEntity.ok(commentService.getReplies(commentId));
    }

    @Operation(summary = "Delete your comment")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
