package com.Looksy.Backend.util.OTP; // Keep your package consistent

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Component; // ADD THIS IMPORT
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component // Mark as a Spring-managed component
public class OtpRateLimiter {

    private final ConcurrentHashMap<String, RateLimiter> limiterMap = new ConcurrentHashMap<>();
    private final RateLimiter globalRequestLimiter = RateLimiter.create(10.0);
    private final double requestsPerSecondPerIdentifier = 1.0 / 60.0;

    public OtpRateLimiter() {
        // Constructor is fine as is
    }

    public boolean tryAcquire(String identifier) {
        if (!globalRequestLimiter.tryAcquire()) {
            return false;
        }
        RateLimiter identifierLimiter = limiterMap.computeIfAbsent(
                identifier,
                k -> RateLimiter.create(requestsPerSecondPerIdentifier)
        );
        return identifierLimiter.tryAcquire();
    }
}