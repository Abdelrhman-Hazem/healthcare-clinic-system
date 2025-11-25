package com.kfh.clinic.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kfh.clinic.dto.AppointmentRequest;
import com.kfh.clinic.dto.AppointmentResponse;
import com.kfh.clinic.dto.AppointmentUpdateRequest;
import com.kfh.clinic.entity.Appointment;
import com.kfh.clinic.entity.AppointmentStatus;
import com.kfh.clinic.entity.Doctor;
import com.kfh.clinic.entity.Patient;
import com.kfh.clinic.exception.InvalidRequestException;
import com.kfh.clinic.exception.ResourceNotFoundException;
import com.kfh.clinic.repository.AppointmentRepository;
import com.kfh.clinic.repository.PatientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;
	private final DoctorService doctorService;

	@Transactional
	public AppointmentResponse scheduleAppointment(AppointmentRequest request) {
		Patient patient = patientRepository.findById(request.patientId())
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + request.patientId()));
		Doctor doctor = doctorService.getDoctorById(request.doctorId());
		if (!patient.isActive()) {
			throw new InvalidRequestException("Cannot schedule appointment for inactive patient");
		}

		log.info("Scheduling appointment patient={} doctor={} date={}", patient.getId(), doctor.getId(),
				request.appointmentDateTime());
		Appointment appointment = Appointment.builder()
				.patient(patient)
				.doctor(doctor)
				.appointmentDateTime(request.appointmentDateTime())
				.notes(request.notes())
				.status(AppointmentStatus.SCHEDULED)
				.build();

		Appointment saved = appointmentRepository.save(appointment);
		return mapToResponse(saved);
	}

	@Transactional
	public AppointmentResponse updateAppointment(Long appointmentId, AppointmentUpdateRequest request) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + appointmentId));

		log.info("Updating appointment {} status {}", appointmentId, request.status());
		appointment.setAppointmentDateTime(request.appointmentDateTime());
		appointment.setNotes(request.notes());
		appointment.setStatus(request.status());

		return mapToResponse(appointmentRepository.save(appointment));
	}

	@Transactional(readOnly = true)
	public AppointmentResponse getAppointment(Long appointmentId) {
		return appointmentRepository.findById(appointmentId)
				.map(this::mapToResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + appointmentId));
	}

	private AppointmentResponse mapToResponse(Appointment appointment) {
		return new AppointmentResponse(appointment.getId(), appointment.getDoctor().getId(),
				appointment.getDoctor().getNameEn(), appointment.getAppointmentDateTime(), appointment.getNotes(),
				appointment.getStatus());
	}
}

