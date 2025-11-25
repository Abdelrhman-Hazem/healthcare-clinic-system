package com.kfh.clinic.config.security;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtService {

	private final JwtProperties jwtProperties;

	public String generateToken(UserDetails userDetails, Instant issuedAt) {
		List<String> authorities = userDetails.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.toList();

		return Jwts.builder()
				.setSubject(userDetails.getUsername())
				.claim("roles", authorities)
				.setIssuedAt(Date.from(issuedAt))
				.setExpiration(Date.from(issuedAt.plus(jwtProperties.getAccessTokenValidityMinutes(), ChronoUnit.MINUTES)))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Instant extractExpirationInstant(String token) {
		return extractClaim(token, Claims::getExpiration).toInstant();
	}

	private boolean isTokenExpired(String token) {
		return extractExpirationInstant(token).isBefore(Instant.now());
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}
}

