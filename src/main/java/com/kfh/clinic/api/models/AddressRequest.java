package com.kfh.clinic.api.models;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
		@NotBlank(message = "Street is required") String street,
		@NotBlank(message = "City is required") String city,
		@NotBlank(message = "Region is required") String region) {
}

