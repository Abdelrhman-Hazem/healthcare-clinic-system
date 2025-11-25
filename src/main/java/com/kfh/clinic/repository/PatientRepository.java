package com.kfh.clinic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kfh.clinic.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

	Optional<Patient> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByNationalId(String nationalId);

	@EntityGraph(attributePaths = { "appointments", "appointments.doctor" })
	List<Patient> findAllByOrderByFullNameEnAsc();
}

