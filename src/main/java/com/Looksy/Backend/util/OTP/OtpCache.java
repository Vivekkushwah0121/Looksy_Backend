package com.Looksy.Backend.util.OTP;
// Inside your OtpCache.java (or a separate UnverifiedUserCache)
import com.Looksy.Backend.model.userSchema;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
// Assuming userSchema is correctly imported

@Component
public class OtpCache {
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final Map<String, userSchema> unverifiedUserStore = new ConcurrentHashMap<>(); // <--- ADD THIS MAP

    // ... existing putOtp and getOtp logic ...

    public void putOtp(String mobileNumber, String otp) {
        otpStore.put(mobileNumber, otp); // No expiry shown in your code, consider adding
    }

    public String getOtp(String mobileNumber) {
        return otpStore.get(mobileNumber);
    }

    public void invalidateOtp(String mobileNumber) {
        otpStore.remove(mobileNumber);
    }

    // --- New methods for unverified user storage ---
    public void putUnverifiedUser(String mobileNumber, userSchema user) {
        unverifiedUserStore.put(mobileNumber, user);
        // Consider adding expiry here as well, similar to OTP
    }

    public userSchema getUnverifiedUser(String mobileNumber) {
        return unverifiedUserStore.get(mobileNumber);
    }

    public void invalidateUnverifiedUser(String mobileNumber) {
        unverifiedUserStore.remove(mobileNumber);
    }
}