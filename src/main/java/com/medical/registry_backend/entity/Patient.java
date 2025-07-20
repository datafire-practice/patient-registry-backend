package com.medical.registry_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "patients")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Информация о пациенте")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Идентификатор пациента", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Pattern(regexp = "^[А-Яа-яЁё-]+$", message = "Фамилия должна содержать только кириллицу и дефис")
    @Schema(description = "Фамилия пациента", example = "Иванов")
    private String lastName;

    @Column(nullable = false)
    @Pattern(regexp = "^[А-Яа-яЁё-]+$", message = "Имя должно содержать только кириллицу и дефис")
    @Schema(description = "Имя пациента", example = "Алексей")
    private String firstName;

    @Column
    @Pattern(regexp = "^[А-Яа-яЁё-]*$", message = "Отчество должно содержать только кириллицу и дефис")
    @Schema(description = "Отчество пациента", example = "Сергеевич")
    private String middleName;

    @Column(nullable = false, length = 1)
    @Pattern(regexp = "^[МЖ]$", message = "Пол должен быть 'М' или 'Ж'")
    @Schema(description = "Пол пациента (М или Ж)", example = "М")
    private String gender;

    @Column(nullable = false)
    @Schema(description = "Дата рождения пациента", example = "1985-05-20")
    private LocalDate birthDate;

    @Column(nullable = false, length = 16, unique = true)
    @Pattern(regexp = "^\\d{16}$", message = "Номер полиса должен содержать 16 цифр")
    @Schema(description = "Номер страхового полиса (16 цифр)", example = "1310754579565031")
    private String insuranceNumber;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Schema(description = "Список заболеваний пациента")
    private List<Disease> diseases = new ArrayList<>();
}
