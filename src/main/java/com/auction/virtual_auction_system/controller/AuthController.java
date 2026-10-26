package com.auction.virtual_auction_system.controller;

import com.auction.virtual_auction_system.controller.dto.LoginRequest;
import com.auction.virtual_auction_system.controller.dto.LoginResponse;
import com.auction.virtual_auction_system.controller.dto.RegisterRequest;
import com.auction.virtual_auction_system.entity.User;
import com.auction.virtual_auction_system.security.JwtService;
import com.auction.virtual_auction_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService){
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        userService.register(user);

        return ResponseEntity.ok(
                "User registered successfully"
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );
        var userDetails = (org.springframework.security.core.userdetails.User)
                authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);
        User user = userService.findByEmail(request.getEmail());
        return ResponseEntity.ok(
                new LoginResponse(
                        user.getEmail(),
                        token,
                        user.getRole().name()
                )
        );
    }
}