package com.example.afyabora.repository;

import com.example.afyabora.model.AppointmentStatus;
import com.example.afyabora.model.Appointment;
import com.example.afyabora.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // 🔹 Find all appointments for a specific doctor (using User reference)
    List<Appointment> findByDoctor(User doctor);

    // 🔹 Find all appointments for a specific patient (using User reference)
    List<Appointment> findByPatient(User patient);

    // 🔹 Find appointments by doctor and status
    List<Appointment> findByDoctorAndStatus(User doctor, AppointmentStatus status);

    // 🔹 Find appointments by patient and status
    List<Appointment> findByPatientAndStatus(User patient, AppointmentStatus status);

    Optional<Appointment> findTopByDoctorEmailOrderByAppointmentTimeDesc(String doctorEmail);
}



