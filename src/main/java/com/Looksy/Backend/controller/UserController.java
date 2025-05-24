package com.Looksy.Backend.controller;


import com.Looksy.Backend.dto.OtpRequest;
import com.Looksy.Backend.dto.OtpVerificationRequest;
import com.Looksy.Backend.exception.ResourceNotFoundException;
import com.Looksy.Backend.exception.SmsServiceException;
import com.Looksy.Backend.model.Address;
import com.Looksy.Backend.model.userSchema;
import com.Looksy.Backend.service.SmsService;
import com.Looksy.Backend.service.UserService;
import com.Looksy.Backend.dto.ApiResponse;
import com.Looksy.Backend.util.OTP.OtpCache;
import com.Looksy.Backend.util.OTP.OtpGenerator;
import com.Looksy.Backend.util.OTP.OtpRateLimiter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus; // Import for HttpStatus
import org.springframework.http.ResponseEntity; // Import for ResponseEntity
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;
    private final OtpCache otpCache;
    private final OtpRateLimiter otpRateLimiter;
    private final SmsService smsService;

    public UserController(UserService userService, OtpCache otpCache, OtpRateLimiter otpRateLimiter, SmsService smsService) {
        this.userService = userService;
        this.otpCache = otpCache;
        this.otpRateLimiter = otpRateLimiter;
        this.smsService = smsService;
    }


//    @PostMapping("/register")
//    public ResponseEntity<ApiResponse<userSchema>> createUser(@Valid @RequestBody String mobileNumber) {
//        userSchema createdUser = userService.createUser(mobileNumber);
//        ApiResponse<userSchema> response = new ApiResponse<>(
//                true,
//                "User created successfully!",
//                createdUser
//        );
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }


    // --- New Endpoint: Request OTP for Registration ---
    // --- New Endpoint: Request OTP for Registration ---
    @PostMapping("/register/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestRegistrationOtp(@Valid @RequestBody OtpRequest otpRequest) {
        String mobileNumber = otpRequest.getMobileNumber();

        // 1. Check if user with this mobile number already exists and is verified
        if (userService.checkUserAlreadyRegistered(mobileNumber)) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "User with this mobile number already registered. Please login.", null),
                    HttpStatus.CONFLICT
            );
        }

        // 2. Apply rate limiting
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

        // 5. CRITICAL: Store the initial unverified user data in the cache
        // Assuming userSchema has a constructor or setters to set mobileNumber and an initial status
        userSchema unverifiedUser = new userSchema();
        unverifiedUser.setMobileNumber(mobileNumber);
        // You might also set other fields if they were part of the initial request,
        // or set a status like UserStatus.UNVERIFIED
        // unverifiedUser.setStatus(UserStatus.UNVERIFIED); // If you have a status field

        otpCache.putUnverifiedUser(mobileNumber, unverifiedUser); // <--- ADD THIS LINE

        // 6. Send OTP via SMS
        try {
            smsService.sendSms(mobileNumber, otp);
        } catch (SmsServiceException e) {
            // Log the actual exception for debugging in production
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

    // --- New Endpoint: Verify OTP and Complete Registration ---
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
                    HttpStatus.UNAUTHORIZED // Or BAD_REQUEST
            );
        }

        // 3. OTP is correct: Invalidate OTP from cache
        otpCache.invalidateOtp(mobileNumber);

        // 4. Create the user (This is where the actual user creation happens)
                // Inside verifyRegistrationOtp
        // ...
                userSchema unverifiedUser = otpCache.getUnverifiedUser(mobileNumber); // <--- THIS LINE IS THE CULPRIT

                if (unverifiedUser == null) {
                    return new ResponseEntity<>(
                            new ApiResponse<>(false, "User details not found for verification. Please restart registration.", null),
                            HttpStatus.BAD_REQUEST
                    );
                }
        // ...

        // Create the user using the details from the cache
        userSchema createdUser = userService.createUser(unverifiedUser); // Your existing createUser logic
        otpCache.invalidateUnverifiedUser(mobileNumber); // Invalidate the unverified user details after successful creation

        ApiResponse<userSchema> response = new ApiResponse<>(
                true,
                "Account created and verified successfully!",
                createdUser
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


//    @PostMapping("/register")
//    public ResponseEntity<ApiResponse<userSchema>> createUser(@Valid @RequestBody userSchema user) {
//        userSchema createdUser = userService.createUser(user);
//        ApiResponse<userSchema> response = new ApiResponse<>(
//                true,
//                "User created successfully!",
//                createdUser
//        );
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<userSchema>> loginUser(@Valid @RequestBody userSchema user) {
        try {
            userSchema authenticatedUser = userService.authenticateUser(user.getMobileNumber(), user.getPassword());

            // IMPORTANT: Remove sensitive data like password before sending response
            authenticatedUser.setPassword(null);

            ApiResponse<userSchema> response = new ApiResponse<>(
                    true,
                    "Login successful!",
                    authenticatedUser
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            // User not found with the provided mobile number
            ApiResponse<userSchema> errorResponse = new ApiResponse<>(
                    false,
                    "Invalid credentials: User not found with this mobile number.",
                    null
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            // This exception could be thrown by userService for incorrect password
            ApiResponse<userSchema> errorResponse = new ApiResponse<>(
                    false,
                    "Invalid credentials: " + e.getMessage(), // e.g., "Incorrect password"
                    null
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            ApiResponse<userSchema> errorResponse = new ApiResponse<>(
                    false,
                    "Server Error: Could not process login.",
                    null
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
            // Let Spring's @ResponseStatus handle the 404
            throw e;
        } catch (Exception e) {
            ApiResponse<userSchema> errorResponse = new ApiResponse<>(false, "Server Error: Could not update user details.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // --- 3. Add Address to User ---
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
            return ResponseEntity.ok(response); // Returns 200 OK
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            // Log the error
            ApiResponse<userSchema> errorResponse = new ApiResponse<>(false, "Server Error: Could not add address.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // --- 4. Check User Addresses (Get All Addresses for a User)
    @GetMapping("/address/{userId}") // Specific path for getting addresses
    public ResponseEntity<ApiResponse<List<Address>>> checkUserAddress(@PathVariable String userId) {
        try {
            List<Address> addresses = userService.checkUserAddress(userId);
            ApiResponse<List<Address>> response = new ApiResponse<>(
                    true,
                    "Addresses retrieved successfully.",
                    addresses
            );
            return ResponseEntity.ok(response); // Returns 200 OK (even if list is empty)
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            // Log the error
            ApiResponse<List<Address>> errorResponse = new ApiResponse<>(false, "Server Error: Could not retrieve addresses.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // --- 5. Update User Address (Specific Address) ---
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
            return ResponseEntity.ok(response); // HTTP 200
        } catch (ResourceNotFoundException e) {
            throw e; // You can customize the exception handler globally
        } catch (Exception e) {
            ApiResponse<Address> errorResponse = new ApiResponse<>(false, "Server Error: Could not update address.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


}