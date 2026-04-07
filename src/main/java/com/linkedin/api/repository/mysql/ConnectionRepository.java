package com.linkedin.api.repository.mysql;

import com.linkedin.api.entity.Connection;
import com.linkedin.api.entity.Connection.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Optional<Connection> findBySender_UserIdAndReceiver_UserId(Long senderId, Long receiverId);

    @Query("SELECT c FROM Connection c WHERE " +
           "(c.sender.userId = :userId OR c.receiver.userId = :userId) AND c.status = :status")
    List<Connection> findByUserAndStatus(@Param("userId") Long userId,
                                         @Param("status") ConnectionStatus status);

    @Query("SELECT c FROM Connection c WHERE c.receiver.userId = :userId AND c.status = 'pending'")
    List<Connection> findPendingRequestsForUser(@Param("userId") Long userId);

    boolean existsBySender_UserIdAndReceiver_UserIdAndStatus(
            Long senderId, Long receiverId, ConnectionStatus status);
}
