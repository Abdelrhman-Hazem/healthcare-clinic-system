package com.kfh.clinic.config.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ActiveSessionService {

	private final Map<String, Instant> activeTokens = new ConcurrentHashMap<>();

	public void registerToken(String token, Instant expiresAt) {
		activeTokens.put(token, expiresAt);
	}

	public boolean isActive(String token) {
		Instant expiresAt = activeTokens.get(token);
		return expiresAt != null && expiresAt.isAfter(Instant.now());
	}

	public void revoke(String token) {
		activeTokens.remove(token);
	}
}

