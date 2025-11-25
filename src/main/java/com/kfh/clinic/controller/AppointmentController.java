package com.kfh.clinic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kfh.clinic.dto.AppointmentRequest;
import com.kfh.clinic.dto.AppointmentResponse;
import com.kfh.clinic.dto.AppointmentUpdateRequest;
import com.kfh.clinic.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@Validated
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Manage patient appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Schedule appointment", description = "Creates an appointment for an existing patient.")
	public AppointmentResponse schedule(@Valid @RequestBody AppointmentRequest request) {
		return appointmentService.scheduleAppointment(request);
	}

	@PutMapping("/{appointmentId}")
	@Operation(summary = "Update appointment", description = "Updates appointment time, note and status.")
	public AppointmentResponse update(@PathVariable Long appointmentId,
			@Valid @RequestBody AppointmentUpdateRequest request) {
		return appointmentService.updateAppointment(appointmentId, request);
	}
}

