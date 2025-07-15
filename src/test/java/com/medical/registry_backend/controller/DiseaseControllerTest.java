//package com.medical.registry_backend.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.medical.registry_backend.entity.Disease;
//import com.medical.registry_backend.entity.Mkb10;
//import com.medical.registry_backend.entity.Patient;
//import com.medical.registry_backend.service.DiseaseService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.time.LocalDate;
//import java.util.Collections;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@ExtendWith(MockitoExtension.class)
//class DiseaseControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Mock
//    private DiseaseService diseaseService;
//
//    private Disease sampleDisease;
//    private Patient samplePatient;
//
//    @BeforeEach
//    void setUp() {
//        samplePatient = createSamplePatient();
//        sampleDisease = createSampleDisease();
//        reset(diseaseService);
//    }
//
//    @Test
//    void getDiseasesByPatientId() throws Exception {
//        Page<Disease> page = new PageImpl<>(Collections.singletonList(sampleDisease));
//        when(diseaseService.getDiseasesByPatientId(eq(1L), any(PageRequest.class))).thenReturn(page);
//
//        MvcResult result = mockMvc.perform(get("/api/patients/1/diseases")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString();
//        PageImpl<Disease> responsePage = objectMapper.readValue(content, objectMapper.getTypeFactory().constructParametricType(PageImpl.class, Disease.class));
//
//        assertNotNull(responsePage);
//        assertNotNull(responsePage.getContent());
//        assertEquals(1, responsePage.getContent().size());
//        assertEquals("Приём лекарств", responsePage.getContent().get(0).getPrescriptions());
//    }
//
//    @Test
//    void getDiseaseById() throws Exception {
//        when(diseaseService.getDiseaseById(1L)).thenReturn(sampleDisease);
//
//        MvcResult result = mockMvc.perform(get("/api/patients/1/diseases/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString();
//        Disease responseDisease = objectMapper.readValue(content, Disease.class);
//
//        assertNotNull(responseDisease);
//        assertEquals("Приём лекарств", responseDisease.getPrescriptions());
//    }
//
//    @Test
//    void addDisease() throws Exception {
//        when(diseaseService.saveDisease(eq(1L), any(Disease.class))).thenReturn(sampleDisease);
//
//        String diseaseJson = objectMapper.writeValueAsString(sampleDisease);
//
//        MvcResult result = mockMvc.perform(post("/api/patients/1/diseases")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(diseaseJson))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString();
//        Disease responseDisease = objectMapper.readValue(content, Disease.class);
//
//        assertNotNull(responseDisease);
//        assertEquals("Приём лекарств", responseDisease.getPrescriptions());
//    }
//
//    @Test
//    void updateDisease() throws Exception {
//        when(diseaseService.updateDisease(eq(1L), eq(1L), any(Disease.class))).thenReturn(sampleDisease);
//
//        String diseaseJson = objectMapper.writeValueAsString(sampleDisease);
//
//        MvcResult result = mockMvc.perform(put("/api/patients/1/diseases/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(diseaseJson))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString();
//        Disease responseDisease = objectMapper.readValue(content, Disease.class);
//
//        assertNotNull(responseDisease);
//        assertEquals("Приём лекарств", responseDisease.getPrescriptions());
//    }
//
//    @Test
//    void deleteDisease() throws Exception {
//        doNothing().when(diseaseService).deleteDisease(1L);
//
//        mockMvc.perform(delete("/api/patients/1/diseases/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent());
//    }
//
//    private Patient createSamplePatient() {
//        Patient patient = new Patient();
//        patient.setId(1L);
//        return patient;
//    }
//
//    private Disease createSampleDisease() {
//        Disease disease = new Disease();
//        disease.setId(1L);
//        disease.setPatient(samplePatient);
//        Mkb10 mkb10 = new Mkb10();
//        mkb10.setCode("A00.0");
//        mkb10.setName("Холера");
//        disease.setMkb10(mkb10);
//        disease.setStartDate(LocalDate.of(2023, 1, 1));
//        disease.setPrescriptions("Приём лекарств");
//        disease.setSickLeaveIssued(true);
//        return disease;
//    }
//}
