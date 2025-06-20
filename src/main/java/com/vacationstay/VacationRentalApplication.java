package com.vacationstay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the VacationStay rental application.
 * <p>
 * This class serves as the entry point for the Spring Boot application.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@SpringBootApplication
public class VacationRentalApplication {

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(VacationRentalApplication.class, args);
    }
}