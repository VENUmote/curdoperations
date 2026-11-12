package com.example.studentcrud.config;

import com.example.studentcrud.model.Student;
import com.example.studentcrud.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * Adds a few sample students the first time the app starts with an empty database,
 * so the UI is not blank on first run. Disabled in the "test" profile.
 */
@Configuration
@Profile("!test")
public class DataSeeder {

    @Bean
    CommandLineRunner seedStudents(StudentRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.saveAll(List.of(
                    new Student("Asha Reddy", "asha.reddy@college.edu", "CS2041", "Computer Science", 2),
                    new Student("Ravi Kumar", "ravi.kumar@college.edu", "EC2017", "Electronics", 2),
                    new Student("Meera Nair", "meera.nair@college.edu", "IT3009", "Information Technology", 3),
                    new Student("Arjun Singh", "arjun.singh@college.edu", "ME1022", "Mechanical", 1),
                    new Student("Priya Sharma", "priya.sharma@college.edu", "CS4005", "Computer Science", 4)
            ));
        };
    }
}
