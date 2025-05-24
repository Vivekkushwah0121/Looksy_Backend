package com.Looksy.Backend.service;

import com.Looksy.Backend.model.Address;
import com.Looksy.Backend.model.userSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface UserService {

//    userSchema createUser(String mobileNumber);

    userSchema createUser(userSchema user);

    boolean checkUserAlreadyRegistered(String mobileNumber);

    userSchema authenticateUser(@NotBlank(message = "Mobile is required") @Size(min = 10, max = 10, message = "Enter valid 10 Digits") String mobileNumber, String password);

    userSchema updateUserDetail(userSchema userDetails);

    userSchema addAddressToUser(String userId, Address newAddress);

    List<Address> checkUserAddress(String userId);

    Address updateUserAddress(String userId, String addressId, Address updatedAddressDetails);

}