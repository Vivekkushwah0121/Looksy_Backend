package com.Looksy.Backend.exception;

// Correct import based on your ApiResponse package
import com.Looksy.Backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody; // <--- MAKE SURE THIS IS IMPORTED
import org.springframework.web.bind.annotation.ResponseStatus; // <--- And this, if you use @ResponseStatus on methods
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler; // <--- Consider extending this for more control

// Option 1: @ControllerAdvice (most common and recommended)
// If you use this, you need @ResponseBody on each @ExceptionHandler method.
@ControllerAdvice
// Option 2: @RestControllerAdvice (if all your controller methods are also @RestController)
// @RestControllerAdvice // This automatically includes @ResponseBody for all methods
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler { // <--- Recommend extending this for better control over default Spring errors

    // Ensure this import matches your project structure for ApiResponse
    // import com.Looksy.Backend.dto.ApiResponse; // Old import, might be different
    // import com.Looksy.Backend.utils.dto.ApiResponse; // New, correct import


    // Handler for your custom UserAlreadyExistsException
    @ExceptionHandler(UserAlreadyExistsException.class)
    // @ResponseStatus(HttpStatus.CONFLICT) // You can use this instead of ResponseEntity if you don't need a custom body
    @ResponseBody // <--- CRITICAL: Ensures the return value is put in the response body
    public ResponseEntity<ApiResponse<?>> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT; // 409 Conflict

        ApiResponse<?> apiResponse = new ApiResponse<>(
                false, // Success will be false for errors
                ex.getMessage(), // The message from your exception ("This mobile number is already registered.")
                null // No data for this type of error
        );

        return new ResponseEntity<>(apiResponse, status);
    }

    // Handler for IllegalArgumentException (e.g., for null/empty mobile number)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody // <--- CRITICAL
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400 Bad Request

        ApiResponse<?> apiResponse = new ApiResponse<>(
                false,
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(apiResponse, status);
    }

    // Handler for ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody // <--- CRITICAL
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404 Not Found

        ApiResponse<?> apiResponse = new ApiResponse<>(
                false,
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(apiResponse, status);
    }


    // Generic handler for any other unhandled exceptions (important for robustness)
    // If you extend ResponseEntityExceptionHandler, this might be handled by super.handleExceptionInternal
    // You can override specific methods from ResponseEntityExceptionHandler if needed.
    @ExceptionHandler(Exception.class)
    @ResponseBody // <--- CRITICAL
    public ResponseEntity<ApiResponse<?>> handleGlobalException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500 Internal Server Error

        ApiResponse<?> apiResponse = new ApiResponse<>(
                false,
                "An unexpected error occurred: " + ex.getMessage(), // Provide a generic message or ex.getMessage()
                null
        );

        // In a real application, you'd log the full stack trace here for debugging.
        // System.err.println("An unhandled exception occurred: " + ex.getMessage());
        // ex.printStackTrace();

        return new ResponseEntity<>(apiResponse, status);
    }

    // Optional: If you want to handle validation errors from @Valid specifically
    /*
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        // Or format it into your ApiResponse
        ApiResponse<?> apiResponse = new ApiResponse<>(
                false,
                "Validation failed",
                errors // Data could be a map of field errors
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
    */
}