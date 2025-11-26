package com.kfh.clinic.application.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;


public class DoctorDTO {
    private static final long serialVersionUID = 123432456L;

    private Long id;
    private String nameEn;
    private String nameAr;
    private String specialty;
    private int yearsOfExperience;
    private int consultationDurationMinutes;

    public DoctorDTO() {}

    public DoctorDTO(Long id, String nameEn, String nameAr, String specialty, int yearsOfExperience, int consultationDurationMinutes) {
        this.id = id;
        this.nameEn = nameEn;
        this.nameAr = nameAr;
        this.specialty = specialty;
        this.yearsOfExperience = yearsOfExperience;
        this.consultationDurationMinutes = consultationDurationMinutes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public int getConsultationDurationMinutes() { return consultationDurationMinutes; }
    public void setConsultationDurationMinutes(int consultationDurationMinutes) { this.consultationDurationMinutes = consultationDurationMinutes; }
}

