package com.medical.registry_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "mkb10")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Mkb10 {

    @Id
    @Column(name = "code", length = 10)
    @Schema(description = "Код диагноза по МКБ-10", example = "A00.0")
    private String code;

    @Column(nullable = false, length = 1000)
    @Schema(description = "Наименование диагноза", example = "Холера")
    private String name;
}
