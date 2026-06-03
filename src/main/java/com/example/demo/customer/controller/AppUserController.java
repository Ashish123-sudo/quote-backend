package com.example.demo.customer.controller;

import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.entity.AppUser;
import com.example.demo.customer.repository.AppRoleRepository;
import com.example.demo.customer.repository.AppUserRepository;
import com.example.demo.customer.service.SuperAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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

    private static final UUID SUPER_ADMIN_ORG_ID =
            UUID.fromString("10000000-0000-0000-0000-000000000001");

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityHelper securityHelper;
    private final SuperAdminService superAdminService;

    public AppUserController(AppUserRepository appUserRepository,
                             AppRoleRepository appRoleRepository,
                             PasswordEncoder passwordEncoder,
                             SecurityHelper securityHelper,
                             SuperAdminService superAdminService) {
        this.appUserRepository  = appUserRepository;
        this.appRoleRepository  = appRoleRepository;
        this.passwordEncoder    = passwordEncoder;
        this.securityHelper     = securityHelper;
        this.superAdminService  = superAdminService;
    }

    private boolean isSuperAdmin(UUID orgId) {
        return SUPER_ADMIN_ORG_ID.equals(orgId);
    }

    @GetMapping
    public ResponseEntity<List<AppUser>> getAll() {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            List<AppUser> users = isSuperAdmin(orgId)
                    ? appUserRepository.findAll()
                    : appUserRepository.findByOrgId(orgId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("❌ Error fetching users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @Transactional
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        try {
            UUID currentOrgId = securityHelper.getCurrentOrgId();
            UUID userId       = securityHelper.getCurrentUserId();

            UUID targetOrgId = currentOrgId;
            Object orgIdObj = payload.get("orgId");
            if (orgIdObj != null && !orgIdObj.toString().isBlank()) {
                targetOrgId = UUID.fromString(orgIdObj.toString());
            }

            String username = (String) payload.get("username");
            if (appUserRepository.existsByUsernameAndOrgId(username, targetOrgId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username '" + username + "' already exists"));
            }

            superAdminService.seedRolesIfMissing(targetOrgId, userId);

            AppUser user = new AppUser();
            user.setOrgId(targetOrgId);
            user.setFullName((String) payload.get("fullName"));
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode((String) payload.get("password")));
            user.setIsActive(true);

            if (payload.containsKey("email")) {
                user.setEmail((String) payload.get("email"));
            }

            Object roleIdObj = payload.get("roleId");
            if (roleIdObj != null && !roleIdObj.toString().isBlank()) {
                UUID roleId = UUID.fromString(roleIdObj.toString());
                appRoleRepository.findByRoleIdAndOrgId(roleId, targetOrgId)
                        .ifPresent(user::setAppRole);
            } else {
                appRoleRepository.findByRoleNameAndOrgId("Administrator", targetOrgId)
                        .ifPresent(user::setAppRole);
            }

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
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @RequestBody Map<String, Object> payload) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            AppUser existing = isSuperAdmin(orgId)
                    ? appUserRepository.findById(id).orElse(null)
                    : appUserRepository.findByUserIdAndOrgId(id, orgId).orElse(null);

            if (existing == null) return ResponseEntity.notFound().build();

            existing.setFullName((String) payload.get("fullName"));

            if (payload.containsKey("email")) {
                existing.setEmail((String) payload.get("email"));
            }

            String newPassword = (String) payload.get("password");
            if (newPassword != null && !newPassword.isBlank()) {
                existing.setPassword(passwordEncoder.encode(newPassword));
            }

            Object roleIdObj = payload.get("roleId");
            if (roleIdObj != null && !roleIdObj.toString().isBlank()) {
                UUID roleId = UUID.fromString(roleIdObj.toString());
                appRoleRepository.findByRoleIdAndOrgId(roleId, existing.getOrgId())
                        .ifPresent(existing::setAppRole);
            }

            if (payload.containsKey("isActive")) {
                existing.setIsActive((Boolean) payload.get("isActive"));
            }

            existing.setUpdatedBy(userId);
            AppUser updated = appUserRepository.save(existing);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            System.err.println("❌ Error updating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();

            AppUser user = isSuperAdmin(orgId)
                    ? appUserRepository.findById(id).orElse(null)
                    : appUserRepository.findByUserIdAndOrgId(id, orgId).orElse(null);

            if (user == null) return ResponseEntity.notFound().build();

            appUserRepository.delete(user);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}