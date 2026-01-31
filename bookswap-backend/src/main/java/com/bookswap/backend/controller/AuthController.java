package com.bookswap.backend.controller;

import com.bookswap.backend.dto.auth.AuthResponse;
import com.bookswap.backend.dto.auth.LoginRequest;
import com.bookswap.backend.dto.auth.RegisterRequest;
import com.bookswap.backend.entity.UserEntity;
import com.bookswap.backend.repository.UserRepository;
import com.bookswap.backend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final long EXP_24H = 24L * 60 * 60 * 1000; // 24 ore

    public AuthController(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already used");
        }

        UserEntity u = new UserEntity();
        u.setEmail(req.getEmail());
        u.setUsername(req.getUsername());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        u = userRepo.save(u);

        String token = jwtService.generateToken(String.valueOf(u.getId()), EXP_24H);
        return new AuthResponse(token, u.getId(), u.getUsername(), u.getEmail());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        UserEntity u = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(String.valueOf(u.getId()), EXP_24H);
        return new AuthResponse(token, u.getId(), u.getUsername(), u.getEmail());
    }
}
