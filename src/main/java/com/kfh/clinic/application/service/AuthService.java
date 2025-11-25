package com.kfh.clinic.application.service;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kfh.clinic.api.models.ApiResponse;
import com.kfh.clinic.api.models.LoginRequest;
import com.kfh.clinic.api.models.LoginResponse;
import com.kfh.clinic.config.security.ActiveSessionService;
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

	public LoginResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		Instant issuedAt = Instant.now();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String token = jwtService.generateToken(userDetails, issuedAt);
		Instant expiresAt = jwtService.extractExpirationInstant(token);
		activeSessionService.registerToken(token, expiresAt);
		log.info("User {} logged in with token expiring at {}", userDetails.getUsername(), expiresAt);
		return new LoginResponse(token, expiresAt, "Bearer");
	}

	public ApiResponse logout(String authHeader) {
		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			activeSessionService.revoke(token);
			log.info("Token revoked");
		}
		SecurityContextHolder.clearContext();
		return ApiResponse.ok("Logged out successfully");
	}
}

