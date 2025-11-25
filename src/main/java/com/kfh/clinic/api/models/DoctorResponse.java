package com.kfh.clinic.api.models;

public record DoctorResponse(
		Long id,
		String nameEn,
		String nameAr,
		String specialty,
		int yearsOfExperience,
		int consultationDurationMinutes) {
}

