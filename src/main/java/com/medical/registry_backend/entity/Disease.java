package com.medical.registry_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "diseases")
public class Disease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор записи о заболевании", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Пациент обязателен")
    @JsonBackReference
    @Schema(description = "Пациент, связанный с заболеванием")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mkb10_code", nullable = false)
    @NotNull(message = "Код МКБ-10 обязателен")
    @Schema(description = "Код МКБ-10, соответствующий заболеванию")
    private Mkb10 mkb10;

    @NotNull(message = "Дата начала болезни обязательна")
    @PastOrPresent(message = "Дата начала болезни не должна быть в будущем")
    @Column(name = "start_date", nullable = false)
    @Schema(description = "Дата начала заболевания", example = "2025-01-15")
    private LocalDate startDate;

    @PastOrPresent(message = "Дата окончания болезни не должна быть в будущем")
    @Column(name = "end_date")
    @Schema(description = "Дата окончания заболевания", example = "2025-02-10")
    private LocalDate endDate;

    @NotBlank(message = "Назначения не должны быть пустыми")
    @Column(nullable = false)
    @Schema(description = "Назначенное лечение", example = "Постельный режим, жаропонижающие препараты")
    private String prescriptions;

    @NotNull(message = "Статус выдачи больничного листа обязателен")
    @Column(name = "sick_leave_issued", nullable = false)
    @Schema(description = "Выдан ли больничный лист", example = "true")
    private Boolean sickLeaveIssued;

    public Boolean isSickLeaveIssued() {
        return sickLeaveIssued;
    }
}
