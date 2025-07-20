//package com.medical.registry_backend.controller;
//
//import com.medical.registry_backend.entity.Mkb10;
//import com.medical.registry_backend.repository.Mkb10Repository;
//import com.medical.registry_backend.service.Mkb10ServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//public class Mkb10ControllerCacheIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private Mkb10Repository mkb10Repository;
//
//    @Autowired
//    private Mkb10ServiceImpl mkb10Service;
//
//    private final String testCode = "A00.0";
//
//    @BeforeEach
//    void setUp() {
//        mkb10Repository.deleteAll();
//
//        Mkb10 mkb10 = new Mkb10();
//        mkb10.setCode(testCode);
//        mkb10.setName("Cholera due to Vibrio cholerae");
//        mkb10Repository.save(mkb10);
//    }
//
//    @Test
//    void testManualCaffeineCachingOnGetByCode() throws Exception {
//        // Убедимся, что до запроса кэш пуст (get напрямую из кэша вернёт null)
//        Mkb10 cachedBefore = mkb10Service.getMkb10ByCode(testCode);
//        assertNotNull(cachedBefore, "Первый вызов должен вернуть объект");
//
//        // Повторный вызов (из кэша Caffeine внутри сервиса)
//        Mkb10 cachedAfter = mkb10Service.getMkb10ByCode(testCode);
//        assertSame(cachedBefore, cachedAfter, "Должен быть возвращён тот же объект из кэша");
//
//        // Теперь вызываем через контроллер и убеждаемся, что всё корректно работает
//        mockMvc.perform(get("/dictionary/mkb10/" + testCode)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(testCode))
//                .andExpect(jsonPath("$.name").value("Cholera due to Vibrio cholerae"));
//    }
//}
