package com.example.studentcrud;

import com.example.studentcrud.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests: start the whole app (with H2) and call the REST API through MockMvc.
 * Run with:  mvn test   (or the "Run Test" links in VS Code)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private StudentRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    private static String json(String name, String email, String rollNo, String dept, int year) {
        return """
                {"name":"%s","email":"%s","rollNo":"%s","department":"%s","yearOfStudy":%d}
                """.formatted(name, email, rollNo, dept, year);
    }

    private long createStudent(String name, String email, String rollNo) throws Exception {
        MvcResult result = mvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(name, email, rollNo, "Computer Science", 2)))
                .andExpect(status().isCreated())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return Long.parseLong(body.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void createAndReadStudent() throws Exception {
        mvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Asha Reddy", "Asha@College.edu", "cs2041", "Computer Science", 2)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("asha@college.edu"))
                .andExpect(jsonPath("$.rollNo").value("CS2041"));

        mvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Asha Reddy"));
    }

    @Test
    void searchFiltersStudents() throws Exception {
        createStudent("Asha Reddy", "asha@college.edu", "CS2041");
        createStudent("Ravi Kumar", "ravi@college.edu", "EC2017");

        mvc.perform(get("/api/students").param("search", "ravi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].rollNo").value("EC2017"));
    }

    @Test
    void updateStudent() throws Exception {
        long id = createStudent("Asha Reddy", "asha@college.edu", "CS2041");

        mvc.perform(put("/api/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Asha R", "asha@college.edu", "CS2041", "Information Technology", 3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Asha R"))
                .andExpect(jsonPath("$.department").value("Information Technology"))
                .andExpect(jsonPath("$.yearOfStudy").value(3));
    }

    @Test
    void deleteStudent() throws Exception {
        long id = createStudent("Asha Reddy", "asha@college.edu", "CS2041");

        mvc.perform(delete("/api/students/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/students/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidInput() throws Exception {
        mvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("", "not-an-email", "x", "CSE", 7)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.rollNo").exists())
                .andExpect(jsonPath("$.fieldErrors.yearOfStudy").exists());
    }

    @Test
    void rejectsDuplicateRollNumber() throws Exception {
        createStudent("Asha Reddy", "asha@college.edu", "CS2041");

        mvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Someone Else", "other@college.edu", "cs2041", "Computer Science", 2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.fieldErrors.rollNo").exists());
    }

    @Test
    void unknownIdReturns404() throws Exception {
        mvc.perform(get("/api/students/{id}", 9999)).andExpect(status().isNotFound());
    }
}
