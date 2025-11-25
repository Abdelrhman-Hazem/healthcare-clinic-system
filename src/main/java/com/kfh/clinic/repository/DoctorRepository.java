package com.kfh.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kfh.clinic.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}

