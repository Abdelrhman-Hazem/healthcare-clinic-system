package com.kfh.clinic.dto;

import java.time.LocalDateTime;

import com.kfh.clinic.entity.AppointmentStatus;

public record AppointmentResponse(
		Long appointmentId,
		Long doctorId,
		String doctorName,
		LocalDateTime appointmentDateTime,
		String notes,
		AppointmentStatus status) {
}

