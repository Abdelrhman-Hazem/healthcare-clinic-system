package com.kfh.clinic.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
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

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "doctors", key = "#root.method.name")
	public List<DoctorDTO> getDoctors() {
		log.debug("Fetching doctors list from database");
		List<DoctorDTO> doctors = doctorRepository.findAll()
				.stream()
				.map(doctorEntityMapper::toDto)
				.collect(Collectors.toList());
		return doctors;
	}

	@Transactional(readOnly = true)
	public Doctor getDoctorById(Long doctorId) {
		return doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id " + doctorId));
	}
}

