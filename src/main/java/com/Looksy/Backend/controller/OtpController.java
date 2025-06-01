package com.Looksy.Backend.controller;

import com.Looksy.Backend.service.SmsService;
import com.Looksy.Backend.util.OTP.OtpCache;
import com.Looksy.Backend.util.OTP.OtpGenerator;
import com.Looksy.Backend.util.OTP.OtpRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OtpController {

    // Declare dependencies as final
    private final OtpCache otpCache;
    private final OtpRateLimiter otpRateLimiter;
    private final SmsService smsService;

    // Use constructor injection
    // Spring will automatically find and inject instances of OtpCache, OtpRateLimiter, and SmsService
    @Autowired // @Autowired is optional here if there's only one constructor
    public OtpController(OtpCache otpCache, OtpRateLimiter otpRateLimiter, SmsService smsService) {
        this.otpCache = otpCache;
        this.otpRateLimiter = otpRateLimiter;
        this.smsService = smsService;
    }

    // Endpoint to request OTP
    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestParam String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return ResponseEntity.badRequest().body("Phone number is required.");
        }

        // Use the injected rateLimiter instance
        if (!otpRateLimiter.tryAcquire(phoneNumber)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many OTP requests. Please try again later.");
        }

        // OtpGenerator is a static utility, so you call its static method directly
        String otp = OtpGenerator.generateOtp();
        otpCache.putOtp(phoneNumber, otp); // Use the injected otpCache instance

        smsService.sendSms(phoneNumber, otp); // Use the injected smsService instance

        return ResponseEntity.ok("OTP sent to " + phoneNumber);
    }

    // Endpoint to verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestParam String phoneNumber,
            @RequestParam String userOtp) {

        if (phoneNumber == null || phoneNumber.isEmpty() || userOtp == null || userOtp.isEmpty()) {
            return ResponseEntity.badRequest().body("Phone number and OTP are required.");
        }

        String storedOtp = otpCache.getOtp(phoneNumber); // Use the injected otpCache instance

        if (storedOtp == null) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP. Please request a new one.");
        }

        if (storedOtp.equals(userOtp)) {
            otpCache.invalidateOtp(phoneNumber); // Use the injected otpCache instance
            return ResponseEntity.ok("OTP verified successfully!");
        } else {
            return ResponseEntity.badRequest().body("Incorrect OTP. Please try again.");
        }
    }
}