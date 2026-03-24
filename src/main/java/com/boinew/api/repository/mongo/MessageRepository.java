package com.boinew.api.repository.mongo;

import com.boinew.api.document.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByConvoIdOrderByCreatedAtAsc(String convoId);
    long countByConvoIdAndIsReadFalseAndSenderIdNot(String convoId, Long userId);
}
