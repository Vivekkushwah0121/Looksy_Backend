package com.Looksy.Backend.repository;

import com.Looksy.Backend.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.bson.types.ObjectId;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(ObjectId userId);
    List<Order> findByProductId(ObjectId productId);
    List<Order> findByStatus(String status);
}