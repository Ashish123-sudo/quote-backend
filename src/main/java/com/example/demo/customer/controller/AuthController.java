package com.example.demo.customer.controller;

import com.example.demo.customer.config.JwtUtil;
import com.example.demo.customer.entity.AppRole;
import com.example.demo.customer.entity.AppUser;
import com.example.demo.customer.entity.UserOrgAccess;
import com.example.demo.customer.repository.AppRoleRepository;
import com.example.demo.customer.repository.AppUserRepository;
import com.example.demo.customer.repository.UserOrgAccessRepository;
import com.example.demo.organization.entity.Organization;
import com.example.demo.organization.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AppUserRepository appUserRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserOrgAccessRepository userOrgAccessRepository;
    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private AppRoleRepository appRoleRepository;

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

            AppUser user = appUserRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }

            if (user.getIsActive() != null && !user.getIsActive()) {
                return ResponseEntity.status(401).body(Map.of("error", "Account is inactive"));
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }

            // Find all orgs this user has access to
            List<UserOrgAccess> orgAccesses = userOrgAccessRepository
                    .findByUserIdAndIsActiveTrue(user.getUserId());

            // Build org list
            List<Map<String, Object>> orgList = new ArrayList<>();

            // Always include the user's primary org
            organizationRepository.findById(user.getOrgId()).ifPresent(org -> {
                Map<String, Object> orgMap = new HashMap<>();
                orgMap.put("orgId", org.getOrgId().toString());
                orgMap.put("orgName", org.getOrgName());
                orgMap.put("orgCode", org.getOrgCode());
                orgMap.put("role", user.getAppRole() != null ? user.getAppRole().getRoleName() : "USER");
                orgMap.put("isPrimary", true);
                orgList.add(orgMap);
            });

            // Add additional orgs from user_org_access
            for (UserOrgAccess access : orgAccesses) {
                // Skip if already added as primary
                if (access.getOrgId().equals(user.getOrgId())) continue;

                organizationRepository.findById(access.getOrgId()).ifPresent(org -> {
                    Map<String, Object> orgMap = new HashMap<>();
                    orgMap.put("orgId", org.getOrgId().toString());
                    orgMap.put("orgName", org.getOrgName());
                    orgMap.put("orgCode", org.getOrgCode());

                    // Get role for this org access
                    String roleName = "USER";
                    if (access.getRoleId() != null) {
                        roleName = appRoleRepository.findById(access.getRoleId())
                                .map(AppRole::getRoleName)
                                .orElse("USER");
                    }
                    orgMap.put("role", roleName);
                    orgMap.put("isPrimary", false);
                    orgList.add(orgMap);
                });
            }

            System.out.println(">>> ✅ Login successful for: " + username + ", orgs: " + orgList.size());

            // If user has access to only 1 org — log straight in
            if (orgList.size() == 1) {
                Map<String, Object> singleOrg = orgList.get(0);
                UUID orgId = UUID.fromString(singleOrg.get("orgId").toString());
                String token = jwtUtil.generateToken(user.getUserId(), orgId, user.getUsername());

                return ResponseEntity.ok(Map.of(
                        "requiresOrgSelection", false,
                        "token",    token,
                        "userId",   user.getUserId().toString(),
                        "orgId",    orgId.toString(),
                        "fullName", user.getFullName(),
                        "username", user.getUsername(),
                        "email",    user.getEmail() != null ? user.getEmail() : "",
                        "role",     singleOrg.get("role")
                ));
            }

            // Multiple orgs — return list for org picker
            return ResponseEntity.ok(Map.of(
                    "requiresOrgSelection", true,
                    "userId",   user.getUserId().toString(),
                    "fullName", user.getFullName(),
                    "username", user.getUsername(),
                    "email",    user.getEmail() != null ? user.getEmail() : "",
                    "orgs",     orgList
            ));

        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Login failed"));
        }
    }

    @PostMapping("/select-org")
    public ResponseEntity<?> selectOrg(@RequestBody Map<String, String> payload) {
        try {
            String userId   = payload.get("userId");
            String orgId    = payload.get("orgId");

            UUID userUUID = UUID.fromString(userId);
            UUID orgUUID  = UUID.fromString(orgId);

            AppUser user = appUserRepository.findById(userUUID)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify user has access to this org
            boolean hasAccess = orgUUID.equals(user.getOrgId()) ||
                    userOrgAccessRepository.existsByUserIdAndOrgIdAndIsActiveTrue(userUUID, orgUUID);

            if (!hasAccess) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied to this organization"));
            }

            // Get role for this org
            String roleName = "USER";
            if (orgUUID.equals(user.getOrgId())) {
                roleName = user.getAppRole() != null ? user.getAppRole().getRoleName() : "USER";
            } else {
                Optional<UserOrgAccess> access = userOrgAccessRepository
                        .findByUserIdAndOrgId(userUUID, orgUUID);
                if (access.isPresent() && access.get().getRoleId() != null) {
                    roleName = appRoleRepository.findById(access.get().getRoleId())
                            .map(AppRole::getRoleName)
                            .orElse("USER");
                }
            }

            Organization org = organizationRepository.findById(orgUUID)
                    .orElseThrow(() -> new RuntimeException("Organization not found"));

            String token = jwtUtil.generateToken(userUUID, orgUUID, user.getUsername());

            return ResponseEntity.ok(Map.of(
                    "token",    token,
                    "userId",   user.getUserId().toString(),
                    "orgId",    orgUUID.toString(),
                    "fullName", user.getFullName(),
                    "username", user.getUsername(),
                    "email",    user.getEmail() != null ? user.getEmail() : "",
                    "role",     roleName
            ));

        } catch (Exception e) {
            System.err.println("❌ Select org error: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to select organization"));
        }
    }
}