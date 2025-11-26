package com.kfh.clinic.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kfh.clinic.api.mapper.PatientApiMapper;
import com.kfh.clinic.api.models.PatientRegistrationRequest;
import com.kfh.clinic.api.models.PatientResponse;
import com.kfh.clinic.application.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patients")
@Validated
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Patient registration and management APIs")
public class PatientController {

	private final PatientService patientService;
	private final PatientApiMapper patientApiMapper;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Register patient", description = "Registers a new patient with contact and address information.")
	public PatientResponse registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {
		return patientApiMapper.toResponse(patientService.register(patientApiMapper.toDto(request)));
	}

	@GetMapping
	@Operation(summary = "List patients", description = "Returns all active patients along with their appointments.")
	public List<PatientResponse> getPatientsWithAppointments() {
		return patientService.getAllPatientsWithAppointments()
				.stream()
				.map(patientApiMapper::toResponse)
				.toList();
	}

	@DeleteMapping("/{patientId}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Soft delete patient", description = "Marks the patient as inactive without removing data.")
	public void deletePatient(@PathVariable Long patientId) {
		patientService.softDeletePatient(patientId);
	}
}
