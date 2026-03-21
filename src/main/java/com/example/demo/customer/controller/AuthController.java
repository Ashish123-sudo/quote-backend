package com.example.demo.customer.controller;

import com.example.demo.customer.entity.AppUser;
import com.example.demo.customer.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/hash")
    public String hash(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");
            String password = payload.get("password");

            System.out.println(">>> Login attempt: " + username);
            System.out.println(">>> Exact password received: [" + password + "]");
            System.out.println(">>> Password length: " + password.length());

            List<AppUser> users = appUserRepository.findByUsername(username);
            System.out.println(">>> Users found: " + users.size());

            if (users.isEmpty()) {
                System.out.println(">>> ❌ No user found");
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }

            AppUser user = users.get(0);
            System.out.println(">>> Active: " + user.getIsActive());
            System.out.println(">>> Stored hash: " + user.getPassword());

            if (user.getIsActive() != null && !user.getIsActive()) {
                System.out.println(">>> ❌ User inactive");
                return ResponseEntity.status(401).body(Map.of("error", "Account is inactive"));
            }

            boolean match = passwordEncoder.matches(password, user.getPassword());
            System.out.println(">>> Password match: " + match);

            if (!match) {
                System.out.println(">>> ❌ Password mismatch");
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }

            System.out.println(">>> ✅ Login successful for: " + username);
            return ResponseEntity.ok(Map.of(
                    "userId", user.getUserId().toString(),
                    "orgId", user.getOrgId().toString(),
                    "fullName", user.getFullName(),
                    "username", user.getUsername(),
                    "email", user.getEmail() != null ? user.getEmail() : "",
                    "role", user.getAppRole() != null ? user.getAppRole().getRoleName() : "USER"
            ));

        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Login failed"));
        }
    }
}