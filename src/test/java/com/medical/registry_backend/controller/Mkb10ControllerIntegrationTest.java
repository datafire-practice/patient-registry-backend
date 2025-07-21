package com.medical.registry_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.repository.DiseaseRepository;
import com.medical.registry_backend.repository.Mkb10Repository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Mkb10ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Mkb10Repository mkb10Repository;

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String sampleMkb10Code;

    @BeforeEach
    void setUp() throws Exception {
        diseaseRepository.deleteAll();
        mkb10Repository.deleteAll();

        Mkb10 mkb10 = new Mkb10();
        mkb10.setCode("A00.0");
        mkb10.setName("Cholera due to Vibrio cholerae");
        mkb10Repository.save(mkb10);
        sampleMkb10Code = mkb10.getCode();
        System.out.println("Saved Mkb10: code=" + sampleMkb10Code + ", name=" + mkb10.getName());

        Mkb10 mkb10_2 = new Mkb10();
        mkb10_2.setCode("B01.0");
        mkb10_2.setName("Varicella meningitis");
        mkb10Repository.save(mkb10_2);
        System.out.println("Saved Mkb10: code=" + mkb10_2.getCode() + ", name=" + mkb10_2.getName());
    }

    @Test
    void getAllMkb10() throws Exception {
        MvcResult result = mockMvc.perform(get("/dictionary/mkb10")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].code").value("A00.0"))
                .andExpect(jsonPath("$.content[0].name").value("Cholera due to Vibrio cholerae"))
                .andExpect(jsonPath("$.content[1].code").value("B01.0"))
                .andExpect(jsonPath("$.content[1].name").value("Varicella meningitis"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("getAllMkb10 Response: " + content);
    }

    @Test
    void getMkb10ByCode() throws Exception {
        MvcResult result = mockMvc.perform(get("/dictionary/mkb10/" + sampleMkb10Code)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(sampleMkb10Code))
                .andExpect(jsonPath("$.name").value("Cholera due to Vibrio cholerae"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("getMkb10ByCode Response: " + content);

        Mkb10 responseMkb10 = objectMapper.readValue(content, Mkb10.class);
        assertNotNull(responseMkb10);
        assertEquals(sampleMkb10Code, responseMkb10.getCode());
        assertEquals("Cholera due to Vibrio cholerae", responseMkb10.getName());
    }

    @Test
    void getMkb10ByCodeNotFound() throws Exception {
        String invalidCode = "Z99.9";
        mockMvc.perform(get("/dictionary/mkb10/" + invalidCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMkb10Data() throws Exception {
        MvcResult result = mockMvc.perform(post("/dictionary/mkb10/update")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string("Обновление справочника МКБ-10 инициировано"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("updateMkb10Data Response: " + content);
    }
}