package com.example.demo.customer.controller;

import com.example.demo.customer.entity.AppRole;
import com.example.demo.customer.repository.AppRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class AppRoleController {

    @Autowired
    private AppRoleRepository appRoleRepository;

    @GetMapping
    public ResponseEntity<List<AppRole>> getAll() {
        return ResponseEntity.ok(appRoleRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AppRole role) {
        if (appRoleRepository.existsByRoleName(role.getRoleName())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Role '" + role.getRoleName() + "' already exists"));
        }
        return new ResponseEntity<>(appRoleRepository.save(role), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AppRole role) {
        return appRoleRepository.findById(id).map(existing -> {
            existing.setRoleName(role.getRoleName());
            existing.setDescription(role.getDescription());
            return ResponseEntity.ok(appRoleRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!appRoleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        appRoleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}