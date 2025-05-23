package com.Looksy.Backend.service.impl;


import com.Looksy.Backend.exception.ResourceNotFoundException;
import com.Looksy.Backend.exception.UserAlreadyExistsException;
import com.Looksy.Backend.model.Address;
import com.Looksy.Backend.model.userSchema;
import com.Looksy.Backend.repository.UserRepository;
import com.Looksy.Backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class userServiceImpl implements UserService {
    private final UserRepository userRepository;

    public userServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @Override
//    public userSchema createUser(String mobileNumber){
//        userSchema newUser = new userSchema();
//        newUser.setMobileNumber(mobileNumber);
//        return userRepository.save(newUser);
//    }

    @Override
    public userSchema createUser(userSchema user) {
        String mobileNumber = user.getMobileNumber();

        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number cannot be null or empty.");
        }

        Optional<userSchema> existingUser = userRepository.findByMobileNumber(mobileNumber);

        if (existingUser.isPresent()) {
            // Throw your custom exception here
            throw new UserAlreadyExistsException("This mobile number is already registered.");
        }

        if (user.getAddresses() == null) {
            user.setAddresses(new java.util.ArrayList<>());
        }

        return userRepository.save(user);
    }

    public userSchema authenticateUser(String mobileNumber, String password) {
        // 1. Find user by mobile number
        Optional<userSchema> userOptional = userRepository.findByMobileNumber(mobileNumber); // Assuming findByMobileNumber method in your repository

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with mobile number: " + mobileNumber);
        }

        userSchema user = userOptional.get();

        if (!user.getPassword().equals(password)) { // DANGER: This is INSECURE for production!
            throw new IllegalArgumentException("Incorrect password.");
        }


        // If both mobile number found and password matches, return the user
        return user;
    }

    @Override
    public userSchema updateUserDetail(userSchema userDetails) {
        String currentMobileNumber = userDetails.getMobileNumber();

        // 1. Validate if mobile number for lookup is provided
        if (currentMobileNumber == null || currentMobileNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number must be provided to update user details.");
        }

        // 2. Find the user by their current mobile number
        userSchema userToUpdate = userRepository.findByMobileNumber(currentMobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with mobile number: " + currentMobileNumber));

        // 3. Update the fields on the found user object, only if the new value is provided (not null)
        if (userDetails.getFirstName() != null) {
            userToUpdate.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            userToUpdate.setLastName(userDetails.getLastName());
        }
        if (userDetails.getEmail() != null) {
            userToUpdate.setEmail(userDetails.getEmail());
        }
        if (userDetails.getDob() != null) {
            userToUpdate.setDob(userDetails.getDob());
        }
        if (userDetails.getGender() != null) {
            userToUpdate.setGender(userDetails.getGender());
        }

        if (userDetails.getPassword() != null) {
            userToUpdate.setPassword(userDetails.getPassword());
        }

        if (userDetails.getMobileNumber() != null && !userDetails.getMobileNumber().equals(userToUpdate.getMobileNumber())) {
            Optional<userSchema> userWithNewMobileNumber = userRepository.findByMobileNumber(userDetails.getMobileNumber());
            // Check if the new mobile number exists AND belongs to a *different* user
            if (userWithNewMobileNumber.isPresent() && !userWithNewMobileNumber.get().getId().equals(userToUpdate.getId())) {
                throw new UserAlreadyExistsException("The new mobile number is already registered by another user.");
            }
            userToUpdate.setMobileNumber(userDetails.getMobileNumber()); // Update the mobile number
        }


        // 4. Save the updated user object back to the repository
        return userRepository.save(userToUpdate);
    }

    @Override
    public userSchema addAddressToUser(String userId, Address newAddress) {
        // Find the user by ID
        userSchema user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Add the new address to the existing list
        user.getAddresses().add(newAddress);

        // Save the updated user object back to the database
        return userRepository.save(user);
    }

    @Override
    public List<Address> checkUserAddress(String userId){
        userSchema user = userRepository.findById(userId)
                .orElseThrow(()->  new ResourceNotFoundException("User address not found "+userId));

        return user.getAddresses();

    }

    @Override
    public Address updateUserAddress(String userId, String addressId, Address updatedAddressDetails) {
        // 1. Find the user by userId
        userSchema user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // 2. Get the list of addresses for the user
        List<Address> addresses = user.getAddresses();

        // 3. Find the specific address to update within the list using a stream
        Optional<Address> addressToUpdateOptional = addresses.stream()
                .filter(address -> address.getId() != null && address.getId().equals(addressId))
                .findFirst();

        // If the address with the given addressId is not found in the user's addresses
        Address addressToUpdate = addressToUpdateOptional.orElseThrow(
                () -> new ResourceNotFoundException("Address not found with ID: " + addressId + " for user ID: " + userId));

        // 4. Update the fields of the found address object
        // Only update if the new value is provided (not null), allowing for partial updates
        if (updatedAddressDetails.getStreet() != null) {
            addressToUpdate.setStreet(updatedAddressDetails.getStreet());
        }
        if (updatedAddressDetails.getTown() != null) {
            addressToUpdate.setTown(updatedAddressDetails.getTown());
        }
        if (updatedAddressDetails.getCity() != null) {
            addressToUpdate.setCity(updatedAddressDetails.getCity());
        }
        if (updatedAddressDetails.getState() != null) {
            addressToUpdate.setState(updatedAddressDetails.getState());
        }
        if (updatedAddressDetails.getPinCode() != null) {
            addressToUpdate.setPinCode(updatedAddressDetails.getPinCode());
        }
        if (updatedAddressDetails.getCountry() != null) {
            addressToUpdate.setCountry(updatedAddressDetails.getCountry());
        }

        // 5. Save the entire user object. Spring Data MongoDB will automatically
        // persist the changes to the embedded list.

        userRepository.save(user);

        return addressToUpdate; // i want return that address only
    }

}


