package com.kfh.clinic.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.kfh.clinic.api.models.ApiResponse;
import com.kfh.clinic.api.models.LoginRequest;
import com.kfh.clinic.api.models.LoginResponse;
import com.kfh.clinic.application.exception.InvalidRequestException;
import com.kfh.clinic.config.security.ActiveSessionService;
import com.kfh.clinic.config.security.JwtProperties;
import com.kfh.clinic.config.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtService jwtService;

	@Mock
	private ActiveSessionService activeSessionService;

	@Mock
	private UserDetailsService userDetailsService;

	@Mock
	private JwtProperties jwtProperties;

	@InjectMocks
	private AuthService authService;

	private UserDetails userDetails;
	private Authentication authentication;
	private LoginRequest loginRequest;

	@BeforeEach
	void setUp() {
		userDetails = User.builder()
				.username("admin")
				.password("encodedPassword")
				.authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
				.build();

		authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		loginRequest = new LoginRequest("admin", "password123");
	}

	@Test
	void login_ShouldReturnTokenWhenCredentialsValid() {
		Instant tokenExpiresAt = Instant.now().plus(3, ChronoUnit.MINUTES);
		Instant sessionExpiresAt = Instant.now().plus(10, ChronoUnit.MINUTES);
		String token = "test-jwt-token";
		String sessionId = "session-123";

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);
		when(jwtService.generateToken(any(UserDetails.class), any(Instant.class))).thenReturn(token);
		when(jwtService.extractExpirationInstant(token)).thenReturn(tokenExpiresAt);
		when(jwtProperties.getSessionValidityMinutes()).thenReturn(10L);
		when(activeSessionService.registerSession(anyString(), any(Instant.class), any(Instant.class), anyString()))
				.thenReturn(sessionId);

		LoginResponse response = authService.login(loginRequest);

		assertThat(response.accessToken()).isEqualTo(token);
		assertThat(response.expiresAt()).isEqualTo(tokenExpiresAt);
		assertThat(response.tokenType()).isEqualTo("Bearer");
		verify(activeSessionService).registerSession(anyString(), any(Instant.class), any(Instant.class), anyString());
	}

	@Test
	void login_ShouldThrowExceptionWhenCredentialsInvalid() {
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new BadCredentialsException("Invalid credentials"));

		assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
		verify(activeSessionService, never()).registerSession(anyString(), any(), any(), anyString());
	}

	@Test
	void refreshToken_ShouldReturnNewTokenWhenSessionActive() {
		String oldToken = "old-token";
		String newToken = "new-token";
		String authHeader = "Bearer " + oldToken;
		String sessionId = "session-123";
		Instant newTokenExpiresAt = Instant.now().plus(3, ChronoUnit.MINUTES);

		when(activeSessionService.isActive(oldToken)).thenReturn(true);
		when(activeSessionService.getSessionId(oldToken)).thenReturn(sessionId);
		when(activeSessionService.getUsernameForSession(sessionId)).thenReturn("admin");
		when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
		when(jwtService.generateToken(any(UserDetails.class), any(Instant.class))).thenReturn(newToken);
		when(jwtService.extractExpirationInstant(newToken)).thenReturn(newTokenExpiresAt);
		when(activeSessionService.renewToken(oldToken, newToken, newTokenExpiresAt)).thenReturn(true);

		LoginResponse response = authService.refreshToken(authHeader);

		assertThat(response.accessToken()).isEqualTo(newToken);
		assertThat(response.expiresAt()).isEqualTo(newTokenExpiresAt);
		verify(activeSessionService).renewToken(oldToken, newToken, newTokenExpiresAt);
	}

	@Test
	void refreshToken_ShouldThrowExceptionWhenSessionExpired() {
		String oldToken = "old-token";
		String authHeader = "Bearer " + oldToken;

		when(activeSessionService.isActive(oldToken)).thenReturn(false);

		assertThrows(InvalidRequestException.class, () -> authService.refreshToken(authHeader));
		verify(activeSessionService, never()).renewToken(anyString(), anyString(), any());
	}

	@Test
	void refreshToken_ShouldThrowExceptionWhenInvalidHeader() {
		assertThrows(InvalidRequestException.class, () -> authService.refreshToken("Invalid"));
		assertThrows(InvalidRequestException.class, () -> authService.refreshToken(null));
		assertThrows(InvalidRequestException.class, () -> authService.refreshToken(""));
	}

	@Test
	void logout_ShouldRevokeTokenWhenHeaderValid() {
		String token = "test-token";
		String authHeader = "Bearer " + token;

		ApiResponse response = authService.logout(authHeader);

		assertThat(response.message()).isEqualTo("Logged out successfully");
		verify(activeSessionService).revoke(token);
	}

	@Test
	void logout_ShouldSucceedEvenWhenHeaderInvalid() {
		ApiResponse response = authService.logout("Invalid");

		assertThat(response.message()).isEqualTo("Logged out successfully");
		verify(activeSessionService, never()).revoke(anyString());
	}
}

