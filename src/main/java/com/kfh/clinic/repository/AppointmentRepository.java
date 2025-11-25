package com.kfh.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kfh.clinic.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}

