package com.kfh.clinic.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kfh.clinic.infrastructure.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}

