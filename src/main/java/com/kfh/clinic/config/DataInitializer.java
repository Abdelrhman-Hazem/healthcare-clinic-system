package com.kfh.clinic.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kfh.clinic.entity.AppUser;
import com.kfh.clinic.entity.Doctor;
import com.kfh.clinic.entity.UserRole;
import com.kfh.clinic.repository.AppUserRepository;
import com.kfh.clinic.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile({ "dev", "test" })
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

	private final DoctorRepository doctorRepository;
	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		seedDoctors();
		seedDefaultUser();
	}

	private void seedDoctors() {
		if (doctorRepository.count() > 0) {
			return;
		}

		log.info("Seeding default doctors for quick lookup");
		List<Doctor> doctors = List.of(
				Doctor.builder().nameEn("Dr. Sara Ahmed").nameAr("د. سارة أحمد").specialty("Cardiology")
						.yearsOfExperience(12).consultationDurationMinutes(30).build(),
				Doctor.builder().nameEn("Dr. Khalid Al-Mutairi").nameAr("د. خالد المطيري").specialty("Dermatology")
						.yearsOfExperience(9).consultationDurationMinutes(20).build(),
				Doctor.builder().nameEn("Dr. Fatima Hussain").nameAr("د. فاطمة حسين").specialty("Pediatrics")
						.yearsOfExperience(15).consultationDurationMinutes(25).build());

		doctorRepository.saveAll(doctors);
	}

	private void seedDefaultUser() {
		appUserRepository.findByUsername("admin").ifPresentOrElse(user -> {
		}, () -> {
			log.info("Creating default admin user (username=admin, password=Admin@123)");
			AppUser admin = AppUser.builder()
					.username("admin")
					.password(passwordEncoder.encode("Admin@123"))
					.role(UserRole.ADMIN)
					.build();
			appUserRepository.save(admin);
		});
	}
}

