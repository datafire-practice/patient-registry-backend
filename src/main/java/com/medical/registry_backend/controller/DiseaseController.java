package com.medical.registry_backend.controller;

import com.medical.registry_backend.entity.Disease;
import com.medical.registry_backend.service.DiseaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients/{patientId}/diseases")
@RequiredArgsConstructor
public class DiseaseController {
    private final DiseaseService diseaseService;

    @Operation(summary = "Получить список заболеваний пациента с пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заболеваний успешно возвращён"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден")
    })
    @GetMapping
    public ResponseEntity<Page<Disease>> getDiseasesByPatientId(@PathVariable Long patientId, Pageable pageable) {
        return ResponseEntity.ok(diseaseService.getDiseasesByPatientId(patientId, pageable));
    }

    @Operation(summary = "Получить заболевание по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заболевание найдено"),
            @ApiResponse(responseCode = "404", description = "Заболевание или пациент не найдены")
    })
    @GetMapping("/{diseaseId}")
    public ResponseEntity<Disease> getDiseaseById(@PathVariable Long patientId, @PathVariable Long diseaseId) {
        return ResponseEntity.ok(diseaseService.getDiseaseById(diseaseId));
    }

    @Operation(summary = "Добавить заболевание пациенту")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заболевание успешно добавлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден")
    })
    @PostMapping
    public ResponseEntity<Disease> addDisease(@PathVariable Long patientId, @Valid @RequestBody Disease disease) {
        return ResponseEntity.ok(diseaseService.saveDisease(patientId, disease));
    }

    @Operation(summary = "Обновить заболевание")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заболевание успешно обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Заболевание или пациент не найдены")
    })
    @PutMapping("/{diseaseId}")
    public ResponseEntity<Disease> updateDisease(@PathVariable Long patientId, @PathVariable Long diseaseId, @Valid @RequestBody Disease disease) {
        return ResponseEntity.ok(diseaseService.updateDisease(patientId, diseaseId, disease));
    }

    @Operation(summary = "Удалить заболевание")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заболевание успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Заболевание или пациент не найдены")
    })
    @DeleteMapping("/{diseaseId}")
    public ResponseEntity<Void> deleteDisease(@PathVariable Long patientId, @PathVariable Long diseaseId) {
        diseaseService.deleteDisease(diseaseId);
        return ResponseEntity.noContent().build();
    }
}