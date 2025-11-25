package com.kfh.clinic.service;

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

import com.kfh.clinic.dto.AppointmentRequest;
import com.kfh.clinic.dto.AppointmentUpdateRequest;
import com.kfh.clinic.entity.Appointment;
import com.kfh.clinic.entity.AppointmentStatus;
import com.kfh.clinic.entity.Doctor;
import com.kfh.clinic.entity.Patient;
import com.kfh.clinic.exception.InvalidRequestException;
import com.kfh.clinic.repository.AppointmentRepository;
import com.kfh.clinic.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private DoctorService doctorService;

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
		AppointmentRequest request = new AppointmentRequest(activePatient.getId(), doctor.getId(), appointmentDate,
				"Routine check");

		when(patientRepository.findById(activePatient.getId())).thenReturn(Optional.of(activePatient));
		when(doctorService.getDoctorById(doctor.getId())).thenReturn(doctor);
		when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
			Appointment appointment = invocation.getArgument(0);
			appointment.setId(10L);
			return appointment;
		});

		var response = appointmentService.scheduleAppointment(request);

		assertThat(response.appointmentId()).isEqualTo(10L);
		assertThat(response.status()).isEqualTo(AppointmentStatus.SCHEDULED);
		verify(appointmentRepository).save(any(Appointment.class));
	}

	@Test
	void scheduleAppointment_ShouldFailForInactivePatient() {
		activePatient.setActive(false);
		LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
		AppointmentRequest request = new AppointmentRequest(activePatient.getId(), doctor.getId(), appointmentDate,
				"Routine check");

		when(patientRepository.findById(activePatient.getId())).thenReturn(Optional.of(activePatient));

		assertThrows(InvalidRequestException.class, () -> appointmentService.scheduleAppointment(request));
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

		AppointmentUpdateRequest request = new AppointmentUpdateRequest(newDate, "Updated note",
				AppointmentStatus.COMPLETED);

		when(appointmentRepository.findById(5L)).thenReturn(Optional.of(appointment));
		when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

		var response = appointmentService.updateAppointment(5L, request);

		assertThat(response.status()).isEqualTo(AppointmentStatus.COMPLETED);
		assertThat(response.appointmentDateTime()).isEqualTo(newDate);
		verify(appointmentRepository).save(appointment);
	}
}

