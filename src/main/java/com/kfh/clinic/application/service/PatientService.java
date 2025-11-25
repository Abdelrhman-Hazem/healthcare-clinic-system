package com.kfh.clinic.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kfh.clinic.application.dto.PatientDTO;
import com.kfh.clinic.application.exception.DuplicateResourceException;
import com.kfh.clinic.application.exception.ResourceNotFoundException;
import com.kfh.clinic.application.mapper.PatientEntityMapper;
import com.kfh.clinic.infrastructure.entity.Patient;
import com.kfh.clinic.infrastructure.repository.PatientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

	private final PatientRepository patientRepository;
	private final PatientEntityMapper patientEntityMapper;

	@Transactional
	public PatientDTO register(PatientDTO dto) {
		validateUniqueness(dto);
		log.info("Registering patient {}", dto.getEmail());
		dto.setEmail(dto.getEmail().toLowerCase());
		dto.setActive(true);
		Patient patient = patientEntityMapper.toEntity(dto);
		Patient saved = patientRepository.save(patient);
		return patientEntityMapper.toDto(saved);
	}

	@Transactional(readOnly = true)
	public List<PatientDTO> getAllPatientsWithAppointments() {
		return patientRepository.findAllByOrderByFullNameEnAsc()
				.stream()
				.map(patientEntityMapper::toDto)
				.toList();
	}

	@Transactional
	public void softDeletePatient(Long patientId) {
		Patient patient = patientRepository.findById(patientId)
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + patientId));
		patient.setActive(false);
		log.warn("Soft deleting patient {} - {}", patientId, patient.getEmail());
		patientRepository.save(patient);
	}

	private void validateUniqueness(PatientDTO dto) {
		if (patientRepository.existsByEmail(dto.getEmail().toLowerCase())) {
			throw new DuplicateResourceException("Email already registered");
		}
		if (patientRepository.existsByNationalId(dto.getNationalId())) {
			throw new DuplicateResourceException("National ID already registered");
		}
	}
}

