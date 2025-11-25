package com.kfh.clinic.api.mapper;

import com.kfh.clinic.api.models.DoctorResponse;
import com.kfh.clinic.application.dto.DoctorDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoctorApiMapper {
    DoctorResponse toResponse(DoctorDTO dto);
}

