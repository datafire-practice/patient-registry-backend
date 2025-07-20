package com.medical.registry_backend.controller;

import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.exception.GlobalExceptionHandler;
import com.medical.registry_backend.service.Mkb10Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Получить все записи MKB10 с поддержкой пагинации")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Страница с записями MKB10",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class),
                            examples = @ExampleObject(name = "Пример ответа", value = """
                                    {
                                        "content": [
                                            {
                                                "code": "A00.0",
                                                "name": "Холера"
                                            },
                                            {
                                                "code": "B02",
                                                "name": "Грипп"
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
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Некорректные параметры", value = """
                                    {
                                        "message": "Некорректные параметры пагинации"
                                    }
                                    """)))
    })
    @GetMapping
    public ResponseEntity<Page<Mkb10>> getAllMkb10(
            @Parameter(description = "Параметры пагинации (передаются как query-параметры: ?page=0&size=10&sort=code,asc)",
                    examples = {
                            @ExampleObject(name = "Пример параметров пагинации", value = """
                                    {
                                        "page": 0,
                                        "size": 10,
                                        "sort": "code,asc"
                                    }
                                    """)
                    })
            Pageable pageable,
            @Parameter(description = "Поиск по коду или названию (опционально, передается как query-параметр: ?search=Холера)", example = "Холера") @RequestParam(required = false) String search) {
        logger.info("Fetching MKB10 data with pageable: {}, search: {}", pageable, search);
        Page<Mkb10> page = mkb10Service.searchMkb10ByCodeOrName(search, pageable);
        if (page.isEmpty()) {
            logger.warn("No MKB10 data found for pageable: {}, search: {}", pageable, search);
        }
        System.out.println("Get_Controller_MKB10&&&&");
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Получить запись MKB10 по коду")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запись MKB10 найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Mkb10.class),
                            examples = @ExampleObject(name = "Пример ответа", value = """
                                    {
                                        "code": "A00.0",
                                        "name": "Холера"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Запись MKB10 не найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Код не найден", value = """
                                    {
                                        "message": "Код МКБ-10 A00.0 не найден"
                                    }
                                    """)))
    })
    @GetMapping("/{code}")
    public ResponseEntity<Mkb10> getMkb10ByCode(
            @Parameter(description = "Код MKB10 (например, A00.0)", example = "A00.0") @PathVariable String code) {
        logger.info("Fetching MKB10 data for code: {}", code);
        Mkb10 mkb10 = mkb10Service.getMkb10ByCode(code);
        if (mkb10 == null) {
            logger.warn("MKB10 data not found for code: {}", code);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mkb10);
    }

    @Operation(summary = "Запустить ручное обновление справочника MKB10")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обновление справочника инициировано",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Пример ответа", value = """
                                    "Обновление справочника МКБ-10 инициировано"
                                    """))),
            @ApiResponse(responseCode = "500", description = "Ошибка при обновлении справочника",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(name = "Ошибка: Обновление не удалось", value = """
                                    {
                                        "message": "Ошибка при обновлении справочника МКБ-10"
                                    }
                                    """)))
    })
    @PostMapping("/update")
    public ResponseEntity<String> updateMkb10Data() {
        logger.info("Initiating manual MKB10 data update");
        mkb10Service.updateMkb10Data();
        return ResponseEntity.ok("Обновление справочника МКБ-10 инициировано");
    }
}