package com.epam.repo;

import com.epam.model.JWT;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JWTRepo extends MongoRepository<JWT, String> {
    boolean existsByToken(String token);
}
