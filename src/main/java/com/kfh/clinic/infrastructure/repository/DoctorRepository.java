package com.kfh.clinic.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kfh.clinic.infrastructure.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}

