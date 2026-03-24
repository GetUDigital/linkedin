package com.boinew.api.controller;

import com.boinew.api.config.CurrentUser;
import com.boinew.api.entity.Connection;
import com.boinew.api.service.ConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
@Tag(name = "Connections")
@SecurityRequirement(name = "bearerAuth")
public class ConnectionController {

    private final ConnectionService connectionService;
    private final CurrentUser currentUser;

    @Operation(summary = "Send a connection request")
    @PostMapping("/request/{receiverId}")
    public ResponseEntity<Connection> sendRequest(@PathVariable Long receiverId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(connectionService.sendRequest(currentUser.getId(), receiverId));
    }

    @Operation(summary = "Accept or reject a connection request")
    @PatchMapping("/{connectionId}/respond")
    public ResponseEntity<Connection> respond(
            @PathVariable Long connectionId,
            @RequestParam boolean accept) {
        return ResponseEntity.ok(
                connectionService.respondToRequest(connectionId, currentUser.getId(), accept));
    }

    @Operation(summary = "Remove a connection")
    @DeleteMapping("/{connectionId}")
    public ResponseEntity<Void> remove(@PathVariable Long connectionId) {
        connectionService.removeConnection(connectionId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get your accepted connections")
    @GetMapping
    public ResponseEntity<List<Connection>> getMyConnections() {
        return ResponseEntity.ok(connectionService.getConnections(currentUser.getId()));
    }

    @Operation(summary = "Get pending connection requests sent to you")
    @GetMapping("/pending")
    public ResponseEntity<List<Connection>> getPendingRequests() {
        return ResponseEntity.ok(connectionService.getPendingRequests(currentUser.getId()));
    }
}
