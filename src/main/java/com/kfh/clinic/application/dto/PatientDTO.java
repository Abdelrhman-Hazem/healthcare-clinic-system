package com.kfh.clinic.application.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;


public class PatientDTO {
    private Long id;
    private String fullNameEn;
    private String fullNameAr;
    private String email;
    private String mobileNumber;
    private LocalDate dateOfBirth;
    private String nationalId;
    private AddressDTO address;
    private boolean active;
    private List<AppointmentDTO> appointments;

    // Getters, setters, builder, etc., as needed

    public static class AddressDTO {
        private String street;
        private String city;
        private String region;
        // Getters/setters/constructor
        public AddressDTO() {}
        public AddressDTO(String street, String city, String region) {
            this.street = street;
            this.city = city;
            this.region = region;
        }
        // getters/setters...
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
    }

    // Getters/setters/constructor
    public PatientDTO() {}
    // All Args constructor for mapping convenience
    public PatientDTO(Long id, String fullNameEn, String fullNameAr, String email, String mobileNumber, LocalDate dateOfBirth, String nationalId, AddressDTO address, boolean active, List<AppointmentDTO> appointments) {
        this.id = id;
        this.fullNameEn = fullNameEn;
        this.fullNameAr = fullNameAr;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.dateOfBirth = dateOfBirth;
        this.nationalId = nationalId;
        this.address = address;
        this.active = active;
        this.appointments = appointments;
    }
    // getters/setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullNameEn() { return fullNameEn; }
    public void setFullNameEn(String fullNameEn) { this.fullNameEn = fullNameEn; }
    public String getFullNameAr() { return fullNameAr; }
    public void setFullNameAr(String fullNameAr) { this.fullNameAr = fullNameAr; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    public AddressDTO getAddress() { return address; }
    public void setAddress(AddressDTO address) { this.address = address; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<AppointmentDTO> getAppointments() { return appointments; }
    public void setAppointments(List<AppointmentDTO> appointments) { this.appointments = appointments; }
}
