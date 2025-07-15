package com.medical.registry_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Pattern(regexp = "^[А-Яа-яЁё-]+$", message = "Фамилия должна содержать только кириллицу и дефис")
    private String lastName;

    @Column(nullable = false)
    @Pattern(regexp = "^[А-Яа-яЁё-]+$", message = "Имя должно содержать только кириллицу и дефис")
    private String firstName;

    @Column
    @Pattern(regexp = "^[А-Яа-яЁё-]*$", message = "Отчество должно содержать только кириллицу и дефис")
    private String middleName;

    @Column(nullable = false, length = 1)
    @Pattern(regexp = "^[МЖ]$", message = "Пол должен быть 'М' или 'Ж'")
    private String gender;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, length = 16, unique = true)
    @Pattern(regexp = "^\\d{16}$", message = "Номер полиса должен содержать 16 цифр")
    private String insuranceNumber;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disease> diseases;
}