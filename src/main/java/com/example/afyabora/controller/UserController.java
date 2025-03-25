package com.example.afyabora.controller;

import com.example.afyabora.model.User;
import com.example.afyabora.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get authenticated user details
    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName(); // Get email (or username) from auth
        User currentUser = userService.findByEmail(email); // Ensure this method exists in UserService

        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("")
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/doctors")
    public ResponseEntity<List<User>> getDoctors() {
        List<User> doctors = userService.getDoctors();
        return ResponseEntity.ok(doctors);
    }
    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAdmins() {
        List<User> admins = userService.getAdmins();
        return ResponseEntity.ok(admins);
    }
    @GetMapping("/patients")
    public ResponseEntity<List<User>> getPatients() {
        List<User> patients = userService.getPatients();
        return ResponseEntity.ok(patients);
    }
    @GetMapping("/doctors/specialty")
    public ResponseEntity<List<User>> getDoctors(@RequestParam(required = false) String specialty) {
        List<User> doctors;
        if (specialty != null) {
            doctors = userService.getDoctorsBySpecialty(specialty);
        } else {
            doctors = userService.getDoctors();
        }
        return ResponseEntity.ok(doctors);
    }
}
