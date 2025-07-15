package com.medical.registry_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "diseases")
public class Disease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "mkb10_code", nullable = false)
    @NotNull
    private Mkb10 mkb10;

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    @Size(max = 1024)
    @Column(nullable = false, length = 1024)
    private String prescriptions;

    @NotNull
    @Column(nullable = false)
    private Boolean sickLeaveIssued;

    // Явный геттер
    public Boolean isSickLeaveIssued() {
        return sickLeaveIssued;
    }

    // Явный сеттер
    public void setSickLeaveIssued(Boolean sickLeaveIssued) {
        this.sickLeaveIssued = sickLeaveIssued;
    }
}