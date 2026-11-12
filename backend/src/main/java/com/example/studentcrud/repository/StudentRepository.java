package com.example.studentcrud.repository;

import com.example.studentcrud.model.Student;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA writes the implementation of this interface at runtime.
 * JpaRepository already gives us save(), findById(), findAll(), deleteById(), count() ...
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Used to stop duplicate emails / roll numbers
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByRollNoIgnoreCase(String rollNo);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByRollNoIgnoreCaseAndIdNot(String rollNo, Long id);

    // Simple search across name, roll number, email and department
    @Query("""
            SELECT s FROM Student s
            WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(s.rollNo) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(s.email) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(s.department) LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    List<Student> search(@Param("q") String query, Sort sort);
}
