package com.sabaidee.market.repository;

import com.sabaidee.market.model.LiveStream;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveStreamRepository extends MongoRepository<LiveStream, String> {
    List<LiveStream> findByStatusOrderByCreatedAtDesc(String status);
}
