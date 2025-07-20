package com.medical.registry_backend.controller;

import com.medical.registry_backend.entity.Disease;
import com.medical.registry_backend.exception.GlobalExceptionHandler;
import com.medical.registry_backend.service.DiseaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient/{patient_id}/disease")
@RequiredArgsConstructor
public class DiseaseController {

    private final DiseaseService diseaseService;
    private static final Logger logger = LoggerFactory.getLogger(DiseaseController.class);

    @Operation(summary = "Получить список заболеваний пациента с пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заболеваний успешно возвращён",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class),
                            examples = @ExampleObject(name = "Пример ответа", value = """
                                    {
                                        "content": [
                                            {
                                                "id": 1,
                                                "mkb10": {
                                                    "code": "A00.0",
                                                    "name": "Холера"
                                                },
                                                "startDate": "2025-01-15",
                                                "endDate": "2025-02-10",
                                                "prescriptions": "Постельный режим, жаропонижающие препараты",
                                                "sickLeaveIssued": true
                                            },
                                            {
                                                "id": 2,
                                                "mkb10": {
                                                    "code": "B02",
                                                    "name": "Грипп"
                                                },
                                                "startDate": "2025-03-01",
                                                "endDate": "2025-03-15",
                                                "prescriptions": "Жаропонижающее, обильное питье",
                                                "sickLeaveIssued": false
                                            }
                                        ],
                                        "pageable": {
                                            "pageNumber": 0,
                                            "pageSize": 10
                                        },
                                        "totalElements": 2,
                                        "totalPages": 1
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Пациент не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Пациент не найден", value = """
                                    {
                                        "message": "Пациент с ID 1 не найден"
                                    }
                                    """)))
    })
    @GetMapping
    public ResponseEntity<Page<Disease>> getDiseasesByPatientId(
            @Parameter(description = "ID пациента", example = "1") @PathVariable("patient_id") Long patientId,
            @Parameter(description = "Параметры пагинации (передаются как query-параметры: ?page=0&size=10&sort=id,asc)",
                    examples = {
                            @ExampleObject(name = "Пример параметров пагинации", value = """
                                    {
                                        "page": 0,
                                        "size": 10,
                                        "sort": "id,asc"
                                    }
                                    """)
                    })
            Pageable pageable) {
        logger.info("Fetching diseases for patient ID: {} with pageable: {}", patientId, pageable);
        return ResponseEntity.ok(diseaseService.getDiseasesByPatientId(patientId, pageable));
    }

    @Operation(summary = "Получить заболевание по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заболевание найдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Disease.class),
                            examples = @ExampleObject(name = "Пример ответа", value = """
                                    {
                                        "id": 1,
                                        "mkb10": {
                                            "code": "A00.0",
                                            "name": "Холера"
                                        },
                                        "startDate": "2025-01-15",
                                        "endDate": "2025-02-10",
                                        "prescriptions": "Постельный режим, жаропонижающие препараты",
                                        "sickLeaveIssued": true
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Заболевание или пациент не найдены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Заболевание не найдено", value = """
                                    {
                                        "message": "Заболевание с ID 1 не найдено или не принадлежит пациенту с ID 1"
                                    }
                                    """)))
    })
    @GetMapping("/{diseaseId}")
    public ResponseEntity<Disease> getDiseaseById(
            @Parameter(description = "ID пациента", example = "1") @PathVariable("patient_id") Long patientId,
            @Parameter(description = "ID заболевания", example = "1") @PathVariable Long diseaseId) {
        logger.info("Fetching disease ID: {} for patient ID: {}", diseaseId, patientId);
        return ResponseEntity.ok(diseaseService.getDiseaseById(patientId, diseaseId));
    }

    @Operation(summary = "Добавить заболевание пациенту")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заболевание успешно добавлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Disease.class),
                            examples = @ExampleObject(name = "Пример ответа", value = """
                                    
                                    {
                                      "mkb10": {
                                        "code": "A00.0",
                                        "name": "Холера"
                                      },
                                      "startDate": "2024-01-01",
                                      "endDate": "2024-02-01",
                                      "prescriptions": "Постельный режим, антибиотики",
                                      "sickLeaveIssued": true,
                                      "patient": {
                                      "id": 1,
                                      "lastName": "Иванов",
                                      "firstName": "Иван",
                                      "middleName": "Иванович",
                                      "gender": "М",
                                      "birthDate": "1990-01-01",
                                      "insuranceNumber": "1234567890123457",
                                      "diseases": [
                                        {
                                          "id": 4,
                                          "mkb10": {
                                            "code": "A00.0",
                                            "name": "Холера"
                                          },
                                          "startDate": "2024-01-01",
                                          "endDate": "2024-02-01",
                                          "prescriptions": "Противохалерная терапия",
                                          "sickLeaveIssued": true
                                        }
                                      ]
                                    }
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Некорректные данные", value = """
                                    {
                                        "message": "Код МКБ-10 не найден или некорректен"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Пациент не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Пациент не найден", value = """
                                    {
                                        "message": "Пациент с ID 1 не найден"
                                    }
                                    """)))
    })
    @PostMapping
    public ResponseEntity<Disease> addDisease(
            @Parameter(description = "ID пациента", example = "1") @PathVariable("patient_id") Long patientId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные заболевания",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Disease.class),
                            examples = @ExampleObject(name = "Пример запроса", value = """
                                    {
                                      "mkb10": {
                                        "code": "A00.0",
                                        "name": "Холера"
                                      },
                                      "startDate": "2024-01-01",
                                      "endDate": "2024-02-01",
                                      "prescriptions": "Постельный режим, антибиотики",
                                      "sickLeaveIssued": true,
                                      "patient": {
                                      "id": 1,
                                      "lastName": "Иванов",
                                      "firstName": "Иван",
                                      "middleName": "Иванович",
                                      "gender": "М",
                                      "birthDate": "1990-01-01",
                                      "insuranceNumber": "1234567890123457",
                                      "diseases": [
                                        {
                                          "id": 4,
                                          "mkb10": {
                                            "code": "A00.0",
                                            "name": "Холера"
                                          },
                                          "startDate": "2024-01-01",
                                          "endDate": "2024-02-01",
                                          "prescriptions": "Противохалерная терапия",
                                          "sickLeaveIssued": true
                                        }
                                      ]
                                    }
                                    }
                                    """)
                    )
            )
            @Valid @RequestBody Disease disease) {
        logger.info("Creating disease for patient ID: {}", patientId);
        Disease savedDisease = diseaseService.saveDisease(patientId, disease);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDisease);
    }

    @Operation(summary = "Обновить заболевание")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заболевание успешно обновлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Disease.class),
                            examples = @ExampleObject(name = "Пример ответа", value = """
                                    {
                                      "mkb10": {
                                        "code": "A00.0",
                                        "name": "Холера"
                                      },
                                      "startDate": "2024-01-01",
                                      "endDate": "2024-02-01",
                                      "prescriptions": "Постельный режим, антибиотики",
                                      "sickLeaveIssued": true,
                                      "patient": {
                                      "id": 1,
                                      "lastName": "Иванов",
                                      "firstName": "Иван",
                                      "middleName": "Иванович",
                                      "gender": "М",
                                      "birthDate": "1990-01-01",
                                      "insuranceNumber": "1234567890123457",
                                      "diseases": [
                                        {
                                          "id": 4,
                                          "mkb10": {
                                            "code": "A00.0",
                                            "name": "Холера"
                                          },
                                          "startDate": "2024-01-01",
                                          "endDate": "2024-02-01",
                                          "prescriptions": "Противохалерная терапия",
                                          "sickLeaveIssued": true
                                        }
                                      ]
                                    }
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Некорректные данные", value = """
                                    {
                                        "message": "Дата начала болезни должна быть до даты окончания"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Заболевание или пациент не найдены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Заболевание не найдено", value = """
                                    {
                                        "message": "Заболевание с ID 1 не найдено или не принадлежит пациенту с ID 1"
                                    }
                                    """)))
    })
    @PutMapping("/{diseaseId}")
    public ResponseEntity<Disease> updateDisease(
            @Parameter(description = "ID пациента", example = "1") @PathVariable("patient_id") Long patientId,
            @Parameter(description = "ID заболевания", example = "1") @PathVariable Long diseaseId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновлённые данные заболевания",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Disease.class),
                            examples = @ExampleObject(name = "Пример запроса", value = """
                                    {
                                        "mkb10": {
                                            "code": "B02",
                                            "name": "Грипп"
                                        },
                                        "startDate": "2024-03-01",
                                        "endDate": "2024-03-15",
                                        "prescriptions": "Жаропонижающее, постельный режим",
                                        "sickLeaveIssued": false
                                    }
                                    """)
                    )
            )
            @Valid @RequestBody Disease disease) {
        logger.info("Updating disease ID: {} for patient ID: {}", diseaseId, patientId);
        Disease updated = diseaseService.updateDisease(patientId, diseaseId, disease);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить заболевание")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заболевание успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Заболевание или пациент не найдены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Заболевание не найдено", value = """
                                    {
                                        "message": "Заболевание с ID 1 не найдено или не принадлежит пациенту с ID 1"
                                    }
                                    """)))
    })
    @DeleteMapping("/{diseaseId}")
    public ResponseEntity<Void> deleteDisease(
            @Parameter(description = "ID пациента", example = "1") @PathVariable("patient_id") Long patientId,
            @Parameter(description = "ID заболевания", example = "1") @PathVariable Long diseaseId) {
        logger.info("Deleting disease ID: {} for patient ID: {}", diseaseId, patientId);
        diseaseService.deleteDisease(patientId, diseaseId);
        return ResponseEntity.noContent().build();
    }
}