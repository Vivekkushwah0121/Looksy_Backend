package com.Looksy.Backend.service; // Or com.Looksy.Backend.otp, depending on your structure

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct; // Make sure this import is there

@Service
public class SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    // Use a no-argument constructor. Spring will call this, then inject @Value fields.
    public SmsService() {
        // Twilio.init() is called in @PostConstruct
    }

    // This method runs AFTER all @Value fields are injected by Spring
    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isEmpty() &&
                authToken != null && !authToken.isEmpty()) {
            Twilio.init(accountSid, authToken);
        } else {
            System.err.println("WARNING: Twilio credentials not fully configured. SMS service will be limited.");
        }
    }

    public void sendSms(String toPhoneNumber, String otp) {
        if (accountSid == null || accountSid.isEmpty() ||
                authToken == null || authToken.isEmpty() ||
                twilioPhoneNumber == null || twilioPhoneNumber.isEmpty()) {
            System.err.println("ERROR: Twilio credentials are missing or invalid. SMS cannot be sent.");
            return;
        }

        try {
            Message message = Message.creator(
                            new PhoneNumber(toPhoneNumber),
                            new PhoneNumber(twilioPhoneNumber),
                            "Your Looksy verification code is: " + otp + ". It is valid for 5 minutes. Do not share this code."
                    )
                    .create();
            System.out.println("SMS sent successfully! SID: " + message.getSid());
        } catch (com.twilio.exception.ApiException e) {
            System.err.println("Failed to send SMS to " + toPhoneNumber + " due to Twilio API error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Failed to send SMS to " + toPhoneNumber + " due to unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}