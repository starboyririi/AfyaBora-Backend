package com.example.afyabora.repository;

import com.example.afyabora.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Fetch all users with role "Doctor"
    List<User> findByRole(String role);
    List<User> findByRoleAndSpecialty(String role, String specialty);

}
