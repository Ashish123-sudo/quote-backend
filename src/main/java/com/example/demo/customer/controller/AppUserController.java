package com.example.demo.customer.controller;

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

    @GetMapping
    public ResponseEntity<List<AppUser>> getAll() {
        return ResponseEntity.ok(appUserRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        if (appUserRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username '" + username + "' already exists"));
        }

        AppUser user = new AppUser();
        user.setFullName((String) payload.get("fullName"));
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode((String) payload.get("password")));

        Object roleIdObj = payload.get("roleId");
        if (roleIdObj != null) {
            Long roleId = Long.valueOf(roleIdObj.toString());
            appRoleRepository.findById(roleId).ifPresent(user::setAppRole);
        }

        return new ResponseEntity<>(appUserRepository.save(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        return appUserRepository.findById(id).map(existing -> {
            existing.setFullName((String) payload.get("fullName"));

            String newPassword = (String) payload.get("password");
            if (newPassword != null && !newPassword.isBlank()) {
                existing.setPassword(passwordEncoder.encode(newPassword));
            }

            Object roleIdObj = payload.get("roleId");
            if (roleIdObj != null) {
                Long roleId = Long.valueOf(roleIdObj.toString());
                appRoleRepository.findById(roleId).ifPresent(existing::setAppRole);
            }

            return ResponseEntity.ok(appUserRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!appUserRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        appUserRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}