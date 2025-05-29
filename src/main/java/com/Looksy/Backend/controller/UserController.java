package com.Looksy.Backend.controller;

import com.Looksy.Backend.dto.AuthResponse;
import com.Looksy.Backend.dto.OtpRequest;
import com.Looksy.Backend.dto.OtpVerificationRequest;
import com.Looksy.Backend.exception.ResourceNotFoundException;
import com.Looksy.Backend.exception.SmsServiceException;
import com.Looksy.Backend.model.Address;
import com.Looksy.Backend.model.userSchema;
import com.Looksy.Backend.service.SmsService;
import com.Looksy.Backend.service.UserService;
import com.Looksy.Backend.dto.ApiResponse;
import com.Looksy.Backend.util.JwtUtil;
import com.Looksy.Backend.util.OTP.OtpCache;
import com.Looksy.Backend.util.OTP.OtpGenerator;
import com.Looksy.Backend.util.OTP.OtpRateLimiter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    @Autowired
    private final UserService userService;
    private final OtpCache otpCache;
    private final OtpRateLimiter otpRateLimiter;
    private final SmsService smsService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, OtpCache otpCache, OtpRateLimiter otpRateLimiter, SmsService smsService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.otpCache = otpCache;
        this.otpRateLimiter = otpRateLimiter;
        this.smsService = smsService;
        this.jwtUtil = jwtUtil;
    }

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // milliseconds


    // --- Request OTP for Registration ---
    @PostMapping("/register/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestRegistrationOtp(@Valid @RequestBody OtpRequest otpRequest) {
        String mobileNumber = otpRequest.getMobileNumber();

        // 1. Check if user already registered and verified
        if (userService.checkUserAlreadyRegistered(mobileNumber)) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "User with this mobile number already registered. Please login.", null),
                    HttpStatus.CONFLICT
            );
        }

        // 2. Apply rate limiting to avoid spamming OTP requests
        if (!otpRateLimiter.tryAcquire(mobileNumber)) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Too many OTP requests. Please try again later.", null),
                    HttpStatus.TOO_MANY_REQUESTS
            );
        }

        // 3. Generate OTP
        String otp = OtpGenerator.generateOtp();

        // 4. Store OTP in cache
        otpCache.putOtp(mobileNumber, otp);

        // 5. Store initial unverified user data in cache
        userSchema unverifiedUser = new userSchema();
        unverifiedUser.setMobileNumber(mobileNumber);
        otpCache.putUnverifiedUser(mobileNumber, unverifiedUser);

        // 6. Send OTP via SMS
        try {
            smsService.sendSms(mobileNumber, otp);
        } catch (SmsServiceException e) {
            System.err.println("Failed to send SMS to " + mobileNumber + " due to: " + e.getMessage());
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Failed to send OTP. Please try again later.", null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return new ResponseEntity<>(
                new ApiResponse<>(true, "OTP sent successfully to " + mobileNumber + ". Please verify.", null),
                HttpStatus.OK
        );
    }

    // --- Verify OTP and Complete Registration ---
    @PostMapping("/register/verify-otp")
    public ResponseEntity<ApiResponse<userSchema>> verifyRegistrationOtp(@Valid @RequestBody OtpVerificationRequest verificationRequest) {
        String mobileNumber = verificationRequest.getMobileNumber();
        String userProvidedOtp = verificationRequest.getOtp();

        // 1. Retrieve stored OTP
        String storedOtp = otpCache.getOtp(mobileNumber);
        if (storedOtp == null) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "OTP either expired or not requested. Please request a new one.", null),
                    HttpStatus.BAD_REQUEST
            );
        }

        // 2. Compare OTPs
        if (!storedOtp.equals(userProvidedOtp)) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Incorrect OTP. Please try again.", null),
                    HttpStatus.UNAUTHORIZED
            );
        }

        // 3. OTP is correct: Invalidate OTP from cache
        otpCache.invalidateOtp(mobileNumber);

        // 4. Retrieve unverified user details from cache
        userSchema unverifiedUser = otpCache.getUnverifiedUser(mobileNumber);
        if (unverifiedUser == null) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "User details not found for verification. Please restart registration.", null),
                    HttpStatus.BAD_REQUEST
            );
        }

        // 5. Create the user using the details from the cache
        userSchema createdUser = userService.createUser(unverifiedUser);
        otpCache.invalidateUnverifiedUser(mobileNumber);

        ApiResponse<userSchema> response = new ApiResponse<>(
                true,
                "Account created and verified successfully!",
                createdUser
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- Create User Directly Endpoint (Optional) ---
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<userSchema>> createUser(@Valid @RequestBody userSchema user) {
        userSchema createdUser = userService.createUser(user);
        ApiResponse<userSchema> response = new ApiResponse<>(
                true,
                "User created successfully!",
                createdUser
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- Login User Endpoint: authenticate and generate JWT ---
    @PostMapping("/login")
    public ResponseEntity<AuthResponse<userSchema>> loginUser(@Valid @RequestBody userSchema user) {
        try {
            // Authenticate user from UserService with username (mobileNumber) and password
            userSchema authenticatedUser = userService.authenticateUser(user.getMobileNumber(), user.getPassword());
            // For security, clear password before returning user data
            authenticatedUser.setPassword(null);

            // Generate JWT token with user info and role "ROLE_USER"
            String token = jwtUtil.generateToken(authenticatedUser.getMobileNumber()); // Using existing generateToken, should accept email or mobile identifier if desired

            // Respond with AuthResponse containing success flag, message, user info, and token
            return ResponseEntity.ok(new AuthResponse<>(true, "Login successful!", authenticatedUser, token));

        } catch (Exception e) {
            // Login failure, return unauthorized with false flag and null data/token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse<>(false, "Login failed. Invalid credentials.", null, null));
        }
    }

    // --- Update User Details ---
    @PutMapping("/update-details")
    public ResponseEntity<ApiResponse<userSchema>> updateUserDetail(@Valid @RequestBody userSchema userDetails) {
        try {
            userSchema updatedUser = userService.updateUserDetail(userDetails);
            ApiResponse<userSchema> response = new ApiResponse<>(
                    true,
                    "User details updated successfully.",
                    updatedUser
            );
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            ApiResponse<userSchema> errorResponse = new ApiResponse<>(false, "Server Error: Could not update user details.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // --- Add Address to User ---
    @PostMapping("/{userId}/address")
    public ResponseEntity<ApiResponse<userSchema>> addAddressToUser(
            @PathVariable String userId,
            @Valid @RequestBody Address newAddress) {
        try {
            userSchema updatedUser = userService.addAddressToUser(userId, newAddress);
            ApiResponse<userSchema> response = new ApiResponse<>(
                    true,
                    "Address added successfully.",
                    updatedUser
            );
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            ApiResponse<userSchema> errorResponse = new ApiResponse<>(false, "Server Error: Could not add address.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // --- Get All Addresses for a User ---
    @GetMapping("/address/{userId}")
    public ResponseEntity<ApiResponse<List<Address>>> checkUserAddress(@PathVariable String userId) {
        try {
            List<Address> addresses = userService.checkUserAddress(userId);
            ApiResponse<List<Address>> response = new ApiResponse<>(
                    true,
                    "Addresses retrieved successfully.",
                    addresses // ✅ Correctly pass the list of addresses here
            );
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            ApiResponse<List<Address>> errorResponse = new ApiResponse<>(false, "Server Error: Could not retrieve addresses.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    // --- Update Specific Address for a User ---
    @PutMapping("/{userId}/address/{addressId}")
    public ResponseEntity<ApiResponse<Address>> updateUserAddress(
            @PathVariable String userId,
            @PathVariable String addressId,
            @Valid @RequestBody Address updatedAddressDetails) {
        try {
            Address updatedAddress = userService.updateUserAddress(userId, addressId, updatedAddressDetails);
            ApiResponse<Address> response = new ApiResponse<>(
                    true,
                    "Address updated successfully.",
                    updatedAddress
            );
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            ApiResponse<Address> errorResponse = new ApiResponse<>(false, "Server Error: Could not update address.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
