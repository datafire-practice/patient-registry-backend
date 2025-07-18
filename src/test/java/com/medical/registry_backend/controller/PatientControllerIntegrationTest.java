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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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

    private Patient samplePatient;

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

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.lastName").value("Генри"))
                .andExpect(jsonPath("$.diseases[0].mkb10.code").value("A00.0"));

        samplePatient = createSamplePatient();
    }
//    @BeforeEach
//    public void setUp() throws Exception {
//        // Настройка ObjectMapper с модулем для Java 8 Time
//        objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//
//        // Очистка базы данных перед каждым тестом
//        try {
//            patientRepository.deleteAll();
//            mkb10Repository.deleteAll();
//            diseaseRepository.deleteAll();
//            logger.info("Database cleared successfully");
//        } catch (Exception e) {
//            logger.error("Failed to clear database", e);
//            throw new RuntimeException("Failed to clear database", e);
//        }
//
//        // Создание и сохранение Mkb10
//        Mkb10 mkb10 = new Mkb10();
//        mkb10.setCode("A00.0");
//        mkb10.setName("Test Disease");
//        mkb10Repository.save(mkb10);
//
//        // Создание тестового пациента
//        samplePatient = new Patient();
//        samplePatient.setLastName("Генри");
//        samplePatient.setFirstName("Ревирс");
//        samplePatient.setGender("М");
//        samplePatient.setBirthDate(LocalDate.of(1990, 1, 1));
//        samplePatient.setInsuranceNumber("1234567890123456");
//
//        Disease disease = new Disease();
//        disease.setStartDate(LocalDate.now());
//        disease.setPrescriptions("Test prescription");
//        disease.setSickLeaveIssued(false);
//        disease.setPatient(samplePatient);
//        disease.setMkb10(mkb10);
//        samplePatient.getDiseases().add(disease);
//
//        // Сохранение данных через сервис
//        try {
//            patientService.savePatient(samplePatient);
//            logger.info("Patient saved successfully via service: {}", samplePatient.getId());
//        } catch (Exception e) {
//            logger.error("Failed to save patient via service", e);
//            throw new RuntimeException("Failed to save patient via service", e);
//        }
//
//        // Проверка сохранения через контроллер
//        try {
//            String jsonPatient = objectMapper.writeValueAsString(samplePatient);
//            logger.info("Sending patient data to controller: {}", jsonPatient);
//            mockMvc.perform(MockMvcRequestBuilders.post("/patients")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(jsonPatient))
//                    .andDo(MockMvcResultHandlers.print())
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.lastName").value("Генри"))
//                    .andExpect(jsonPath("$.firstName").value("Ревирс"));
//            logger.info("Patient saved successfully via controller");
//        } catch (Exception e) {
//            logger.error("Failed to save patient via controller", e);
//            throw new RuntimeException("Failed to save patient via controller", e);
//        }
//    }
//    public void setUp() throws Exception {
//        objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//
//        // Очистка базы данных перед каждым тестом
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.delete("/patients/clear"));
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to clear patients", e);
//        }
//        // Создание тестовых данных
//        Patient patient = new Patient();
//        patient.setLastName("Генри");
//        patient.setFirstName("Ревирс");
//        patient.setGender("М");
//        patient.setBirthDate(LocalDate.of(1990, 1, 1));
//        patient.setInsuranceNumber("1234567890123456");
//
//        patient.setDiseases(new java.util.ArrayList<>()); // Инициализация списка diseases
//        Mkb10 mkb10 = new Mkb10();
//        mkb10.setCode("A00.0");
//        mkb10.setName("Test Disease");
//
//        Disease disease = new Disease();
//        disease.setStartDate(LocalDate.now());
//        disease.setPrescriptions("Test prescription");
//        disease.setSickLeaveIssued(false);
//        disease.setPatient(patient);
//        patient.getDiseases().add(disease);
//
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.post("/patients")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(patient)));
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to save patient", e);
//        }
//    }
//    void setUp() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.delete("/patients/clear"));
//        diseaseRepository.deleteAll();
//        patientRepository.deleteAll();
//        mkb10Repository.deleteAll();
//
//        Mkb10 mkb10 = new Mkb10();
//        mkb10.setCode("A00.0");
//        mkb10.setName("Test Disease");
//        mkb10Repository.save(mkb10);
//
//        Patient patient = new Patient();
//        patient.setLastName("Генри");
//        patient.setFirstName("Ревирс");
//        patient.setGender("М");
//        patient.setBirthDate(LocalDate.of(1990, 1, 1));
//        patient.setInsuranceNumber("1234567890123456");
//        patientRepository.save(patient);
//
//        Disease disease = new Disease();
//        disease.setPatient(patient);
//        disease.setMkb10(mkb10);
//        disease.setStartDate(LocalDate.now());
//        disease.setPrescriptions("Test prescription");
//        disease.setSickLeaveIssued(false);
//        diseaseRepository.save(disease);
//
//        samplePatient = createSamplePatient();
//    }

    @Test
    void createPatient() throws Exception {
        String patientJson = """
                {
                  "lastName": "Иванов",
                  "firstName": "Иван",
                  "middleName": "Иванович",
                  "gender": "М",
                  "birthDate": "1995-01-01",
                  "insuranceNumber": "9876543210987654"
                }
                """;

        MvcResult result = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isOk())
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
        System.out.println("=== Custom Message: Starting getAllPatients test ===!!!!!!!!!!!!!!!!!!!!!!!!!!!!"); // Ваша уникальная строка
        mockMvc.perform(get("/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()) // Добавляет вывод ответа в консоль
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").exists())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].lastName").value("Генри"))
                .andExpect(jsonPath("$.content[0].firstName").value("Ревирс"));
    }

    @Test
    void getPatientById() throws Exception {
        Long patientId = samplePatient.getId();
        if (patientId == null) {
            throw new IllegalStateException("Sample patient ID is null, save operation failed");
        }

        MvcResult result = mockMvc.perform(get("/patients/" + patientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Генри"))
                .andExpect(jsonPath("$.firstName").value("Ревирс"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Patient responsePatient = objectMapper.readValue(content, Patient.class);

        assertNotNull(responsePatient);
        assertEquals(patientId, responsePatient.getId());
        assertEquals("Генри", responsePatient.getLastName());
        assertEquals("Ревирс", responsePatient.getFirstName());
    }

    @Test
    void updatePatient() throws Exception {
        Long patientId = samplePatient.getId();
        if (patientId == null) {
            throw new IllegalStateException("Sample patient ID is null, save operation failed");
        }

        String updatedPatientJson = """
                {
                  "lastName": "Петров",
                  "firstName": "Петр",
                  "middleName": "Петрович",
                  "gender": "М",
                  "birthDate": "1990-02-02",
                  "insuranceNumber": "6543210987654321"
                }
                """;

        MvcResult result = mockMvc.perform(put("/patients/" + patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPatientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Петров"))
                .andExpect(jsonPath("$.firstName").value("Петр"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Patient responsePatient = objectMapper.readValue(content, Patient.class);

        assertNotNull(responsePatient);
        assertEquals(patientId, responsePatient.getId());
        assertEquals("Петров", responsePatient.getLastName());
        assertEquals("Петр", responsePatient.getFirstName());
        assertEquals("Петрович", responsePatient.getMiddleName());
        assertEquals("М", responsePatient.getGender());
        assertEquals(LocalDate.of(1990, 2, 2), responsePatient.getBirthDate());
        assertEquals("6543210987654321", responsePatient.getInsuranceNumber());
    }

    @Test
    void updatePatientWithInvalidData() throws Exception {
        Long patientId = samplePatient.getId();
        if (patientId == null) {
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

        MvcResult result = mockMvc.perform(put("/patients/" + patientId)
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
        Long patientId = samplePatient.getId();
        if (patientId == null) {
            throw new IllegalStateException("Sample patient ID is null, save operation failed");
        }

        mockMvc.perform(delete("/patients/" + patientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThrows(EntityNotFoundException.class, () -> patientService.getPatientById(patientId));
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
        patient.setInsuranceNumber("9876543210987654"); // Уникальный номер
        return patient;
    }
}