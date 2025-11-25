package com.kfh.clinic.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kfh.clinic.api.models.ApiResponse;
import com.kfh.clinic.api.models.LoginRequest;
import com.kfh.clinic.api.models.LoginResponse;
import com.kfh.clinic.application.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Session-based JWT authentication endpoints")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Login", description = "Authenticates the user and returns a JWT access token (valid for 3 minutes). Session is valid for 10 minutes.")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/refresh")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Refresh token", description = "Renews the JWT token if the session (10 minutes) is still active. Token expires in 3 minutes.")
	public LoginResponse refreshToken(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
		return authService.refreshToken(authorizationHeader);
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Logout", description = "Revoke the active JWT token and session.")
	public ApiResponse logout(
			@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
		return authService.logout(authorizationHeader);
	}
}

