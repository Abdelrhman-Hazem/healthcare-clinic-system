package com.kfh.clinic.application.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kfh.clinic.api.models.ApiResponse;
import com.kfh.clinic.api.models.LoginRequest;
import com.kfh.clinic.api.models.LoginResponse;
import com.kfh.clinic.application.exception.InvalidRequestException;
import com.kfh.clinic.config.security.ActiveSessionService;
import com.kfh.clinic.config.security.JwtProperties;
import com.kfh.clinic.config.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final ActiveSessionService activeSessionService;
	private final UserDetailsService userDetailsService;
	private final JwtProperties jwtProperties;

	public LoginResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		Instant issuedAt = Instant.now();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		// Generate token with 3-minute expiration
		String token = jwtService.generateToken(userDetails, issuedAt);
		Instant tokenExpiresAt = jwtService.extractExpirationInstant(token);
		
		// Create session with 10-minute expiration
		Instant sessionExpiresAt = issuedAt.plus(jwtProperties.getSessionValidityMinutes(), ChronoUnit.MINUTES);
		String sessionId = activeSessionService.registerSession(token, tokenExpiresAt, sessionExpiresAt, userDetails.getUsername());
		
		log.info("User {} logged in - Token expires at {}, Session expires at {}, SessionId: {}", 
				userDetails.getUsername(), tokenExpiresAt, sessionExpiresAt, sessionId);
		return new LoginResponse(token, tokenExpiresAt, "Bearer");
	}

	public LoginResponse refreshToken(String authHeader) {
		if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
			throw new InvalidRequestException("Invalid authorization header");
		}

		String oldToken = authHeader.substring(7);
		
		// Check if old token's session is still active
		if (!activeSessionService.isActive(oldToken)) {
			throw new InvalidRequestException("Session expired or token invalid");
		}

		String sessionId = activeSessionService.getSessionId(oldToken);
		String username = activeSessionService.getUsernameForSession(sessionId);
		
		if (username == null) {
			throw new InvalidRequestException("Session not found");
		}

		// Load user details and generate new token
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		Instant issuedAt = Instant.now();
		String newToken = jwtService.generateToken(userDetails, issuedAt);
		Instant newTokenExpiresAt = jwtService.extractExpirationInstant(newToken);

		// Renew token in session
		boolean renewed = activeSessionService.renewToken(oldToken, newToken, newTokenExpiresAt);
		if (!renewed) {
			throw new InvalidRequestException("Failed to renew token - session may have expired");
		}

		log.info("Token renewed for user {} - New token expires at {}", username, newTokenExpiresAt);
		return new LoginResponse(newToken, newTokenExpiresAt, "Bearer");
	}

	public ApiResponse logout(String authHeader) {
		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			activeSessionService.revoke(token);
			log.info("Token and session revoked");
		}
		SecurityContextHolder.clearContext();
		return ApiResponse.ok("Logged out successfully");
	}
}

