package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.repository.Mkb10Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Mkb10ServiceTest {

    @Mock
    private Mkb10Repository mkb10Repository;

    @InjectMocks
    private Mkb10ServiceImpl mkb10Service;

    private Mkb10 mkb10;
    private Mkb10 mkb10_2;

    @BeforeEach
    void setUp() {
        mkb10 = new Mkb10();
        mkb10.setCode("A00.0");
        mkb10.setName("Холера");
        mkb10_2 = new Mkb10();
        mkb10_2.setCode("B02.0");
        mkb10_2.setName("Грипп");
    }

    @Test
    void getMkb10ByCode_shouldReturnMkb10FromRepository() {
        when(mkb10Repository.findById("A00.0")).thenReturn(Optional.of(mkb10));

        Mkb10 result = mkb10Service.getMkb10ByCode("A00.0");

        assertNotNull(result);
        assertEquals("A00.0", result.getCode());
        assertEquals("Холера", result.getName());
        verify(mkb10Repository).findById("A00.0");
    }

    @Test
    void getMkb10ByCode_notFound_shouldReturnNull() {
        when(mkb10Repository.findById("Z99.9")).thenReturn(Optional.empty());

        Mkb10 result = mkb10Service.getMkb10ByCode("Z99.9");

        assertNull(result);
        verify(mkb10Repository).findById("Z99.9");
    }

    @Test
    void getAllMkb10_shouldReturnAllRecords() {
        List<Mkb10> mkb10List = Arrays.asList(mkb10, mkb10_2);
        when(mkb10Repository.findAll()).thenReturn(mkb10List);

        List<Mkb10> result = mkb10Service.getAllMkb10();

        assertEquals(2, result.size());
        assertEquals("A00.0", result.get(0).getCode());
        assertEquals("Холера", result.get(0).getName());
        assertEquals("B02.0", result.get(1).getCode());
        assertEquals("Грипп", result.get(1).getName());
        verify(mkb10Repository).findAll();
    }

    @Test
    void getAllMkb10_emptyList_shouldReturnEmptyList() {
        when(mkb10Repository.findAll()).thenReturn(Collections.emptyList());

        List<Mkb10> result = mkb10Service.getAllMkb10();

        assertTrue(result.isEmpty());
        verify(mkb10Repository).findAll();
    }

    @Test
    void getAllMkb10_pageable_shouldReturnPagedRecords() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("code"));
        List<Mkb10> mkb10List = Arrays.asList(mkb10, mkb10_2);
        Page<Mkb10> page = new PageImpl<>(mkb10List, pageable, mkb10List.size());
        when(mkb10Repository.findAll(pageable)).thenReturn(page);

        Page<Mkb10> result = mkb10Service.getAllMkb10(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("A00.0", result.getContent().get(0).getCode());
        assertEquals("Холера", result.getContent().get(0).getName());
        assertEquals("B02.0", result.getContent().get(1).getCode());
        assertEquals("Грипп", result.getContent().get(1).getName());
        verify(mkb10Repository).findAll(pageable);
    }

    @Test
    void searchMkb10ByCodeOrName_emptySearch_shouldReturnAllRecords() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("code"));
        List<Mkb10> mkb10List = Arrays.asList(mkb10, mkb10_2);
        Page<Mkb10> page = new PageImpl<>(mkb10List, pageable, mkb10List.size());
        when(mkb10Repository.findAll(pageable)).thenReturn(page);

        Page<Mkb10> result = mkb10Service.searchMkb10ByCodeOrName("", pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("A00.0", result.getContent().get(0).getCode());
        assertEquals("Холера", result.getContent().get(0).getName());
        verify(mkb10Repository).findAll(pageable);
    }

    @Test
    void searchMkb10ByCodeOrName_withSearchQuery_shouldReturnFilteredRecords() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("code"));
        List<Mkb10> mkb10List = Collections.singletonList(mkb10);
        Page<Mkb10> page = new PageImpl<>(mkb10List, pageable, mkb10List.size());
        when(mkb10Repository.findByCodeOrNameContainingIgnoreCase("Холера", pageable)).thenReturn(page);

        Page<Mkb10> result = mkb10Service.searchMkb10ByCodeOrName("Холера", pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("A00.0", result.getContent().get(0).getCode());
        assertEquals("Холера", result.getContent().get(0).getName());
        verify(mkb10Repository).findByCodeOrNameContainingIgnoreCase("Холера", pageable);
    }

    @Test
    void searchMkb10ByCodeOrName_noResults_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("code"));
        Page<Mkb10> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(mkb10Repository.findByCodeOrNameContainingIgnoreCase("Неизвестно", pageable)).thenReturn(emptyPage);

        Page<Mkb10> result = mkb10Service.searchMkb10ByCodeOrName("Неизвестно", pageable);

        assertTrue(result.getContent().isEmpty());
        verify(mkb10Repository).findByCodeOrNameContainingIgnoreCase("Неизвестно", pageable);
    }

    @Test
    void updateMkb10Data_validCsv_shouldUpdateRepository() throws Exception {
        String csvContent = """
                "id","parent_code","code","name"
                ,,"A00.0","Холера"
                ,,"B02.0","Грипп"
                """;
        BufferedReader reader = new BufferedReader(new StringReader(csvContent));
        mkb10Service = spy(mkb10Service);
        doReturn(reader).when(mkb10Service).getCsvReader();

        mkb10Service.updateMkb10Data();

        verify(mkb10Repository).deleteAll();
        verify(mkb10Repository).saveAll(argThat((List<Mkb10> list) -> {
            if (list.size() != 2) return false;
            return list.stream().anyMatch(m -> m.getCode().equals("A00.0") && m.getName().equals("Холера")) &&
                    list.stream().anyMatch(m -> m.getCode().equals("B02.0") && m.getName().equals("Грипп"));
        }));
    }

    @Test
    void updateMkb10Data_emptyCsv_shouldNotUpdateRepository() throws Exception {
        String csvContent = "\"id\",\"parent_code\",\"code\",\"name\"\n";
        BufferedReader reader = new BufferedReader(new StringReader(csvContent));
        mkb10Service = spy(mkb10Service);
        doReturn(reader).when(mkb10Service).getCsvReader();

        mkb10Service.updateMkb10Data();

        verify(mkb10Repository, never()).deleteAll();
        verify(mkb10Repository, never()).saveAll(any());
    }

    @Test
    void updateMkb10Data_invalidCsvLine_shouldSkipInvalidLines() throws Exception {
        String csvContent = """
                "id","parent_code","code","name"
                ,,"A00.0","Холера"
                ,,"Invalid",""
                ,,"B02.0","Грипп"
                """;
        BufferedReader reader = new BufferedReader(new StringReader(csvContent));
        mkb10Service = spy(mkb10Service);
        doReturn(reader).when(mkb10Service).getCsvReader();

        mkb10Service.updateMkb10Data();

        verify(mkb10Repository).deleteAll();
        verify(mkb10Repository).saveAll(argThat((List<Mkb10> list) -> {
            if (list.size() != 2) return false;
            return list.stream().anyMatch(m -> m.getCode().equals("A00.0") && m.getName().equals("Холера")) &&
                    list.stream().anyMatch(m -> m.getCode().equals("B02.0") && m.getName().equals("Грипп"));
        }));
    }

    @Test
    void updateMkb10Data_exceptionInParsing_shouldNotUpdateRepository() throws Exception {
        mkb10Service = spy(mkb10Service);
        doThrow(new RuntimeException("IO Error")).when(mkb10Service).getCsvReader();

        mkb10Service.updateMkb10Data();

        verify(mkb10Repository, never()).deleteAll();
        verify(mkb10Repository, never()).saveAll(any());
    }
}