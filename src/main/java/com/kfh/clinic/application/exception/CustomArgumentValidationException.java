package com.kfh.clinic.application.exception;

import java.time.Instant;
import java.util.List;

public record CustomArgumentValidationException(
		Instant timestamp,
		int status,
		String error,
		String message,
		String path,
		List<String> details) {
}

