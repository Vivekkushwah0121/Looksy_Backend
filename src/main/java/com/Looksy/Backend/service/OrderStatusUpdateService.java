package com.Looksy.Backend.service;

import com.Looksy.Backend.model.Order;
import com.Looksy.Backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class OrderStatusUpdateService {

    @Autowired
    private OrderRepository orderRepository; // Ensure this is injected

    @Async
    public void scheduleOrderStatusUpdate(String orderId) {
        try {
            Random random = new Random();
            long delayMillis = (1 + random.nextInt(3)) * 60 * 1000L;
            System.out.println("DEBUG: Scheduling order " + orderId + " confirmation in " + (delayMillis / 60000.0) + " minutes (async task).");

            Thread.sleep(delayMillis);

            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();
                order.setStatus("Confirmed");
                orderRepository.save(order);
                System.out.println("DEBUG: Order " + orderId + " status updated to Confirmed in DB by async task.");
            } else {
                System.err.println("DEBUG: Order with ID " + orderId + " not found for status update (async task).");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("ERROR: Order status update for " + orderId + " was interrupted (async task): " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: Error during scheduled order status update for " + orderId + ": " + e.getMessage());
        }
    }
}
