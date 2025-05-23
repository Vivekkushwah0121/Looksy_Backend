package com.Looksy.Backend.controller;


import com.Looksy.Backend.exception.ResourceNotFoundException;
import com.Looksy.Backend.model.Address;
import com.Looksy.Backend.model.userSchema;
import com.Looksy.Backend.service.UserService;
import com.Looksy.Backend.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus; // Import for HttpStatus
import org.springframework.http.ResponseEntity; // Import for ResponseEntity
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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