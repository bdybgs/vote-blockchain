package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.RegistrationRequest;
import org.example.dto.request.LoginRequest;
import org.example.entity.User;
import org.example.security.JwtTokenProvider;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST-контроллер работы с пользователями:
 *   • /registration  — регистрация нового пользователя;
 *   • /authorization — логин по e-mail / паролю → JWT;
 *   • /profile       — данные текущего пользователя;
 *   • /{id}          — данные произвольного пользователя;
 *   • /welcome       — простой публичный энд-пойнт.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider      jwtTokenProvider;
    private final UserService           userService;

    /* ─────────────────────────  REGISTRATION  ───────────────────────── */

    @PostMapping("/registration")
    public ResponseEntity<User> register(@RequestBody @Valid RegistrationRequest req) {

        User created = userService.registerUser(
                req.getEmail(),
                req.getUsername(),
                req.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /* ───────────────────────────  LOGIN  ────────────────────────────── */

    @PostMapping("/authorization")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest credentials) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.email(), credentials.password()));

            SecurityContextHolder.getContext().setAuthentication(auth);

            String token = jwtTokenProvider.generateToken(auth);

            return ResponseEntity.ok(
                    Map.of("token",   token,
                            "message", "User authorized successfully"));
        }
        catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    /* ──────────────────────────  PUBLIC ENDPOINT  ───────────────────── */

    @GetMapping("/welcome")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome to the Blockchain Voter application!");
    }

    /* ───────────────────────────  PROFILE  ──────────────────────────── */

    @GetMapping("/profile")
    public ResponseEntity<User> currentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    /* ───────────────────────  USER BY ID (ADMIN)  ───────────────────── */

    @GetMapping("/{id}")
    public ResponseEntity<User> userById(@PathVariable Long id) {

        User user = userService.getUserById(id);
        return user != null
                ? ResponseEntity.ok(user)
                : ResponseEntity.notFound().build();
    }
}
