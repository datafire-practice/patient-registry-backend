package com.medical.registry_backend.repository;

import com.medical.registry_backend.entity.Mkb10;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface Mkb10Repository extends JpaRepository<Mkb10, String> {
    Optional<Mkb10> findById(String code);
    @Query("SELECT m FROM Mkb10 m WHERE LOWER(m.code) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Mkb10> findByCodeOrNameContainingIgnoreCase(@Param("search") String search, Pageable pageable);
}