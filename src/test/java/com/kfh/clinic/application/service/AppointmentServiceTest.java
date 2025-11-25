package com.kfh.clinic.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kfh.clinic.application.dto.AppointmentDTO;
import com.kfh.clinic.application.exception.InvalidRequestException;
import com.kfh.clinic.application.mapper.AppointmentEntityMapper;
import com.kfh.clinic.infrastructure.entity.Appointment;
import com.kfh.clinic.infrastructure.entity.AppointmentStatus;
import com.kfh.clinic.infrastructure.entity.Doctor;
import com.kfh.clinic.infrastructure.entity.Patient;
import com.kfh.clinic.infrastructure.repository.AppointmentRepository;
import com.kfh.clinic.infrastructure.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private DoctorService doctorService;
	
	@Mock
	private AppointmentEntityMapper appointmentEntityMapper;

	@InjectMocks
	private AppointmentService appointmentService;

	private Patient activePatient;
	private Doctor doctor;

	@BeforeEach
	void setup() {
		activePatient = Patient.builder()
				.id(1L)
				.fullNameEn("John")
				.fullNameAr("جون")
				.email("john@doe.com")
				.mobileNumber("+96544444444")
				.dateOfBirth(LocalDate.of(1985, 5, 20))
				.nationalId("112233445566")
				.active(true)
				.build();

		doctor = Doctor.builder()
				.id(2L)
				.nameEn("Dr. Samir")
				.nameAr("د. سمير")
				.specialty("Dentist")
				.yearsOfExperience(8)
				.consultationDurationMinutes(30)
				.build();
	}

	@Test
	void scheduleAppointment_ShouldCreateAppointment() {
		LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
		AppointmentDTO dto = new AppointmentDTO();
		dto.setPatientId(activePatient.getId());
		dto.setDoctorId(doctor.getId());
		dto.setAppointmentDateTime(appointmentDate);
		dto.setNotes("Routine check");

		when(patientRepository.findById(activePatient.getId())).thenReturn(Optional.of(activePatient));
		when(doctorService.getDoctorById(doctor.getId())).thenReturn(doctor);
		
		Appointment appointment = new Appointment();
		when(appointmentEntityMapper.toEntity(any(AppointmentDTO.class))).thenReturn(appointment);
		when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
			Appointment a = invocation.getArgument(0);
			a.setId(10L);
			return a;
		});
		
		AppointmentDTO savedDto = new AppointmentDTO();
		savedDto.setId(10L);
		savedDto.setStatus(AppointmentStatus.SCHEDULED.name());
		when(appointmentEntityMapper.toDto(any(Appointment.class))).thenReturn(savedDto);

		var response = appointmentService.scheduleAppointment(dto);

		assertThat(response.getId()).isEqualTo(10L);
		assertThat(response.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED.name());
		verify(appointmentRepository).save(any(Appointment.class));
	}

	@Test
	void scheduleAppointment_ShouldFailForInactivePatient() {
		activePatient.setActive(false);
		LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
		AppointmentDTO dto = new AppointmentDTO();
		dto.setPatientId(activePatient.getId());
		dto.setDoctorId(doctor.getId());
		dto.setAppointmentDateTime(appointmentDate);
		dto.setNotes("Routine check");

		when(patientRepository.findById(activePatient.getId())).thenReturn(Optional.of(activePatient));

		assertThrows(InvalidRequestException.class, () -> appointmentService.scheduleAppointment(dto));
	}

	@Test
	void updateAppointment_ShouldPersistChanges() {
		LocalDateTime newDate = LocalDateTime.now().plusDays(3);
		Appointment appointment = Appointment.builder()
				.id(5L)
				.patient(activePatient)
				.doctor(doctor)
				.appointmentDateTime(LocalDateTime.now().plusDays(2))
				.notes("Initial")
				.status(AppointmentStatus.SCHEDULED)
				.build();

		AppointmentDTO dto = new AppointmentDTO();
		dto.setAppointmentDateTime(newDate);
		dto.setNotes("Updated note");
		dto.setStatus(AppointmentStatus.COMPLETED.name());

		when(appointmentRepository.findById(5L)).thenReturn(Optional.of(appointment));
		when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		AppointmentDTO savedDto = new AppointmentDTO();
		savedDto.setStatus(AppointmentStatus.COMPLETED.name());
		savedDto.setAppointmentDateTime(newDate);
		when(appointmentEntityMapper.toDto(any(Appointment.class))).thenReturn(savedDto);

		var response = appointmentService.updateAppointment(5L, dto);

		assertThat(response.getStatus()).isEqualTo(AppointmentStatus.COMPLETED.name());
		assertThat(response.getAppointmentDateTime()).isEqualTo(newDate);
		verify(appointmentRepository).save(appointment);
	}
}

