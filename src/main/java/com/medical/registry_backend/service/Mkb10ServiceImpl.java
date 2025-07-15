package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.repository.Mkb10Repository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class Mkb10ServiceImpl implements Mkb10Service {

    private static final Logger logger = LoggerFactory.getLogger(Mkb10ServiceImpl.class);
    private static final String MKB10_CSV_URL = "https://raw.githubusercontent.com/ak4nv/mkb10/master/mkb10.csv";
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z]\\d{2}\\.\\d$");

    private final Mkb10Repository mkb10Repository;
    private final Cache<String, Mkb10> mkb10Cache;

    @Autowired
    public Mkb10ServiceImpl(Mkb10Repository mkb10Repository) {
        this.mkb10Repository = mkb10Repository;
        this.mkb10Cache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(20_000)
                .build();
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateMkb10Data() {
        System.out.println("update mkb10!");
        logger.info("Starting MKB10 data update from {}", MKB10_CSV_URL);
        try {
            List<Mkb10> mkb10List = parseMkb10Csv();
            if (!mkb10List.isEmpty()) {
                // Очистка старых данных
                mkb10Repository.deleteAll();
                // Сохранение новых данных
                mkb10Repository.saveAll(mkb10List);
                // Очистка кэша
                mkb10Cache.invalidateAll();
                logger.info("Successfully updated MKB10 data with {} records", mkb10List.size());
            } else {
                logger.warn("No valid MKB10 data parsed from CSV");
            }
        } catch (Exception e) {
            logger.error("Error updating MKB10 data", e);
        }
    }

    private List<Mkb10> parseMkb10Csv() throws Exception {
        List<Mkb10> mkb10List = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new URL(MKB10_CSV_URL).openStream()))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Пропускаем заголовок
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    String code = parts[2].replace("\"", "").trim();
                    String name = parts[3].replace("\"", "").trim();
                    // Фильтрация кодов по шаблону AXX.X
                    if (CODE_PATTERN.matcher(code).matches() && !name.isEmpty()) {
                        Mkb10 mkb10 = new Mkb10();
                        mkb10.setCode(code);
                        mkb10.setName(name);
                        mkb10List.add(mkb10);
                    }
                }
            }
        }
        return mkb10List;
    }

    @Override
    public Mkb10 getMkb10ByCode(String code) {
        return mkb10Cache.get(code, key -> mkb10Repository.findById(key).orElse(null));
    }

    @Override
    public List<Mkb10> getAllMkb10() {
        System.out.println("get all mkb10!");
        updateMkb10Data();
        return mkb10Repository.findAll().stream()
                .peek(mkb10 -> mkb10Cache.put(mkb10.getCode(), mkb10))
                .toList();
    }

    @Override
    public Page<Mkb10> getAllMkb10(Pageable pageable) {
        return mkb10Repository.findAll(pageable).map(mkb10 -> {
            mkb10Cache.put(mkb10.getCode(), mkb10);
            return mkb10;
        });
    }
}