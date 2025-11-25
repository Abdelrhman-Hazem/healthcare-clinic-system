package com.kfh.clinic.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "patients")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE patients SET active=false WHERE id=?")
@Where(clause = "active = true")
public class Patient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String fullNameEn;

	@Column(nullable = false, length = 150)
	private String fullNameAr;

	@Email
	@Column(nullable = false, unique = true, length = 120)
	private String email;

	@Column(nullable = false, length = 20)
	private String mobileNumber;

	@Column(nullable = false)
	private LocalDate dateOfBirth;

	@Column(nullable = false, unique = true, length = 20)
	private String nationalId;

	@Embedded
	private Address address;

	@Builder.Default
	private boolean active = true;

	@Builder.Default
	@OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Appointment> appointments = new ArrayList<>();

	public void addAppointment(Appointment appointment) {
		appointments.add(appointment);
		appointment.setPatient(this);
	}
}

