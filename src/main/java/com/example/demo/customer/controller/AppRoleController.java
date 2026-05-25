package com.example.demo.customer.controller;

import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.entity.AppRole;
import com.example.demo.customer.repository.AppRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.customer.config.PlatformConstants;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class AppRoleController {

    @Autowired
    private AppRoleRepository appRoleRepository;

    @Autowired
    private SecurityHelper securityHelper;

    @GetMapping
    public ResponseEntity<List<AppRole>> getAll() {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();

            // Super Admin sees all roles across all orgs
            List<AppRole> roles = PlatformConstants.isPlatformOrg(orgId)
                    ? appRoleRepository.findAll()
                    : appRoleRepository.findByOrgId(orgId);

            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            System.err.println("❌ Error fetching roles: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-org/{orgId}")
    public ResponseEntity<List<AppRole>> getByOrg(@PathVariable UUID orgId) {
        try {
            UUID currentOrgId = securityHelper.getCurrentOrgId();
            // Only platform admin can fetch roles of other orgs
            if (!PlatformConstants.isPlatformOrg(currentOrgId) && !currentOrgId.equals(orgId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            List<AppRole> roles = appRoleRepository.findByOrgId(orgId);
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AppRole role) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            // Check if role name already exists in this org
            if (appRoleRepository.existsByRoleNameAndOrgId(role.getRoleName(), orgId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Role '" + role.getRoleName() + "' already exists"));
            }

            // Set org and audit fields
            role.setOrgId(orgId);
            role.setCreatedBy(userId);
            role.setUpdatedBy(userId);

            AppRole savedRole = appRoleRepository.save(role);
            return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error creating role: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody AppRole role) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            return appRoleRepository.findByRoleIdAndOrgId(id, orgId).map(existing -> {
                existing.setRoleName(role.getRoleName());
                existing.setDescription(role.getDescription());
                existing.setUpdatedBy(userId);

                AppRole updated = appRoleRepository.save(existing);
                return ResponseEntity.ok(updated);
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("❌ Error updating role: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();

            if (!appRoleRepository.existsByRoleIdAndOrgId(id, orgId)) {
                return ResponseEntity.notFound().build();
            }

            AppRole role = appRoleRepository.findByRoleIdAndOrgId(id, orgId)
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            appRoleRepository.delete(role);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("❌ Error deleting role: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}