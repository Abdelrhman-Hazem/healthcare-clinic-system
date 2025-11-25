package com.kfh.clinic.dto;

import java.time.Instant;

public record ApiResponse(
		boolean success,
		String message,
		Instant timestamp) {

	public static ApiResponse ok(String message) {
		return new ApiResponse(true, message, Instant.now());
	}
}

