package com.medical.registry_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private PatientService patientService;

    private Patient samplePatient;

    @BeforeEach
    void setUp() {
        samplePatient = createSamplePatient();
        reset(patientService);
    }

    @Test
    void getAllPatients() throws Exception {
        Page<Patient> page = new PageImpl<>(Collections.singletonList(samplePatient));
        when(patientService.getAllPatients(any(PageRequest.class))).thenReturn(page);

        MvcResult result = mockMvc.perform(get("/api/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        PageImpl<Patient> responsePage = objectMapper.readValue(content, objectMapper.getTypeFactory().constructParametricType(PageImpl.class, Patient.class));

        assertNotNull(responsePage);
        assertNotNull(responsePage.getContent());
        assertEquals(1, responsePage.getContent().size());
        assertEquals("Иванов", responsePage.getContent().get(0).getLastName());
    }

    @Test
    void getPatientById() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(samplePatient);

        MvcResult result = mockMvc.perform(get("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Patient responsePatient = objectMapper.readValue(content, Patient.class);

        assertNotNull(responsePatient);
        assertEquals(1L, responsePatient.getId());
        assertEquals("Иванов", responsePatient.getLastName());
    }

    @Test
    void createPatient() throws Exception {
        when(patientService.savePatient(any(Patient.class))).thenReturn(samplePatient);

        String patientJson = objectMapper.writeValueAsString(samplePatient);

        MvcResult result = mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Patient responsePatient = objectMapper.readValue(content, Patient.class);

        assertNotNull(responsePatient);
        assertEquals("Иванов", responsePatient.getLastName());
        assertEquals("Иван", responsePatient.getFirstName());
    }

    @Test
    void updatePatient() throws Exception {
        when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(samplePatient);

        String patientJson = objectMapper.writeValueAsString(samplePatient);

        MvcResult result = mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Patient responsePatient = objectMapper.readValue(content, Patient.class);

        assertNotNull(responsePatient);
        assertEquals("Иванов", responsePatient.getLastName());
        assertEquals("Иван", responsePatient.getFirstName());
    }

    @Test
    void deletePatient() throws Exception {
        doNothing().when(patientService).deletePatient(1L);

        mockMvc.perform(delete("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private Patient createSamplePatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setLastName("Иванов");
        patient.setFirstName("Иван");
        patient.setMiddleName("Иванович");
        patient.setGender("М");
        patient.setBirthDate(LocalDate.of(1995, 1, 1));
        patient.setInsuranceNumber("1234567890123456");
        return patient;
    }
}