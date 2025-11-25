package com.kfh.clinic.api.models;

import java.time.LocalDateTime;

import com.kfh.clinic.infrastructure.entity.AppointmentStatus;

public record AppointmentResponse(
		Long appointmentId,
		Long doctorId,
		String doctorName,
		LocalDateTime appointmentDateTime,
		String notes,
		AppointmentStatus status) {
}

