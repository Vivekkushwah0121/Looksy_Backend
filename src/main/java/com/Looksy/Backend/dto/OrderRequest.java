package com.Looksy.Backend.dto;

import com.Looksy.Backend.config.ObjectIdDeserializer; // Import the custom deserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize; // Import Jackson annotations
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;


public class OrderRequest {

    @NotNull(message = "User ID cannot be null")
    @JsonDeserialize(using = ObjectIdDeserializer.class) // Apply custom deserializer
    private ObjectId userId;

    @NotBlank(message = "Mobile number cannot be blank")
    private String mobileNumber;

    @NotNull(message = "Product ID cannot be null")
    @JsonDeserialize(using = ObjectIdDeserializer.class) // Apply custom deserializer
    private ObjectId productId;

    @NotBlank(message = "Color cannot be blank")
    private String color;

    @NotBlank(message = "Size cannot be blank")
    private String size;

    // Constructors
    public OrderRequest() {
    }

    public OrderRequest(ObjectId userId, String mobileNumber, ObjectId productId, String color, String size) {
        this.userId = userId;
        this.mobileNumber = mobileNumber;
        this.productId = productId;
        this.color = color;
        this.size = size;
    }

    // Getters and Setters
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
}