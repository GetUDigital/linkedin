package com.linkedin.api.service;

import com.linkedin.api.entity.Connection;
import com.linkedin.api.entity.User;
import com.linkedin.api.exception.BadRequestException;
import com.linkedin.api.exception.ConflictException;
import com.linkedin.api.exception.ResourceNotFoundException;
import com.linkedin.api.exception.UnauthorizedException;
import com.linkedin.api.exception.*;
import com.linkedin.api.repository.mysql.ConnectionRepository;
import com.linkedin.api.repository.mysql.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Connection sendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new BadRequestException("You cannot connect with yourself");
        }
        userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User", receiverId));

        boolean exists = connectionRepository
                .existsBySender_UserIdAndReceiver_UserIdAndStatus(
                        senderId, receiverId, Connection.ConnectionStatus.accepted);
        if (exists) throw new ConflictException("Already connected");

        connectionRepository.findBySender_UserIdAndReceiver_UserId(senderId, receiverId)
                .ifPresent(c -> { throw new ConflictException("Connection request already sent"); });

        User sender   = userRepository.getReferenceById(senderId);
        User receiver = userRepository.getReferenceById(receiverId);

        Connection conn = Connection.builder()
                .sender(sender)
                .receiver(receiver)
                .status(Connection.ConnectionStatus.pending)
                .build();

        return connectionRepository.save(conn);
    }

    @Transactional
    public Connection respondToRequest(Long connectionId, Long currentUserId, boolean accept) {
        Connection conn = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection", connectionId));

        if (!conn.getReceiver().getUserId().equals(currentUserId)) {
            throw new UnauthorizedException("You cannot respond to this request");
        }
        if (conn.getStatus() != Connection.ConnectionStatus.pending) {
            throw new BadRequestException("Connection request is no longer pending");
        }

        conn.setStatus(accept
                ? Connection.ConnectionStatus.accepted
                : Connection.ConnectionStatus.rejected);

        return connectionRepository.save(conn);
    }

    @Transactional
    public void removeConnection(Long connectionId, Long currentUserId) {
        Connection conn = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection", connectionId));

        boolean isParty = conn.getSender().getUserId().equals(currentUserId)
                       || conn.getReceiver().getUserId().equals(currentUserId);
        if (!isParty) throw new UnauthorizedException("Not your connection");

        connectionRepository.delete(conn);
    }

    @Transactional(readOnly = true)
    public List<Connection> getConnections(Long userId) {
        return connectionRepository.findByUserAndStatus(userId, Connection.ConnectionStatus.accepted);
    }

    @Transactional(readOnly = true)
    public List<Connection> getPendingRequests(Long userId) {
        return connectionRepository.findPendingRequestsForUser(userId);
    }
}
