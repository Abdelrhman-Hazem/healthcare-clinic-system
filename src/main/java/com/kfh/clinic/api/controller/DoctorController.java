package com.kfh.clinic.api.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kfh.clinic.api.mapper.DoctorApiMapper;
import com.kfh.clinic.api.models.DoctorResponse;
import com.kfh.clinic.application.service.DoctorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctors")
@Validated
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Doctor lookup APIs")
public class DoctorController {

	private final DoctorService doctorService;
	private final DoctorApiMapper doctorApiMapper;

	@GetMapping
	@Operation(summary = "List doctors", description = "Retrieves doctors asynchronously and caches the response.")
	public CompletableFuture<List<DoctorResponse>> getDoctors() {
		return doctorService.getDoctorsAsync()
				.thenApply(doctors -> doctors.stream()
						.map(doctorApiMapper::toResponse)
						.toList());
	}
}

