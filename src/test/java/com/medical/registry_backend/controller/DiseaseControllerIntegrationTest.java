package com.medical.registry_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.registry_backend.entity.Disease;
import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.repository.DiseaseRepository;
import com.medical.registry_backend.repository.Mkb10Repository;
import com.medical.registry_backend.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DiseaseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private Mkb10Repository mkb10Repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long samplePatientId;
    private Long sampleDiseaseId;
    private Mkb10 sampleMkb10;
    @BeforeEach
    void setUp() throws Exception {
        patientRepository.deleteAll();
        diseaseRepository.deleteAll();
        mkb10Repository.deleteAll();

        sampleMkb10 = new Mkb10();
        sampleMkb10.setCode("A00.0");
        sampleMkb10.setName("Test Disease");
        mkb10Repository.save(sampleMkb10);
        System.out.println("Saved Mkb10: code=" + sampleMkb10.getCode());

        Patient patient = new Patient();
        patient.setLastName("Генри");
        patient.setFirstName("Ревирс");
        patient.setGender("М");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setInsuranceNumber("1234567890123456");
        patientRepository.save(patient);
        samplePatientId = patient.getId();
        System.out.println("Saved Patient: id=" + samplePatientId + ", lastName=" + patient.getLastName());

        Disease disease = new Disease();
        disease.setPatient(patient);
        disease.setMkb10(sampleMkb10);
        disease.setStartDate(LocalDate.now());
        disease.setPrescriptions("Test prescription");
        disease.setSickLeaveIssued(false);
        diseaseRepository.save(disease);
        sampleDiseaseId = disease.getId();
        Disease savedDisease = diseaseRepository.findById(sampleDiseaseId).orElse(null);
        System.out.println("Saved Disease: id=" + (savedDisease != null ? savedDisease.getId() : null) +
                ", prescriptions=" + (savedDisease != null ? savedDisease.getPrescriptions() : null));
    }
    @Test
    void createDisease() throws Exception {
        String diseaseJson = """
                {
                  "patient": {"id": %d},
                  "mkb10": {"code": "A00.0"},
                  "startDate": "%s",
                  "endDate": null,
                  "prescriptions": "New prescription",
                  "sickLeaveIssued": true
                }
                """.formatted(samplePatientId, LocalDate.now().toString());

        MvcResult result = mockMvc.perform(post("/patient/" + samplePatientId + "/disease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(diseaseJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.prescriptions").value("New prescription"))
                .andExpect(jsonPath("$.mkb10.code").value("A00.0"))
                .andExpect(jsonPath("$.sickLeaveIssued").value(true))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Create Disease Response: " + content);
        Disease responseDisease = objectMapper.readValue(content, Disease.class);

        assertNotNull(responseDisease);
        assertNotNull(responseDisease.getId());
        assertEquals("New prescription", responseDisease.getPrescriptions());
        assertEquals("A00.0", responseDisease.getMkb10().getCode());
        assertTrue(responseDisease.isSickLeaveIssued());
    }
    @Test
    void updateDisease() throws Exception {
        String updatedDiseaseJson = """
        {
          "patient": {"id": %d},
          "mkb10": {"code": "A00.0"},
          "startDate": "%s",
          "endDate": "%s",
          "prescriptions": "Updated prescription",
          "sickLeaveIssued": true
        }
        """.formatted(samplePatientId, LocalDate.now().toString(), LocalDate.now().toString());

        MvcResult result = mockMvc.perform(put("/patient/" + samplePatientId + "/disease/" + sampleDiseaseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDiseaseJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleDiseaseId))
                .andExpect(jsonPath("$.prescriptions").value("Updated prescription"))
                .andExpect(jsonPath("$.mkb10.code").value("A00.0"))
                .andExpect(jsonPath("$.endDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.sickLeaveIssued").value(true))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Update Disease Response: " + content);
        Disease responseDisease = objectMapper.readValue(content, Disease.class);

        assertNotNull(responseDisease);
        assertEquals(sampleDiseaseId, responseDisease.getId());
        assertEquals("Updated prescription", responseDisease.getPrescriptions());
        assertEquals(LocalDate.now(), responseDisease.getEndDate());
        assertTrue(responseDisease.isSickLeaveIssued());
    }
    @Test
    void getAllDiseases() throws Exception {
        System.out.println("Testing getAllDiseases for patient ID: " + samplePatientId);
        MvcResult result = mockMvc.perform(get("/patient/" + samplePatientId + "/disease")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].prescriptions").value("Test prescription"))
                .andExpect(jsonPath("$.content[0].mkb10.code").value("A00.0"))
                .andExpect(jsonPath("$.content[0].sickLeaveIssued").value(false))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("getAllDiseases Response: " + content);
    }

    @Test
    void getDiseaseById() throws Exception {
        mockMvc.perform(get("/patient/" + samplePatientId + "/disease/" + sampleDiseaseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleDiseaseId))
                .andExpect(jsonPath("$.prescriptions").value("Test prescription"))
                .andExpect(jsonPath("$.mkb10.code").value("A00.0"))
                .andExpect(jsonPath("$.sickLeaveIssued").value(false));
    }



    @Test
    void deleteDisease() throws Exception {
        mockMvc.perform(delete("/patient/" + samplePatientId + "/disease/" + sampleDiseaseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/patient/" + samplePatientId + "/disease/" + sampleDiseaseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Заболевание с ID " + sampleDiseaseId + " не найдено"));
    }

    @Test
    void createDiseaseWithInvalidData() throws Exception {
        String invalidDiseaseJson = """
                {
                  "mkb10": {"code": "A00.0"},
                  "startDate": "2026-01-01",
                  "endDate": null,
                  "prescriptions": "Test prescription",
                  "sickLeaveIssued": true
                }
                """;

        mockMvc.perform(post("/patient/" + samplePatientId + "/disease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDiseaseJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.startDate").value("Дата начала болезни не должна быть в будущем"));
    }

    @Test
    void createDiseaseWithMissingFields() throws Exception {
        String invalidDiseaseJson = """
                {
                  "mkb10": {"code": "A00.0"},
                  "startDate": "%s",
                  "endDate": null,
                  "prescriptions": "",
                  "sickLeaveIssued": null
                }
                """.formatted(LocalDate.now().toString());

        mockMvc.perform(post("/patient/" + samplePatientId + "/disease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDiseaseJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.prescriptions").value("Назначения не должны быть пустыми"))
                .andExpect(jsonPath("$.sickLeaveIssued").value("Статус выдачи больничного листа обязателен"));
    }

    @Test
    void getDiseaseByIdNotFound() throws Exception {
        mockMvc.perform(get("/patient/" + samplePatientId + "/disease/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Заболевание с ID 999 не найдено"));
    }

    @Test
    void getDiseaseByIdWrongPatient() throws Exception {
        Patient anotherPatient = new Patient();
        anotherPatient.setLastName("Смирнов");
        anotherPatient.setFirstName("Алексей");
        anotherPatient.setGender("М");
        anotherPatient.setBirthDate(LocalDate.of(1980, 1, 1));
        anotherPatient.setInsuranceNumber("9876543210987654");
        patientRepository.save(anotherPatient);
        Long anotherPatientId = anotherPatient.getId();

        mockMvc.perform(get("/patient/" + anotherPatientId + "/disease/" + sampleDiseaseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Заболевание не принадлежит пациенту с ID " + anotherPatientId));
    }

    @Test
    void deleteDiseaseNotFound() throws Exception {
        mockMvc.perform(delete("/patient/" + samplePatientId + "/disease/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Заболевание с ID 999 не найдено"));
    }

    @Test
    void deleteDiseaseWrongPatient() throws Exception {
        Patient anotherPatient = new Patient();
        anotherPatient.setLastName("Смирнов");
        anotherPatient.setFirstName("Алексей");
        anotherPatient.setGender("М");
        anotherPatient.setBirthDate(LocalDate.of(1980, 1, 1));
        anotherPatient.setInsuranceNumber("9876543210987654");
        patientRepository.save(anotherPatient);
        Long anotherPatientId = anotherPatient.getId();

        mockMvc.perform(delete("/patient/" + anotherPatientId + "/disease/" + sampleDiseaseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Заболевание не принадлежит пациенту с ID " + anotherPatientId));
    }
}