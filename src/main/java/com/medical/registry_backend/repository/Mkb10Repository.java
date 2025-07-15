package com.medical.registry_backend.repository;

import com.medical.registry_backend.entity.Mkb10;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Mkb10Repository extends JpaRepository<Mkb10, String> {
//    public List<Mkb10> findAll(pageable) {}
    // Можно добавить кастомные методы, если нужно
}