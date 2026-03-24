package com.boinew.api.repository.mongo;

import com.boinew.api.document.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    @Query("{ 'memberIds': ?0 }")
    List<Conversation> findByMemberId(Long userId);

    @Query("{ 'memberIds': { $all: [?0, ?1] }, $where: 'this.memberIds.length == 2' }")
    Optional<Conversation> findDirectConversation(Long userIdA, Long userIdB);
}
