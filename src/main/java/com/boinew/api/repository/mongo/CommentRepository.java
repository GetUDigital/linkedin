package com.boinew.api.repository.mongo;

import com.boinew.api.document.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostIdAndParentIdIsNullOrderByCreatedAtAsc(String postId);
    List<Comment> findByParentIdOrderByCreatedAtAsc(String parentId);
    long countByPostId(String postId);
    void deleteByPostId(String postId);
}
