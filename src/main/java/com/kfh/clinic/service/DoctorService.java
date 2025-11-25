package com.kfh.clinic.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kfh.clinic.dto.DoctorResponse;
import com.kfh.clinic.entity.Doctor;
import com.kfh.clinic.exception.ResourceNotFoundException;
import com.kfh.clinic.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

	private final DoctorRepository doctorRepository;

	@Async
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "doctors")
	public CompletableFuture<List<DoctorResponse>> getDoctorsAsync() {
		log.debug("Fetching doctors list");
		List<DoctorResponse> doctors = doctorRepository.findAll()
				.stream()
				.map(this::mapToResponse)
				.toList();
		return CompletableFuture.completedFuture(doctors);
	}

	@Transactional(readOnly = true)
	public Doctor getDoctorById(Long doctorId) {
		return doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id " + doctorId));
	}

	private DoctorResponse mapToResponse(Doctor doctor) {
		return new DoctorResponse(doctor.getId(), doctor.getNameEn(), doctor.getNameAr(), doctor.getSpecialty(),
				doctor.getYearsOfExperience(), doctor.getConsultationDurationMinutes());
	}
}

