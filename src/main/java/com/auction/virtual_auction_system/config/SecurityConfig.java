package com.auction.virtual_auction_system.config;
import com.auction.virtual_auction_system.exception.ApiErrorResponseWriter;
import com.auction.virtual_auction_system.security.JwtAuthenticationFilter;
import com.auction.virtual_auction_system.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final UserService userService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserService userService){
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userService = userService;
    }
    /**
     * BCrypt password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    /**
     * Adapts the application's user service to Spring Security's
     * UserDetailsService contract.
     */
    @Bean
    public UserDetailsService userDetailsService() {

        return userService::loadUserByUsername;
    }

    /**
     * Authentication provider tells Spring Security
     * how to find users and verify passwords.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        // Compare passwords using BCrypt
        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }

    /**
     * AuthenticationManager is used during login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    /**
     * Main Spring Security configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider)
            throws Exception {

        http

                // REST API does not use browser sessions
                .csrf(csrf -> csrf.disable())

                // JWT based authentication is stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Return useful JSON responses for API security failures.
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) -> {
                            ApiErrorResponseWriter.write(
                                    request,
                                    response,
                                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                                    "Authentication required");
                        })
                        .accessDeniedHandler((request, response, exception) -> {
                            ApiErrorResponseWriter.write(
                                    request,
                                    response,
                                    org.springframework.http.HttpStatus.FORBIDDEN,
                                    "Access denied");
                        })
                )

                // Configure which endpoints are public
                .authorizeHttpRequests(auth -> auth

                        // Registration and login don't need JWT
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // Test endpoint
                        .requestMatchers(
                                "/api/hello",
                                "/ws",
                                "/ws/**"
                        ).permitAll()

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // Register our authentication provider
                .authenticationProvider(
                        authenticationProvider
                )

                /*
                 * JWT filter must execute before
                 * Spring's username/password filter.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
