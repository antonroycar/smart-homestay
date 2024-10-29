package com.antonroycar.homestay.repository;

import com.antonroycar.homestay.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {
    boolean existsByUsername(String email);
    Optional<Account> findByUsername(String username);
    Optional<Account> findByToken(String token);
}
