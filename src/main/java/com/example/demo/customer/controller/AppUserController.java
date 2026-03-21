package com.example.demo.customer.controller;

import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.entity.AppRole;
import com.example.demo.customer.entity.AppUser;
import com.example.demo.customer.repository.AppRoleRepository;
import com.example.demo.customer.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class AppUserController {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppRoleRepository appRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityHelper securityHelper;

    @GetMapping
    public ResponseEntity<List<AppUser>> getAll() {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            List<AppUser> users = appUserRepository.findByOrgId(orgId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("❌ Error fetching users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            String username = (String) payload.get("username");

            // Check if username already exists in this org
            if (appUserRepository.existsByUsernameAndOrgId(username, orgId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username '" + username + "' already exists"));
            }

            AppUser user = new AppUser();
            user.setOrgId(orgId);
            user.setFullName((String) payload.get("fullName"));
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode((String) payload.get("password")));

            // Set email if provided
            if (payload.containsKey("email")) {
                user.setEmail((String) payload.get("email"));
            }

            // Set role if provided
            Object roleIdObj = payload.get("roleId");
            if (roleIdObj != null) {
                UUID roleId = UUID.fromString(roleIdObj.toString());
                appRoleRepository.findByRoleIdAndOrgId(roleId, orgId)
                        .ifPresent(user::setAppRole);
            }

            // Set audit fields
            user.setCreatedBy(userId);
            user.setUpdatedBy(userId);

            AppUser savedUser = appUserRepository.save(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error creating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            return appUserRepository.findByUserIdAndOrgId(id, orgId).map(existing -> {
                existing.setFullName((String) payload.get("fullName"));

                // Update email if provided
                if (payload.containsKey("email")) {
                    existing.setEmail((String) payload.get("email"));
                }

                // Update password if provided
                String newPassword = (String) payload.get("password");
                if (newPassword != null && !newPassword.isBlank()) {
                    existing.setPassword(passwordEncoder.encode(newPassword));
                }

                // Update role if provided
                Object roleIdObj = payload.get("roleId");
                if (roleIdObj != null) {
                    UUID roleId = UUID.fromString(roleIdObj.toString());
                    appRoleRepository.findByRoleIdAndOrgId(roleId, orgId)
                            .ifPresent(existing::setAppRole);
                }

                // Update active status if provided
                if (payload.containsKey("isActive")) {
                    existing.setIsActive((Boolean) payload.get("isActive"));
                }

                // Set audit field
                existing.setUpdatedBy(userId);

                AppUser updated = appUserRepository.save(existing);
                return ResponseEntity.ok(updated);
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("❌ Error updating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();

            if (!appUserRepository.existsByUserIdAndOrgId(id, orgId)) {
                return ResponseEntity.notFound().build();
            }

            AppUser user = appUserRepository.findByUserIdAndOrgId(id, orgId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            appUserRepository.delete(user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}