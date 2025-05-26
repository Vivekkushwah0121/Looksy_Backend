package com.Looksy.Backend.service;

import com.Looksy.Backend.dto.OrderConfirmationDTO;
import com.Looksy.Backend.dto.OrderRequest;
import com.Looksy.Backend.model.Order;
import com.Looksy.Backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List; // Import List
import java.util.Optional;
import java.util.Random;
import org.bson.types.ObjectId;

@Service
public class OrderService {


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusUpdateService orderStatusUpdateService; // Inject the service

    public Order placeOrder(OrderRequest orderRequest) {
        try {
            LocalDateTime orderTime = LocalDateTime.now();
            LocalDateTime estimatedDeliveryTime = orderTime.plus(5, ChronoUnit.DAYS);

            Order order = new Order();
            order.setUserId(orderRequest.getUserId());
            order.setMobileNumber(orderRequest.getMobileNumber());
            order.setProductId(orderRequest.getProductId());
            order.setColor(orderRequest.getColor());
            order.setSize(orderRequest.getSize());
            order.setOrderTiming(orderTime);
            order.setEstimatedDeliveryTiming(estimatedDeliveryTime);
            order.setStatus("Pending");

            // Save the order immediately with "Pending" status
            Order savedOrder = orderRepository.save(order);

            // Schedule the asynchronous status update
            orderStatusUpdateService.scheduleOrderStatusUpdate(savedOrder.getId());

            // Return the saved order (which is currently "Pending") immediately to the client
            return savedOrder;

        } catch (Exception e) {
            System.err.println("Error placing order: " + e.getMessage());
            throw new RuntimeException("Failed to place order: " + e.getMessage());
        }
    }

//    @Async
//    public void scheduleOrderStatusUpdate(String orderId) {
//        try {
//            Random random = new Random();
//            long delayMillis = (1 + random.nextInt(3)) * 60 * 1000L;
//            System.out.println("DEBUG: Scheduling order " + orderId + " confirmation in " + (delayMillis / 60000.0) + " minutes (async task).");
//
//            Thread.sleep(delayMillis);
//
//            Optional<Order> optionalOrder = orderRepository.findById(orderId);
//
//            if (optionalOrder.isPresent()) {
//                Order order = optionalOrder.get();
//                order.setStatus("Confirmed");
//                orderRepository.save(order);
//
//                System.out.println("DEBUG: Order " + orderId + " status updated to Confirmed in DB by async task.");
//
//            } else {
//                System.err.println("DEBUG: Order with ID " + orderId + " not found for status update (async task).");
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            System.err.println("ERROR: Order status update for " + orderId + " was interrupted (async task): " + e.getMessage());
//        } catch (Exception e) {
//            System.err.println("ERROR: Error during scheduled order status update for " + orderId + ": " + e.getMessage());
//        }
//    }

    public OrderConfirmationDTO getOrderConfirmationDetails(String orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            return new OrderConfirmationDTO(order.getId(), order.getProductId(), order.getUserId(), order.getStatus());
        }
        return null;
    }

    public List<Order> getOrdersByUserId(ObjectId userId) {
        return orderRepository.findByUserId(userId);
    }
}