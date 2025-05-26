package com.Looksy.Backend.dto;

import com.Looksy.Backend.config.ObjectIdSerializer; // Import custom serializer
import com.Looksy.Backend.config.ObjectIdDeserializer; // Import custom deserializer (for completeness)
import com.fasterxml.jackson.databind.annotation.JsonDeserialize; // Import Jackson annotations
import com.fasterxml.jackson.databind.annotation.JsonSerialize; // Import Jackson annotations
import org.bson.types.ObjectId;

public class OrderConfirmationDTO {
    private String orderId;

    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class) // Add for completeness, though not strictly needed for this DTO's output
    private ObjectId productId;

    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class) // Add for completeness
    private ObjectId userId;
    private String status;

    public OrderConfirmationDTO() {
    }

    public OrderConfirmationDTO(String orderId, ObjectId productId, ObjectId userId, String status) {
        this.orderId = orderId;
        this.productId = productId;
        this.userId = userId;
        this.status = status;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ObjectId getProductId() {
        return productId;
    }

    public void setProductId(ObjectId productId) {
        this.productId = productId;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}