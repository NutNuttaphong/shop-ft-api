package com.sabaidee.market.repository;

import com.sabaidee.market.model.VisitorLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface VisitorLogRepository extends MongoRepository<VisitorLog, String> {
    List<VisitorLog> findByCreatedAtBetween(Instant start, Instant end);
    long countByCreatedAtBetween(Instant start, Instant end);
}
