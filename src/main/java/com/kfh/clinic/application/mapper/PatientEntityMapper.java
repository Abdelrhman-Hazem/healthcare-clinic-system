package com.kfh.clinic.application.mapper;

import com.kfh.clinic.application.dto.PatientDTO;
import com.kfh.clinic.infrastructure.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AppointmentEntityMapper.class)
public interface PatientEntityMapper {
    
    @Mapping(target = "appointments", ignore = true)
    Patient toEntity(PatientDTO dto);
    
    @Mapping(target = "appointments", source = "appointments")
    PatientDTO toDto(Patient entity);
}
