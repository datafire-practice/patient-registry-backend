package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Mkb10;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface Mkb10Service {
    Page<Mkb10> getAllMkb10(Pageable pageable);
    void updateMkb10FromCsv();
}