package com.medical.registry_backend.controller;

import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.service.Mkb10Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dictionary/mkb10")
@RequiredArgsConstructor
public class Mkb10Controller {
    private static final Logger logger = LoggerFactory.getLogger(Mkb10Controller.class);
    private final Mkb10Service mkb10Service;

    /**
     * Получает все записи MKB10 с поддержкой пагинации.
     * @param pageable Параметры пагинации (страница, размер, сортировка)
     * @return Страница с записями MKB10
     */
    @GetMapping
    public ResponseEntity<Page<Mkb10>> getAllMkb10(Pageable pageable) {
        logger.info("Fetching MKB10 data with pageable: {}", pageable);
        Page<Mkb10> page = mkb10Service.getAllMkb10(pageable);
        if (page.isEmpty()) {
            logger.warn("No MKB10 data found for pageable: {}", pageable);
        }
        System.out.println("Get_Controller_MKB10&&&&");
        return ResponseEntity.ok(page);
    }

    /**
     * Получает запись MKB10 по коду.
     * @param code Код MKB10 (например, A00.0)
     * @return Запись MKB10 или 404, если не найдена
     */
    @GetMapping("/{code}")
    public ResponseEntity<Mkb10> getMkb10ByCode(@PathVariable String code) {
        logger.info("Fetching MKB10 data for code: {}", code);
        Mkb10 mkb10 = mkb10Service.getMkb10ByCode(code);
        if (mkb10 == null) {
            logger.warn("MKB10 data not found for code: {}", code);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mkb10);
    }

    /**
     * Запускает ручное обновление справочника MKB10.
     * @return Сообщение об успешном запуске обновления
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateMkb10Data() {
        logger.info("Initiating manual MKB10 data update");
        mkb10Service.updateMkb10Data();
        return ResponseEntity.ok("MKB10 data update initiated");
    }
}