package com.kfh.clinic.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppointmentRequest(
		@NotNull Long patientId,
		@NotNull Long doctorId,
		@NotNull @Future(message = "Appointment date must be in the future") LocalDateTime appointmentDateTime,
		@NotBlank(message = "Notes must not be blank") String notes) {
}

