package com.medical.registry_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.medical.registry_backend.entity.Disease;
import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.repository.DiseaseRepository;
import com.medical.registry_backend.repository.Mkb10Repository;
import com.medical.registry_backend.repository.PatientRepository;
import com.medical.registry_backend.service.PatientService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientControllerIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(PatientControllerIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private Mkb10Repository mkb10Repository;

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientService patientService;

    private Long samplePatientId;

    @BeforeEach
    void setUp() throws Exception {
        patientRepository.deleteAll();
        diseaseRepository.deleteAll();
        mkb10Repository.deleteAll();

        Mkb10 mkb10 = new Mkb10();
        mkb10.setCode("A00.0");
        mkb10.setName("Test Disease");
        mkb10Repository.save(mkb10);

        Patient patient = new Patient();
        patient.setLastName("Генри");
        patient.setFirstName("Ревирс");
        patient.setGender("М");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setInsuranceNumber("1234567890123456");

        Disease disease = new Disease();
        disease.setMkb10(mkb10);
        disease.setStartDate(LocalDate.now());
        disease.setPrescriptions("Test prescription");
        disease.setSickLeaveIssued(false);
        patient.setDiseases(List.of(disease));

        MvcResult result = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.lastName").value("Генри"))
                .andExpect(jsonPath("$.diseases[0].mkb10.code").value("A00.0"))
                .andReturn();

        Patient savedPatient = objectMapper.readValue(result.getResponse().getContentAsString(), Patient.class);
        samplePatientId = savedPatient.getId();
        if (samplePatientId == null) {
            throw new IllegalStateException("Sample patient ID is null, save operation failed");
        }
    }

    @Test
    void createPatient() throws Exception {
        String patientJson = """
                {
                  "lastName": "Иванов",
                  "firstName": "Иван",
                  "middleName": "Иванович",
                  "gender": "М",
                  "birthDate": "1995-01-01",
                  "insuranceNumber": "9876543210987654",
                  "diseases": []
                }
                """;

        MvcResult result = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lastName").value("Иванов"))
                .andExpect(jsonPath("$.firstName").value("Иван"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Patient responsePatient = objectMapper.readValue(content, Patient.class);

        assertNotNull(responsePatient);
        assertNotNull(responsePatient.getId());
        assertEquals("Иванов", responsePatient.getLastName());
        assertEquals("Иван", responsePatient.getFirstName());
        assertEquals("Иванович", responsePatient.getMiddleName());
        assertEquals("М", responsePatient.getGender());
        assertEquals(LocalDate.of(1995, 1, 1), responsePatient.getBirthDate());
        assertEquals("9876543210987654", responsePatient.getInsuranceNumber());
    }

    @Test
    void getAllPatients() throws Exception {
        System.out.println("=== Custom Message: Starting getAllPatients test ===!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        mockMvc.perform(get("/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").exists())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].lastName").value("Генри"))
                .andExpect(jsonPath("$.content[0].firstName").value("Ревирс"))
                .andExpect(jsonPath("$.content[0].diseases[0].mkb10.code").value("A00.0"));
    }

    @Test
    void getPatientById() throws Exception {
        if (samplePatientId == null) {
            throw new IllegalStateException("Sample patient ID is null, save operation failed");
        }

        MvcResult result = mockMvc.perform(get("/patients/" + samplePatientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Генри"))
                .andExpect(jsonPath("$.firstName").value("Ревирс"))
                .andExpect(jsonPath("$.diseases[0].mkb10.code").value("A00.0"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Patient responsePatient = objectMapper.readValue(content, Patient.class);

        assertNotNull(responsePatient);
        assertEquals(samplePatientId, responsePatient.getId());
        assertEquals("Генри", responsePatient.getLastName());
        assertEquals("Ревирс", responsePatient.getFirstName());
    }

    @Test
    void updatePatient() throws Exception {
        if (samplePatientId == null) {
            throw new IllegalStateException("Sample patient ID is null, save operation failed");
        }

        String updatedPatientJson = """
                {
                  "lastName": "Петров",
                  "firstName": "Петр",
                  "middleName": "Петрович",
                  "gender": "М",
                  "birthDate": "1990-02-02",
                  "insuranceNumber": "6543210987654321",
                  "diseases": []
                }
                """;

        MvcResult result = mockMvc.perform(put("/patients/" + samplePatientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPatientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Петров"))
                .andExpect(jsonPath("$.firstName").value("Петр"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Patient responsePatient = objectMapper.readValue(content, Patient.class);

        assertNotNull(responsePatient);
        assertEquals(samplePatientId, responsePatient.getId());
        assertEquals("Петров", responsePatient.getLastName());
        assertEquals("Петр", responsePatient.getFirstName());
        assertEquals("Петрович", responsePatient.getMiddleName());
        assertEquals("М", responsePatient.getGender());
        assertEquals(LocalDate.of(1990, 2, 2), responsePatient.getBirthDate());
        assertEquals("6543210987654321", responsePatient.getInsuranceNumber());
    }

    @Test
    void updatePatientWithInvalidData() throws Exception {
        if (samplePatientId == null) {
            throw new IllegalStateException("Sample patient ID is null, save operation failed");
        }

        String invalidPatientJson = """
                {
                  "lastName": "Petrov123",
                  "firstName": "Петр",
                  "middleName": "Петрович",
                  "gender": "М",
                  "birthDate": "1990-02-02",
                  "insuranceNumber": "6543210987654321"
                }
                """;

        MvcResult result = mockMvc.perform(put("/patients/" + samplePatientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPatientJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.lastName").value("Фамилия должна содержать только кириллицу и дефис"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Response body (updatePatientWithInvalidData): " + content);
    }

    @Test
    void deletePatient() throws Exception {
        if (samplePatientId == null) {
            throw new IllegalStateException("Sample patient ID is null, save operation failed");
        }

        mockMvc.perform(delete("/patients/" + samplePatientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThrows(EntityNotFoundException.class, () -> patientService.getPatientById(samplePatientId));
    }

    @Test
    void createPatientWithInvalidData() throws Exception {
        String invalidPatientJson = """
                {
                  "lastName": "Ivanov123",
                  "firstName": "Иван",
                  "middleName": "Иванович",
                  "gender": "М",
                  "birthDate": "1995-01-01",
                  "insuranceNumber": "9876543210987654"
                }
                """;

        MvcResult result = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPatientJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.lastName").value("Фамилия должна содержать только кириллицу и дефис"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Response body (createPatientWithInvalidData): " + content);
    }

    @Test
    void getPatientByIdNotFound() throws Exception {
        mockMvc.perform(get("/patients/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пациент с ID 999 не найден"));
    }

    @Test
    void deletePatientNotFound() throws Exception {
        mockMvc.perform(delete("/patients/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пациент с ID 999 не найден"));
    }

    @Test
    void createPatientWithDuplicateInsuranceNumber() throws Exception {
        String duplicatePatientJson = """
                {
                  "lastName": "Смирнов",
                  "firstName": "Алексей",
                  "middleName": "Алексеевич",
                  "gender": "М",
                  "birthDate": "1980-10-10",
                  "insuranceNumber": "1234567890123456"
                }
                """;

        MvcResult result = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicatePatientJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Data integrity violation"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Response body (createPatientWithDuplicateInsuranceNumber): " + content);
    }

    private Patient createSamplePatient() {
        Patient patient = new Patient();
        patient.setLastName("Иванов");
        patient.setFirstName("Иван");
        patient.setMiddleName("Иванович");
        patient.setGender("М");
        patient.setBirthDate(LocalDate.of(1995, 1, 1));
        patient.setInsuranceNumber("9876543210987654");
        return patientService.savePatient(patient);
    }
}