package com.sabaidee.market.repository;

import com.sabaidee.market.model.User;
import com.sabaidee.market.model.enums.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    long countByRole(UserRole role);
}
