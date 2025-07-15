package com.medical.registry_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setLastName("Иванов");
        patient.setFirstName("Иван");
        patient.setGender("М");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setInsuranceNumber("1234567890123456");
    }

    @Test
    void getAllPatients() throws Exception {
        mockMvc.perform(get("/api/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getPatientById() throws Exception {
        Patient savedPatient = patientService.savePatient(patient);
        mockMvc.perform(get("/api/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Иванов"));
    }

    @Test
    void createPatient() throws Exception {
        String patientJson = objectMapper.writeValueAsString(patient);
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Иванов"));
    }

    @Test
    void updatePatient() throws Exception {
        Patient savedPatient = patientService.savePatient(patient);
        patient.setLastName("Петров");
        String patientJson = objectMapper.writeValueAsString(patient);
        mockMvc.perform(put("/api/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Петров"));
    }

    @Test
    void deletePatient() throws Exception {
        Patient savedPatient = patientService.savePatient(patient);
        mockMvc.perform(delete("/api/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}