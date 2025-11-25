package com.kfh.clinic.application.mapper;

import com.kfh.clinic.application.dto.AppointmentDTO;
import com.kfh.clinic.infrastructure.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentEntityMapper {
    
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(source = "status", target = "status", ignore = true)
    Appointment toEntity(AppointmentDTO dto);
    
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.nameEn", target = "doctorName")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    AppointmentDTO toDto(Appointment entity);
    
    @org.mapstruct.Named("statusToString")
    default String statusToString(com.kfh.clinic.infrastructure.entity.AppointmentStatus status) {
        return status != null ? status.name() : null;
    }
    
    default com.kfh.clinic.infrastructure.entity.AppointmentStatus stringToStatus(String status) {
        return status != null ? com.kfh.clinic.infrastructure.entity.AppointmentStatus.valueOf(status) : null;
    }
}

