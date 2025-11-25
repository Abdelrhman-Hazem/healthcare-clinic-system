package com.kfh.clinic.api.models;

import java.time.LocalDateTime;

import com.kfh.clinic.infrastructure.entity.AppointmentStatus;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppointmentUpdateRequest(
		@NotNull @Future(message = "Appointment date must be in the future") LocalDateTime appointmentDateTime,
		@NotBlank String notes,
		@NotNull AppointmentStatus status) {
}

