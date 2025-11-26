package com.kfh.clinic.config.security;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActiveSessionService {

	private static final String TOKEN_TO_SESSION_KEY_PREFIX = "token:session:";
	private static final String SESSION_DATA_KEY_PREFIX = "session:data:";

	private final RedisTemplate<String, Object> redisTemplate;

	private final ObjectMapper objectMapper;

	/**
	 * Creates a new session and associates a token with it.
	 * @param token The JWT token
	 * @param tokenExpiresAt Token expiration (3 minutes)
	 * @param sessionExpiresAt Session expiration (10 minutes)
	 * @param username The username for this session
	 * @return The session ID
	 */
	public String registerSession(String token, Instant tokenExpiresAt, Instant sessionExpiresAt, String username) {
		String sessionId = UUID.randomUUID().toString();
		Instant now = Instant.now();
		
		// Store session data with TTL until session expiration
		SessionData sessionData = new SessionData(sessionId, username, sessionExpiresAt);
		String sessionDataKey = SESSION_DATA_KEY_PREFIX + sessionId;
		Duration sessionTtl = Duration.between(now, sessionExpiresAt);
		if (sessionTtl.isNegative()) {
			sessionTtl = Duration.ZERO;
		}
		redisTemplate.opsForValue().set(sessionDataKey, sessionData, sessionTtl);
		
		// Map token to sessionId with TTL until token expiration
		String tokenKey = TOKEN_TO_SESSION_KEY_PREFIX + token;
		Duration tokenTtl = Duration.between(now, tokenExpiresAt);
		if (tokenTtl.isNegative()) {
			tokenTtl = Duration.ZERO;
		}
		redisTemplate.opsForValue().set(tokenKey, sessionId, tokenTtl);
		
		log.debug("Registered session {} for user {} - Session expires at {}", sessionId, username, sessionExpiresAt);
		return sessionId;
	}

	/**
	 * Associates a new token with an existing session (token renewal).
	 * @param oldToken The old token being replaced
	 * @param newToken The new token
	 * @param tokenExpiresAt New token expiration
	 * @return true if session was active and token was renewed, false otherwise
	 */
	public boolean renewToken(String oldToken, String newToken, Instant tokenExpiresAt) {
		String sessionId = getSessionId(oldToken);
		if (sessionId == null) {
			return false;
		}
		
		// Check if session is still active
		SessionData sessionData = getSessionData(sessionId);
		if (sessionData == null || sessionData.getSessionExpiresAt().isBefore(Instant.now())) {
			// Session expired, clean up
			cleanupSession(sessionId);
			return false;
		}
		
		// Remove old token mapping
		String oldTokenKey = TOKEN_TO_SESSION_KEY_PREFIX + oldToken;
		redisTemplate.delete(oldTokenKey);
		
		// Add new token mapping with TTL until token expiration
		String newTokenKey = TOKEN_TO_SESSION_KEY_PREFIX + newToken;
		Instant now = Instant.now();
		Duration tokenTtl = Duration.between(now, tokenExpiresAt);
		if (tokenTtl.isNegative()) {
			tokenTtl = Duration.ZERO;
		}
		redisTemplate.opsForValue().set(newTokenKey, sessionId, tokenTtl);
		
		log.debug("Renewed token for session {}", sessionId);
		return true;
	}

	/**
	 * Checks if a token is active (token not expired AND session not expired).
	 */
	public boolean isActive(String token) {
		String sessionId = getSessionId(token);
		if (sessionId == null) {
			return false;
		}
		
		SessionData sessionData = getSessionData(sessionId);
		if (sessionData == null) {
			return false;
		}
		
		return sessionData.getSessionExpiresAt().isAfter(Instant.now());
	}

	/**
	 * Gets the session ID for a token.
	 */
	public String getSessionId(String token) {
		String tokenKey = TOKEN_TO_SESSION_KEY_PREFIX + token;
		Object sessionId = redisTemplate.opsForValue().get(tokenKey);
		return sessionId != null ? sessionId.toString() : null;
	}

	/**
	 * Gets the username for a session.
	 */
	public String getUsernameForSession(String sessionId) {
		SessionData sessionData = getSessionData(sessionId);
		return sessionData != null ? sessionData.getUsername() : null;
	}

	/**
	 * Gets session data from Redis.
	 */
	private SessionData getSessionData(String sessionId) {
		String sessionDataKey = SESSION_DATA_KEY_PREFIX + sessionId;
		Object data = redisTemplate.opsForValue().get(sessionDataKey);
		try {
			SessionData sessionData = objectMapper.convertValue(data, SessionData.class);
			return sessionData;
		} catch (IllegalArgumentException ex) {
			// Conversion failed → data format is incorrect
			log.error("Invalid session data format in Redis: {}", data, ex);
			return null;
		}
	}

	/**
	 * Revokes a token and its associated session.
	 */
	public void revoke(String token) {
		String sessionId = getSessionId(token);
		
		// Remove token mapping
		String tokenKey = TOKEN_TO_SESSION_KEY_PREFIX + token;
		redisTemplate.delete(tokenKey);
		
		if (sessionId != null) {
			cleanupSession(sessionId);
		}
	}

	/**
	 * Cleans up all data for a session.
	 */
	private void cleanupSession(String sessionId) {
		String sessionDataKey = SESSION_DATA_KEY_PREFIX + sessionId;
		redisTemplate.delete(sessionDataKey);
		
		// Note: Token mappings will expire automatically based on their TTL
		// For immediate cleanup, we could scan and delete, but it's not necessary
		log.debug("Cleaned up session {}", sessionId);
	}
}

