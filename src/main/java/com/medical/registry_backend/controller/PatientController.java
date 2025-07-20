package com.medical.registry_backend.controller;

import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.exception.GlobalExceptionHandler;
import com.medical.registry_backend.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @Operation(summary = "Получить всех пациентов с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пациентов",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class),
                            examples = @ExampleObject(name = "Пример ответа", value = """
                                    {
                                        "content": [
                                            {
                                                "id": 1,
                                                "lastName": "Иванов",
                                                "firstName": "Иван",
                                                "middleName": "Иванович",
                                                "gender": "М",
                                                "birthDate": "1990-01-01",
                                                "insuranceNumber": "1234567890123456",
                                                "diseases": []
                                            },
                                            {
                                                "id": 2,
                                                "lastName": "Петрова",
                                                "firstName": "Анна",
                                                "middleName": "Сергеевна",
                                                "gender": "Ж",
                                                "birthDate": "1985-05-12",
                                                "insuranceNumber": "9876543210987654",
                                                "diseases": []
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
            @ApiResponse(responseCode = "400", description = "Некорректные параметры пагинации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Некорректные параметры", value = """
                                    {
                                        "message": "Некорректные параметры пагинации"
                                    }
                                    """)))
    })
    @GetMapping
    public ResponseEntity<Page<Patient>> getAllPatients(
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
        logger.info("Fetching all patients with pageable: {}", pageable);
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    @Operation(summary = "Получить пациента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пациент найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Patient.class),
                            examples = @ExampleObject(name = "Пример ответа", value = """
                                    {
                                        "id": 1,
                                        "lastName": "Иванов",
                                        "firstName": "Иван",
                                        "middleName": "Иванович",
                                        "gender": "М",
                                        "birthDate": "1990-01-01",
                                        "insuranceNumber": "1234567890123456",
                                        "diseases": []
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
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(
            @Parameter(description = "ID пациента", example = "1") @PathVariable Long id) {
        logger.info("Fetching patient with ID: {}", id);
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @Operation(summary = "Создать нового пациента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пациент создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Patient.class),
                            examples = @ExampleObject(name = "Пример ответа", value = """
                                    {
                                        "id": 1,
                                        "lastName": "Иванов",
                                        "firstName": "Иван",
                                        "middleName": "Иванович",
                                        "gender": "М",
                                        "birthDate": "1990-01-01",
                                        "insuranceNumber": "1234567890123456",
                                        "diseases": [
                                            {
                                                "mkb10": {
                                                    "code": "A00.0",
                                                    "name": "Холера"
                                                },
                                                "startDate": "2025-01-15",
                                                "endDate": "2025-02-10",
                                                "prescriptions": "Противохалерная терапия",
                                                "sickLeaveIssued": true
                                            }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Невалидные данные",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Некорректные данные", value = """
                                    {
                                        "message": "Фамилия должна содержать только кириллицу и дефис"
                                    }
                                    """)))
    })
    @PostMapping
    public ResponseEntity<Patient> createPatient(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Пациент для создания",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Пример запроса", value = """
                                    {
                                        "lastName": "Иванов",
                                        "firstName": "Иван",
                                        "middleName": "Иванович",
                                        "gender": "М",
                                        "birthDate": "1990-01-01",
                                        "insuranceNumber": "1234567890123456",
                                        "diseases": [
                                            {
                                                "mkb10": {
                                                    "code": "A00.0",
                                                    "name": "Холера"
                                                },
                                                "startDate": "2025-01-15",
                                                "endDate": "2025-02-10",
                                                "prescriptions": "Противохалерная терапия",
                                                "sickLeaveIssued": true
                                            }
                                        ]
                                    }
                                    """)))
            @Valid @RequestBody Patient patient) {
        logger.info("Creating patient: {}", patient);
        Patient savedPatient = patientService.savePatient(patient);
        logger.info("Created patient with ID: {}", savedPatient.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }

    @Operation(summary = "Обновить данные пациента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пациент обновлён",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Patient.class),
                            examples = @ExampleObject(name = "Пример ответа", value = """
                                    {
                                        "id": 1,
                                        "lastName": "Иванов",
                                        "firstName": "Иван",
                                        "middleName": "Сергеевич",
                                        "gender": "М",
                                        "birthDate": "1990-01-01",
                                        "insuranceNumber": "1234567890123456",
                                        "diseases": [
                                            {
                                                "mkb10": {
                                                    "code": "B02",
                                                    "name": "Грипп"
                                                },
                                                "startDate": "2025-03-01",
                                                "endDate": "2025-03-15",
                                                "prescriptions": "Жаропонижающее, постельный режим",
                                                "sickLeaveIssued": false
                                            }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Некорректные данные", value = """
                                    {
                                        "message": "Фамилия не может быть пустой"
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
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(
            @Parameter(description = "ID пациента", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновлённые данные пациента",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Пример запроса", value = """
                                    {
                                        "lastName": "Иванов",
                                        "firstName": "Иван",
                                        "middleName": "Сергеевич",
                                        "gender": "М",
                                        "birthDate": "1990-01-01",
                                        "insuranceNumber": "1234567890123456",
                                        "diseases": [
                                            {
                                                "mkb10": {
                                                    "code": "B02",
                                                    "name": "Грипп"
                                                },
                                                "startDate": "2025-03-01",
                                                "endDate": "2025-03-15",
                                                "prescriptions": "Жаропонижающее, постельный режим",
                                                "sickLeaveIssued": false
                                            }
                                        ]
                                    }
                                    """)))
            @Valid @RequestBody Patient patient) {
        logger.info("Updating patient with ID: {}", id);
        try {
            Patient updatedPatient = patientService.updatePatient(id, patient);
            logger.info("Updated patient with ID: {}", updatedPatient.getId());
            return ResponseEntity.ok(updatedPatient);
        } catch (ValidationException e) {
            logger.error("Validation error while updating patient with ID: {}", id, e);
            return ResponseEntity.badRequest().body(new GlobalExceptionHandler.ErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "Удалить пациента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пациент удалён"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Пациент не найден", value = """
                                    {
                                        "message": "Пациент с ID 1 не найден"
                                    }
                                    """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "ID пациента", example = "1") @PathVariable Long id) {
        logger.info("Deleting patient with ID: {}", id);
        patientService.deletePatient(id);
        logger.info("Deleted patient with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}