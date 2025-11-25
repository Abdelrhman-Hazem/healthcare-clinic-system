package com.kfh.clinic.api.mapper;

import com.kfh.clinic.api.models.PatientRegistrationRequest;
import com.kfh.clinic.api.models.PatientResponse;
import com.kfh.clinic.application.dto.PatientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AppointmentApiMapper.class)
public interface PatientApiMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "appointments", ignore = true)
    PatientDTO toDto(PatientRegistrationRequest req);
    
    @Mapping(source = "id", target = "patientId")
    @Mapping(source = "address.street", target = "street")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.region", target = "region")
    PatientResponse toResponse(PatientDTO dto);
}
