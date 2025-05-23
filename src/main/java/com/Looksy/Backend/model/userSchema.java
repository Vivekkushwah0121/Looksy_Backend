// src/main/java/com/Looksy/Backend/model/userSchema.java
package com.Looksy.Backend.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id; // Import for @Id
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "userSchema")
public class userSchema {

    @Id
    private String id;

    @NotBlank(message = "Mobile is required")
    @Size(min = 10, max = 10, message = "Enter valid 10 Digits")
    @Indexed(unique = true)
    private String mobileNumber;

    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private String password;

    @Valid
    private List<Address> addresses;

    public userSchema() {
        this.addresses = new ArrayList<>();
    }
//    private List<ObjectId> addressIds = new ArrayList<>();
}