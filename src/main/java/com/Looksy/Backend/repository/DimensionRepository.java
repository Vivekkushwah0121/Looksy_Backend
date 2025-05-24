package com.Looksy.Backend.repository;

import com.Looksy.Backend.model.Dimension;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DimensionRepository extends MongoRepository<Dimension, ObjectId> {
    // Custom query methods can be defined here if needed
}
