package com.example.afyabora.repository;

import com.example.afyabora.model.Feedback;
import com.example.afyabora.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Page<Feedback> findByDoctor(User doctor, Pageable pageable);
}
