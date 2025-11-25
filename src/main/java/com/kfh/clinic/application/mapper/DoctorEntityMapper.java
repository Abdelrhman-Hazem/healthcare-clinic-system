package com.kfh.clinic.application.mapper;

import com.kfh.clinic.application.dto.DoctorDTO;
import com.kfh.clinic.infrastructure.entity.Doctor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoctorEntityMapper {
    Doctor toEntity(DoctorDTO dto);
    DoctorDTO toDto(Doctor entity);
}

