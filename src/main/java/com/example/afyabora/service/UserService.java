package com.example.afyabora.service;

import com.example.afyabora.model.User;
import com.example.afyabora.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
    }

    // Get all users
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    // Find user by email
    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }
    public List<User> getDoctors() {
        return userRepository.findByRole("Doctor"); // Ensure this matches your DB value
    }
    public List<User> getAdmins() {
        return userRepository.findByRole("Admin"); // Ensure this matches your DB value
    }
    public List<User> getPatients() {
        return userRepository.findByRole("Patient"); // Ensure this matches your DB value
    }
    public List<User> getDoctorsBySpecialty(String specialty) {
        return userRepository.findByRoleAndSpecialty("DOCTOR", specialty);
    }

}


