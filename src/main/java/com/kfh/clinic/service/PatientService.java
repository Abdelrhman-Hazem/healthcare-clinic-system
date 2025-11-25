package com.kfh.clinic.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kfh.clinic.dto.AddressRequest;
import com.kfh.clinic.dto.PatientRegistrationRequest;
import com.kfh.clinic.dto.PatientResponse;
import com.kfh.clinic.entity.Address;
import com.kfh.clinic.entity.Patient;
import com.kfh.clinic.exception.DuplicateResourceException;
import com.kfh.clinic.exception.ResourceNotFoundException;
import com.kfh.clinic.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

	private final PatientRepository patientRepository;

	@Transactional
	public PatientResponse register(PatientRegistrationRequest request) {
		validateUniqueness(request);
		log.info("Registering patient {}", request.email());
		Patient patient = Patient.builder()
				.fullNameEn(request.fullNameEn())
				.fullNameAr(request.fullNameAr())
				.email(request.email().toLowerCase())
				.mobileNumber(request.mobileNumber())
				.dateOfBirth(request.dateOfBirth())
				.nationalId(request.nationalId())
				.address(mapAddress(request.address()))
				.build();
		Patient saved = patientRepository.save(patient);
		return mapToResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<PatientResponse> getAllPatientsWithAppointments() {
		return patientRepository.findAllByOrderByFullNameEnAsc()
				.stream()
				.map(this::mapToResponse)
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

	private void validateUniqueness(PatientRegistrationRequest request) {
		if (patientRepository.existsByEmail(request.email().toLowerCase())) {
			throw new DuplicateResourceException("Email already registered");
		}
		if (patientRepository.existsByNationalId(request.nationalId())) {
			throw new DuplicateResourceException("National ID already registered");
		}
	}

	private Address mapAddress(AddressRequest addressRequest) {
		return Address.builder()
				.street(addressRequest.street())
				.city(addressRequest.city())
				.region(addressRequest.region())
				.build();
	}

	private PatientResponse mapToResponse(Patient patient) {
		return new PatientResponse(patient.getId(), patient.getFullNameEn(), patient.getFullNameAr(), patient.getEmail(),
				patient.getMobileNumber(), patient.getDateOfBirth(), patient.getNationalId(),
				patient.getAddress() != null ? patient.getAddress().getStreet() : null,
				patient.getAddress() != null ? patient.getAddress().getCity() : null,
				patient.getAddress() != null ? patient.getAddress().getRegion() : null,
				patient.getAppointments()
						.stream()
						.map(appointment -> new com.kfh.clinic.dto.AppointmentResponse(appointment.getId(),
								appointment.getDoctor().getId(), appointment.getDoctor().getNameEn(),
								appointment.getAppointmentDateTime(), appointment.getNotes(),
								appointment.getStatus()))
						.toList());
	}
}

