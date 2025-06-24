package com.filemanager.backend;

import com.filemanager.backend.entity.User;
import com.filemanager.backend.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class FileManagerApplication {

	private static final Logger logger = LoggerFactory.getLogger(FileManagerApplication.class);

	public static void main(String[] args) {
		logger.info("Starting File Manager Application...");
		SpringApplication.run(FileManagerApplication.class, args);
		logger.info("File Manager Application started successfully!");
	}

	/**
	 * Initializes the admin user if it does not exist in the database.
	 * This method runs automatically at application startup.
	 */
	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				User admin = User.builder()
						.username("admin")
						.email("admin@example.com")
						.password(passwordEncoder.encode("admin123"))
						.role(User.Role.ADMIN) //
						.build();
				userRepository.save(admin);
				logger.info("✅ Admin user created: admin / admin123");
			} else {
				logger.info("ℹ️ Admin user already exists.");
			}
		};
	}
}
