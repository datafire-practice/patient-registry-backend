package com.medical.registry_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "mkb10")
public class Mkb10 {
    @Id
    @Column(name = "code", length = 10)
    private String code;

    @Column(nullable = false, length = 1000)
    private String name;
}