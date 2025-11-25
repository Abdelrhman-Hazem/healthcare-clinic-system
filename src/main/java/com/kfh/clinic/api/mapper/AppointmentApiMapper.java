package com.kfh.clinic.api.mapper;

import com.kfh.clinic.api.models.AppointmentRequest;
import com.kfh.clinic.api.models.AppointmentResponse;
import com.kfh.clinic.api.models.AppointmentUpdateRequest;
import com.kfh.clinic.application.dto.AppointmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentApiMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctorName", ignore = true)
    @Mapping(target = "status", ignore = true)
    AppointmentDTO toDto(AppointmentRequest request);
    
    @Mapping(source = "id", target = "appointmentId")
    AppointmentResponse toResponse(AppointmentDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patientId", ignore = true)
    @Mapping(target = "doctorId", ignore = true)
    @Mapping(target = "doctorName", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateDtoFromRequest(AppointmentUpdateRequest request, @org.mapstruct.MappingTarget AppointmentDTO dto);
}

