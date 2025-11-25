package com.kfh.clinic.dto;

public record DoctorResponse(
		Long id,
		String nameEn,
		String nameAr,
		String specialty,
		int yearsOfExperience,
		int consultationDurationMinutes) {
}

