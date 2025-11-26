package com.kfh.clinic.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kfh.clinic.application.dto.DoctorDTO;
import com.kfh.clinic.application.exception.ResourceNotFoundException;
import com.kfh.clinic.application.mapper.DoctorEntityMapper;
import com.kfh.clinic.infrastructure.entity.Doctor;
import com.kfh.clinic.infrastructure.repository.DoctorRepository;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private DoctorEntityMapper doctorEntityMapper;

	@InjectMocks
	private DoctorService doctorService;

	private Doctor doctor1;
	private Doctor doctor2;
	private DoctorDTO doctorDTO1;
	private DoctorDTO doctorDTO2;

	@BeforeEach
	void setUp() {
		doctor1 = Doctor.builder()
				.id(1L)
				.nameEn("Dr. Sara Ahmed")
				.nameAr("د. سارة أحمد")
				.specialty("Cardiology")
				.yearsOfExperience(12)
				.consultationDurationMinutes(30)
				.build();

		doctor2 = Doctor.builder()
				.id(2L)
				.nameEn("Dr. Khalid Al-Mutairi")
				.nameAr("د. خالد المطيري")
				.specialty("Dermatology")
				.yearsOfExperience(9)
				.consultationDurationMinutes(20)
				.build();

		doctorDTO1 = new DoctorDTO(1L, "Dr. Sara Ahmed", "د. سارة أحمد", "Cardiology", 12, 30);
		doctorDTO2 = new DoctorDTO(2L, "Dr. Khalid Al-Mutairi", "د. خالد المطيري", "Dermatology", 9, 20);
	}

	@Test
	void getDoctors_ShouldReturnListOfDoctors() {
		when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));
		when(doctorEntityMapper.toDto(doctor1)).thenReturn(doctorDTO1);
		when(doctorEntityMapper.toDto(doctor2)).thenReturn(doctorDTO2);

		List<DoctorDTO> result = doctorService.getDoctors();

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getNameEn()).isEqualTo("Dr. Sara Ahmed");
		assertThat(result.get(1).getNameEn()).isEqualTo("Dr. Khalid Al-Mutairi");
		verify(doctorRepository).findAll();
	}

	@Test
	void getDoctors_ShouldReturnEmptyListWhenNoDoctors() {
		when(doctorRepository.findAll()).thenReturn(List.of());

		List<DoctorDTO> result = doctorService.getDoctors();

		assertThat(result).isEmpty();
		verify(doctorRepository).findAll();
	}

	@Test
	void getDoctorById_ShouldReturnDoctorWhenExists() {
		when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor1));

		Doctor result = doctorService.getDoctorById(1L);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getNameEn()).isEqualTo("Dr. Sara Ahmed");
		verify(doctorRepository).findById(1L);
	}

	@Test
	void getDoctorById_ShouldThrowExceptionWhenNotFound() {
		when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> doctorService.getDoctorById(999L));
		verify(doctorRepository).findById(999L);
	}
}

