package com.Looksy.Backend.security;

import com.Looksy.Backend.config.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // Admin & User Public Endpoints
                                "/admin/register",
                                "/admin/check_admin_login",
                                "/admin/cleartoken",

                                "/api/v1/user/register",
                                "/api/v1/user/login",
                                "/api/v1/user/register/request-otp",
                                "/api/v1/user/register/verify-otp",
                                "/api/v1/user/update-details",
                                "/api/v1/fetch_all_product",

                                // Unprotected Color Endpoints
                                "/api/colors/display_all_color",
                                "/api/colors/product-by-id",

                                // Banners
                                "/api/banners/all",

                                // Categories
                                "/category/display_all_category",

                                // Dimensions
                                "/api/dimensions/display_all_dimensions",
                                "/api/dimensions/fetch_all_dimensions",

                                // Products
                                "/api/products/display_all_product",
                                "/api/products/fetch_all_product",
                                "/api/products/by_salestatus",
                                "/api/products/picture",
                                "/api/products/search",
                                "/api/products/sorted-by-price",
                                "/api/products/fetch_by_ids", // Ensure this is included
                                "/api/products/by-size", // Ensure this is included

                                // Subcategories
                                "/subcategory/all",
                                "/subcategory/by-category/**", // Allow all subcategory IDs
                                "/subcategory/by-priority/**",
                                "/api/v1/user/register"
                                // Allow all priority values
                        ).permitAll() // Allow access to the above endpoints without authentication
                        .anyRequest().authenticated() // Require authentication for all other requests
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session management
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
