package com.kfh.clinic.application.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kfh.clinic.application.dto.DoctorDTO;
import com.kfh.clinic.application.exception.ResourceNotFoundException;
import com.kfh.clinic.application.mapper.DoctorEntityMapper;
import com.kfh.clinic.infrastructure.entity.Doctor;
import com.kfh.clinic.infrastructure.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

	private final DoctorRepository doctorRepository;
	private final DoctorEntityMapper doctorEntityMapper;

	@Async
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "doctors")
	public CompletableFuture<List<DoctorDTO>> getDoctorsAsync() {
		log.debug("Fetching doctors list");
		List<DoctorDTO> doctors = doctorRepository.findAll()
				.stream()
				.map(doctorEntityMapper::toDto)
				.toList();
		return CompletableFuture.completedFuture(doctors);
	}

	@Transactional(readOnly = true)
	public Doctor getDoctorById(Long doctorId) {
		return doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id " + doctorId));
	}
}

