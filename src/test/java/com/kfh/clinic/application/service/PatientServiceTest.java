package com.kfh.clinic.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kfh.clinic.application.dto.PatientDTO;
import com.kfh.clinic.application.exception.DuplicateResourceException;
import com.kfh.clinic.application.exception.ResourceNotFoundException;
import com.kfh.clinic.application.mapper.PatientEntityMapper;
import com.kfh.clinic.infrastructure.entity.Patient;
import com.kfh.clinic.infrastructure.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

	@Mock
	private PatientRepository patientRepository;
	
	@Mock
	private PatientEntityMapper patientEntityMapper;

	@InjectMocks
	private PatientService patientService;

	private PatientDTO patientDTO;

	@BeforeEach
	void setUp() {
		PatientDTO.AddressDTO address = new PatientDTO.AddressDTO("Street 1", "Kuwait City", "Capital");
		patientDTO = new PatientDTO();
		patientDTO.setFullNameEn("John Doe");
		patientDTO.setFullNameAr("جون دو");
		patientDTO.setEmail("john.doe@example.com");
		patientDTO.setMobileNumber("+96512345678");
		patientDTO.setDateOfBirth(LocalDate.of(1990, 1, 10));
		patientDTO.setNationalId("123456789012");
		patientDTO.setAddress(address);
	}

	@Test
	void register_ShouldPersistWhenUnique() {
		when(patientRepository.existsByEmail(patientDTO.getEmail())).thenReturn(false);
		when(patientRepository.existsByNationalId(patientDTO.getNationalId())).thenReturn(false);
		
		Patient patient = new Patient();
		patient.setId(1L);
		when(patientEntityMapper.toEntity(any(PatientDTO.class))).thenReturn(patient);
		when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
			Patient p = invocation.getArgument(0);
			p.setId(1L);
			return p;
		});
		
		PatientDTO savedDto = new PatientDTO();
		savedDto.setId(1L);
		savedDto.setEmail("john.doe@example.com");
		when(patientEntityMapper.toDto(any(Patient.class))).thenReturn(savedDto);

		PatientDTO response = patientService.register(patientDTO);

		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
		verify(patientRepository).save(any(Patient.class));
	}

	@Test
	void register_ShouldThrowWhenDuplicateEmail() {
		when(patientRepository.existsByEmail(patientDTO.getEmail())).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> patientService.register(patientDTO));
		verify(patientRepository, never()).save(any(Patient.class));
	}

	@Test
	void register_ShouldThrowWhenDuplicateNationalId() {
		when(patientRepository.existsByEmail(patientDTO.getEmail())).thenReturn(false);
		when(patientRepository.existsByNationalId(patientDTO.getNationalId())).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> patientService.register(patientDTO));
		verify(patientRepository, never()).save(any(Patient.class));
	}

	@Test
	void getAllPatientsWithAppointments_ShouldReturnListOfPatients() {
		Patient patient1 = new Patient();
		patient1.setId(1L);
		patient1.setFullNameEn("John Doe");
		Patient patient2 = new Patient();
		patient2.setId(2L);
		patient2.setFullNameEn("Jane Smith");

		PatientDTO dto1 = new PatientDTO();
		dto1.setId(1L);
		dto1.setFullNameEn("John Doe");
		PatientDTO dto2 = new PatientDTO();
		dto2.setId(2L);
		dto2.setFullNameEn("Jane Smith");

		when(patientRepository.findAllByOrderByFullNameEnAsc()).thenReturn(List.of(patient1, patient2));
		when(patientEntityMapper.toDto(patient1)).thenReturn(dto1);
		when(patientEntityMapper.toDto(patient2)).thenReturn(dto2);

		List<PatientDTO> result = patientService.getAllPatientsWithAppointments();

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getFullNameEn()).isEqualTo("John Doe");
		assertThat(result.get(1).getFullNameEn()).isEqualTo("Jane Smith");
		verify(patientRepository).findAllByOrderByFullNameEnAsc();
	}

	@Test
	void softDeletePatient_ShouldDeactivatePatient() {
		Patient patient = new Patient();
		patient.setId(1L);
		patient.setEmail("test@example.com");
		patient.setActive(true);

		when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
		when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

		patientService.softDeletePatient(1L);

		assertThat(patient.isActive()).isFalse();
		verify(patientRepository).save(patient);
	}

	@Test
	void softDeletePatient_ShouldThrowExceptionWhenNotFound() {
		when(patientRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> patientService.softDeletePatient(999L));
		verify(patientRepository, never()).save(any(Patient.class));
	}
}

