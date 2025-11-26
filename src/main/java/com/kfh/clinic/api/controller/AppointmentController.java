package com.kfh.clinic.api.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kfh.clinic.api.mapper.AppointmentApiMapper;
import com.kfh.clinic.api.models.AppointmentRequest;
import com.kfh.clinic.api.models.AppointmentResponse;
import com.kfh.clinic.api.models.AppointmentUpdateRequest;
import com.kfh.clinic.application.dto.AppointmentDTO;
import com.kfh.clinic.application.exception.ResourceNotFoundException;
import com.kfh.clinic.application.service.AppointmentService;

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
	private final AppointmentApiMapper appointmentApiMapper;

	@PostMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	@Operation(summary = "Schedule appointment", description = "Creates an appointment for an existing patient asynchronously.")
	public AppointmentResponse schedule(@Valid @RequestBody AppointmentRequest request){
		return appointmentService.scheduleAppointment(appointmentApiMapper.toDto(request))
				.thenApply(appointmentApiMapper::toResponse)
				.getNow(new AppointmentResponse(null, null, null, null, "processing...", null));
				//this is far from best practice, it is only a show case for Async calls
		
	}

	@PutMapping("/{appointmentId}")
	@Operation(summary = "Update appointment", description = "Updates appointment time, note and status.")
	public AppointmentResponse update(@PathVariable Long appointmentId,
			@Valid @RequestBody AppointmentUpdateRequest request) {
		AppointmentDTO dto = appointmentService.getAppointment(appointmentId);
		appointmentApiMapper.updateDtoFromRequest(request, dto);
		dto.setStatus(request.status().name());
		return appointmentApiMapper.toResponse(appointmentService.updateAppointment(appointmentId, dto));
	}
}

