package com.medical.registry_backend.repository;

import com.medical.registry_backend.entity.Mkb10;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Mkb10Repository extends JpaRepository<Mkb10, String> {
    Optional<Mkb10> findById(String code);
}