package com.kfh.clinic.api.models;

import java.time.LocalDate;
import java.util.List;

public record PatientResponse(
		Long patientId,
		String fullNameEn,
		String fullNameAr,
		String email,
		String mobileNumber,
		LocalDate dateOfBirth,
		String nationalId,
		String street,
		String city,
		String region,
		List<AppointmentResponse> appointments) {
}

