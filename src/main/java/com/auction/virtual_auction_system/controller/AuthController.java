package com.auction.virtual_auction_system.controller;

import com.auction.virtual_auction_system.controller.dto.RegisterRequest;
import com.auction.virtual_auction_system.entity.User;
import com.auction.virtual_auction_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService){
        this.userService = userService;
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
}