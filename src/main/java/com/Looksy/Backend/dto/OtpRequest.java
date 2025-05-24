package com.Looksy.Backend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import lombok.Data; // Assuming you use Lombok

@Data // For getters, setters, toString, equals, hashCode
public class OtpRequest {
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Mobile number must be in E.164 format (e.g., +919876543210)")
    private String mobileNumber;
    // Add other relevant user registration data if needed, e.g., name, email
    // private String name;
    // @Email private String email;
}