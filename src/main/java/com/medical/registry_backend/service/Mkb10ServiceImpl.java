package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.repository.Mkb10Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;

@Service
@RequiredArgsConstructor
public class Mkb10ServiceImpl implements Mkb10Service {
    private final Mkb10Repository mkb10Repository;
    private final RestTemplate restTemplate;

    @Cacheable("mkb10Cache")
    @Override
    public Page<Mkb10> getAllMkb10(Pageable pageable) {
        return mkb10Repository.findAll(pageable);
    }

    @CacheEvict(value = "mkb10Cache", allEntries = true)
    @Scheduled(cron = "0 0 0 * * ?")
    @Override
    public void updateMkb10FromCsv() {
        String csvContent = restTemplate.getForObject(
                "https://raw.githubusercontent.com/ak4nv/mkb10/master/mkb10.csv", String.class);
        List<Mkb10> mkb10List = parseCsv(csvContent);
        mkb10Repository.deleteAll();
        mkb10Repository.saveAll(mkb10List);
    }

    private List<Mkb10> parseCsv(String csvContent) {
        List<Mkb10> mkb10List = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(";", -1);
                if (parts.length >= 2 && parts[0].matches("^[A-Z]\\d{2}(\\.\\d)?$")) {
                    Mkb10 mkb10 = new Mkb10();
                    mkb10.setCode(parts[0].trim());
                    mkb10.setName(parts[1].trim());
                    mkb10List.add(mkb10);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга CSV: " + e.getMessage());
        }
        return mkb10List;
    }
}