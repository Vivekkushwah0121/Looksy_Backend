package com.Looksy.Backend.repository;


import com.Looksy.Backend.model.userSchema;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<userSchema,String> {
    Optional<userSchema> findByMobileNumber(String mobileNumber);
}
