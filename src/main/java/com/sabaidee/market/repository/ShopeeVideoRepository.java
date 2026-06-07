package com.sabaidee.market.repository;

import com.sabaidee.market.model.ShopeeVideo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopeeVideoRepository extends MongoRepository<ShopeeVideo, String> {
}
