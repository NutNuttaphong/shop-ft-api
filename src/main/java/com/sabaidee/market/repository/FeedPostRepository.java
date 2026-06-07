package com.sabaidee.market.repository;

import com.sabaidee.market.model.FeedPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedPostRepository extends MongoRepository<FeedPost, String> {
    List<FeedPost> findAllByOrderByCreatedAtDesc();
}
