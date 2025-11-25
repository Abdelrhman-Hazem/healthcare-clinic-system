package com.kfh.clinic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kfh.clinic.dto.AddressRequest;
import com.kfh.clinic.dto.PatientRegistrationRequest;
import com.kfh.clinic.dto.PatientResponse;
import com.kfh.clinic.entity.Patient;
import com.kfh.clinic.exception.DuplicateResourceException;
import com.kfh.clinic.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

	@Mock
	private PatientRepository patientRepository;

	@InjectMocks
	private PatientService patientService;

	private PatientRegistrationRequest registrationRequest;

	@BeforeEach
	void setUp() {
		registrationRequest = new PatientRegistrationRequest(
				"John Doe",
				"جون دو",
				"john.doe@example.com",
				"+96512345678",
				LocalDate.of(1990, 1, 10),
				"123456789012",
				new AddressRequest("Street 1", "Kuwait City", "Capital"));
	}

	@Test
	void register_ShouldPersistWhenUnique() {
		when(patientRepository.existsByEmail(registrationRequest.email())).thenReturn(false);
		when(patientRepository.existsByNationalId(registrationRequest.nationalId())).thenReturn(false);
		when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
			Patient patient = invocation.getArgument(0);
			patient.setId(1L);
			return patient;
		});

		PatientResponse response = patientService.register(registrationRequest);

		assertThat(response.patientId()).isEqualTo(1L);
		assertThat(response.email()).isEqualTo("john.doe@example.com");
		verify(patientRepository).save(any(Patient.class));
	}

	@Test
	void register_ShouldThrowWhenDuplicateEmail() {
		when(patientRepository.existsByEmail(registrationRequest.email())).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> patientService.register(registrationRequest));
		verify(patientRepository, never()).save(any(Patient.class));
	}
}

