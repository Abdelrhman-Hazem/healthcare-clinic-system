package com.kfh.clinic.application.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kfh.clinic.application.dto.AppointmentDTO;
import com.kfh.clinic.application.exception.InvalidRequestException;
import com.kfh.clinic.application.exception.ResourceNotFoundException;
import com.kfh.clinic.application.mapper.AppointmentEntityMapper;
import com.kfh.clinic.infrastructure.entity.Appointment;
import com.kfh.clinic.infrastructure.entity.AppointmentStatus;
import com.kfh.clinic.infrastructure.entity.Doctor;
import com.kfh.clinic.infrastructure.entity.Patient;
import com.kfh.clinic.infrastructure.repository.AppointmentRepository;
import com.kfh.clinic.infrastructure.repository.PatientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;
	private final DoctorService doctorService;
	private final AppointmentEntityMapper appointmentEntityMapper;

	@Async
	@Transactional
	public CompletableFuture<AppointmentDTO> scheduleAppointment(AppointmentDTO dto) {
		Patient patient = patientRepository.findById(dto.getPatientId())
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + dto.getPatientId()));
		Doctor doctor = doctorService.getDoctorById(dto.getDoctorId());
		if (!patient.isActive()) {
			throw new InvalidRequestException("Cannot schedule appointment for inactive patient");
		}

		log.info("Scheduling appointment patient={} doctor={} date={} (cache evicted)", patient.getId(), doctor.getId(),
				dto.getAppointmentDateTime());
		
		dto.setStatus(AppointmentStatus.SCHEDULED.name());
		Appointment appointment = appointmentEntityMapper.toEntity(dto);
		appointment.setPatient(patient);
		appointment.setDoctor(doctor);
		appointment.setStatus(AppointmentStatus.SCHEDULED);

		Appointment saved = appointmentRepository.save(appointment);
		return CompletableFuture.completedFuture(appointmentEntityMapper.toDto(saved));
	}

	@Transactional
	public AppointmentDTO updateAppointment(Long appointmentId, AppointmentDTO dto) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + appointmentId));

		log.info("Updating appointment {} status {} (cache evicted)", appointmentId, dto.getStatus());
		appointment.setAppointmentDateTime(dto.getAppointmentDateTime());
		appointment.setNotes(dto.getNotes());
		if (dto.getStatus() != null) {
			appointment.setStatus(AppointmentStatus.valueOf(dto.getStatus()));
		}

		Appointment saved = appointmentRepository.save(appointment);
		return appointmentEntityMapper.toDto(saved);
	}

	@Transactional(readOnly = true)
	public AppointmentDTO getAppointment(Long appointmentId) {
		return appointmentRepository.findById(appointmentId)
				.map(appointmentEntityMapper::toDto)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + appointmentId));
	}
}

