package com.kfh.clinic.api.models;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatientRegistrationRequest(
		@NotBlank(message = "English name is required") @Size(max = 150) String fullNameEn,
		@NotBlank(message = "Arabic name is required") @Size(max = 150) String fullNameAr,
		@NotBlank @Email String email,
		@NotBlank @Pattern(regexp = "^[0-9+]{8,20}$", message = "Invalid mobile number") String mobileNumber,
		@NotNull @Past(message = "Date of birth must be in the past") LocalDate dateOfBirth,
		@NotBlank @Size(min = 10, max = 20) String nationalId,
		@NotNull @Valid AddressRequest address) {
}

