package com.kfh.clinic.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kfh.clinic.infrastructure.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

	Optional<Patient> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByNationalId(String nationalId);

	@EntityGraph(attributePaths = { "appointments", "appointments.doctor" })
	List<Patient> findAllByOrderByFullNameEnAsc();
}

