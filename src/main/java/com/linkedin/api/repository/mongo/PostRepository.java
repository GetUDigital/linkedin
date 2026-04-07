package com.linkedin.api.repository.mongo;

import com.linkedin.api.document.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Post> findByVisibilityOrderByCreatedAtDesc(Post.Visibility visibility, Pageable pageable);
    List<Post> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds);
}
