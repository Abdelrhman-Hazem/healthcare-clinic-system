package com.kfh.clinic.api.models;

import java.time.Instant;

public record LoginResponse(
		String accessToken,
		Instant expiresAt,
		String tokenType) {
}

