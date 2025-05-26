package com.Looksy.Backend.controller;

import com.Looksy.Backend.dto.OrderConfirmationDTO;
import com.Looksy.Backend.dto.OrderRequest;
import com.Looksy.Backend.model.Order;
import com.Looksy.Backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.bson.types.ObjectId; // Import ObjectId

import java.util.List; // Import List

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<?> placeNewOrder(@Valid @RequestBody OrderRequest orderRequest) {
        try {
            Order newOrder = orderService.placeOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/confirmation/{orderId}")
    public ResponseEntity<?> getOrderConfirmationDetails(@PathVariable String orderId) {
        try {
            OrderConfirmationDTO confirmation = orderService.getOrderConfirmationDetails(orderId);
            if (confirmation != null) {
                return ResponseEntity.ok(confirmation);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found with ID: " + orderId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching order confirmation: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}") // Using a different path to avoid conflict with /orders/{orderId}
    public ResponseEntity<?> getAllOrdersByUserId(@PathVariable @Valid @org.springframework.web.bind.annotation.RequestBody String userId) {
        try {

            ObjectId userObjectId;
            try {
                userObjectId = new ObjectId(userId);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid User ID format: " + userId);
            }

            List<Order> orders = orderService.getOrdersByUserId(userObjectId);
            if (!orders.isEmpty()) {
                return ResponseEntity.ok(orders);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No orders found for User ID: " + userId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching orders for user: " + e.getMessage());
        }
    }
}