package com.medical.registry_backend.controller;

import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.service.Mkb10Service;
import com.medical.registry_backend.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dictionary/mkb10")
@RequiredArgsConstructor
public class Mkb10Controller {
    private final Mkb10Service mkb10Service;


    @GetMapping
    public ResponseEntity<Page<Mkb10>> getAllMkb10(Pageable pageable) {
        return ResponseEntity.ok(mkb10Service.getAllMkb10(pageable));
    }
}