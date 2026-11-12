package com.example.studentcrud.service;

import com.example.studentcrud.dto.StudentRequest;
import com.example.studentcrud.exception.DuplicateStudentException;
import com.example.studentcrud.exception.StudentNotFoundException;
import com.example.studentcrud.model.Student;
import com.example.studentcrud.repository.StudentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic layer: rules such as "email and roll number must be unique" live here,
 * not in the controller and not in the React UI.
 */
@Service
@Transactional
public class StudentService {

    private static final Sort DEFAULT_SORT = Sort.by("id").ascending();

    private final StudentRepository repository;

    // Constructor injection: Spring passes in the repository for us
    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    // READ - all students, optionally filtered by a search text
    @Transactional(readOnly = true)
    public List<Student> findAll(String search) {
        if (search == null || search.isBlank()) {
            return repository.findAll(DEFAULT_SORT);
        }
        return repository.search(search.trim(), DEFAULT_SORT);
    }

    // READ - one student
    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    // CREATE
    public Student create(StudentRequest request) {
        String email = request.email().trim();
        String rollNo = request.rollNo().trim().toUpperCase();

        if (repository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateStudentException("email", "Email '" + email + "' is already registered");
        }
        if (repository.existsByRollNoIgnoreCase(rollNo)) {
            throw new DuplicateStudentException("rollNo", "Roll number '" + rollNo + "' already exists");
        }

        Student student = new Student();
        apply(student, request);
        return repository.save(student);
    }

    // UPDATE
    public Student update(Long id, StudentRequest request) {
        Student student = findById(id);
        String email = request.email().trim();
        String rollNo = request.rollNo().trim().toUpperCase();

        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateStudentException("email", "Email '" + email + "' is already registered");
        }
        if (repository.existsByRollNoIgnoreCaseAndIdNot(rollNo, id)) {
            throw new DuplicateStudentException("rollNo", "Roll number '" + rollNo + "' already exists");
        }

        apply(student, request);
        return repository.save(student);
    }

    // DELETE
    public void delete(Long id) {
        Student student = findById(id); // throws 404 if it does not exist
        repository.delete(student);
    }

    private void apply(Student student, StudentRequest request) {
        student.setName(request.name().trim());
        student.setEmail(request.email().trim().toLowerCase());
        student.setRollNo(request.rollNo().trim().toUpperCase());
        student.setDepartment(request.department().trim());
        student.setYearOfStudy(request.yearOfStudy());
    }
}
