package com.medical.registry_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.service.Mkb10Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // Ensure this import is present
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class Mkb10ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private Mkb10Service mkb10Service;

    @BeforeEach
    void setUp() {
        Mkb10 mkb10 = new Mkb10();
        mkb10.setCode("A00.0");
        mkb10.setName("Холера");
        List<Mkb10> mkb10List = Arrays.asList(mkb10);
        when(mkb10Service.getAllMkb10()).thenReturn(mkb10List);
    }

    @Test
    void getAllMkb10() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/dictionary/mkb10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Mkb10[] mkb10Array = objectMapper.readValue(content, Mkb10[].class);

        assertNotNull(mkb10Array);
        assertTrue(mkb10Array.length > 0);
        assertTrue(Arrays.stream(mkb10Array).anyMatch(mkb10 -> "A00.0".equals(mkb10.getCode())));
    }
}