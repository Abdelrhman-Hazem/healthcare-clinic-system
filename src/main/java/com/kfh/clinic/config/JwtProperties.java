package com.kfh.clinic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "clinic.jwt")
public class JwtProperties {

	@NotBlank
	private String secret;

	@Positive
	private long accessTokenValidityMinutes;
}

