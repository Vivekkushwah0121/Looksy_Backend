package com.Looksy.Backend.model;

import com.Looksy.Backend.config.ObjectIdSerializer; // Import custom serializer
import com.Looksy.Backend.config.ObjectIdDeserializer; // Import custom deserializer (for completeness, though not strictly needed for model output)
import com.fasterxml.jackson.databind.annotation.JsonDeserialize; // Import Jackson annotations
import com.fasterxml.jackson.databind.annotation.JsonSerialize; // Import Jackson annotations
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

@Document(collection = "orders")
public class Order {

    @Id
    private String id; // MongoDB auto-generates ObjectId as String

    @Field("userId")
    @JsonSerialize(using = ObjectIdSerializer.class)    // Apply custom serializer for output
    @JsonDeserialize(using = ObjectIdDeserializer.class) // Apply custom deserializer for input (if ever used in @RequestBody)
    private ObjectId userId;

    private String mobileNumber;

    @Field("productId")
    @JsonSerialize(using = ObjectIdSerializer.class)    // Apply custom serializer for output
    @JsonDeserialize(using = ObjectIdDeserializer.class) // Apply custom deserializer
    private ObjectId productId;

    private String color;

    private String size;

    private LocalDateTime orderTiming;

    private LocalDateTime estimatedDeliveryTiming;

    private String status;

    // Constructors
    public Order() {
    }

    public Order(ObjectId userId, String mobileNumber, ObjectId productId, String color, String size,
                 LocalDateTime orderTiming, LocalDateTime estimatedDeliveryTiming, String status) {
        this.userId = userId;
        this.mobileNumber = mobileNumber;
        this.productId = productId;
        this.color = color;
        this.size = size;
        this.orderTiming = orderTiming;
        this.estimatedDeliveryTiming = estimatedDeliveryTiming;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public ObjectId getProductId() {
        return productId;
    }

    public void setProductId(ObjectId productId) {
        this.productId = productId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public LocalDateTime getOrderTiming() {
        return orderTiming;
    }

    public void setOrderTiming(LocalDateTime orderTiming) {
        this.orderTiming = orderTiming;
    }

    public LocalDateTime getEstimatedDeliveryTiming() {
        return estimatedDeliveryTiming;
    }

    public void setEstimatedDeliveryTiming(LocalDateTime estimatedDeliveryTiming) {
        this.estimatedDeliveryTiming = estimatedDeliveryTiming;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}