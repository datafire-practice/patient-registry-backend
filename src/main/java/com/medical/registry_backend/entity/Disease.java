package com.medical.registry_backend.entity;

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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Пациент обязателен")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mkb10_code", nullable = false)
    @NotNull(message = "Код МКБ-10 обязателен")
    private Mkb10 mkb10;

    @NotNull(message = "Дата начала болезни обязательна")
    @PastOrPresent(message = "Дата начала болезни не должна быть в будущем")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @PastOrPresent(message = "Дата окончания болезни не должна быть в будущем")
    @Column(name = "end_date")
    private LocalDate endDate;

    @NotBlank(message = "Назначения не должны быть пустыми")
    @Column(nullable = false)
    private String prescriptions;

    @NotNull(message = "Статус выдачи больничного листа обязателен")
    @Column(name = "sick_leave_issued", nullable = false)
    private Boolean sickLeaveIssued;
}