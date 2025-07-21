package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.repository.Mkb10Repository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class Mkb10ServiceImpl implements Mkb10Service {

    private static final Logger logger = LoggerFactory.getLogger(Mkb10ServiceImpl.class);
    private static final String MKB10_CSV_URL = "https://raw.githubusercontent.com/ak4nv/mkb10/master/mkb10.csv";
    private static final String FALLBACK_CSV_PATH = "classpath:mkb10.csv";
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

    @PostConstruct
    public void init() {
        logger.info("Initializing MKB10 data on application startup");
        updateMkb10Data();
    }



    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateMkb10Data() {
        logger.info("Starting MKB10 data update from {}", MKB10_CSV_URL);
        try {
            List<Mkb10> mkb10List = parseMkb10Csv();
            if (!mkb10List.isEmpty()) {
                mkb10Repository.deleteAll();
                mkb10Repository.saveAll(mkb10List);
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
        try (BufferedReader reader = getCsvReader()) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
//                logger.debug("Processing CSV line: {}", line);
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    String code = parts[2].replace("\"", "").trim();
                    String name = parts[3].replace("\"", "").trim();
                    if (CODE_PATTERN.matcher(code).matches() && !name.isEmpty()) {
//                        logger.debug("Parsed valid MKB10 entry: code={}, name={}", code, name);
                        Mkb10 mkb10 = new Mkb10();
                        mkb10.setCode(code);
                        mkb10.setName(name);
                        mkb10List.add(mkb10);
                    } else {
//                        logger.debug("Skipped CSV entry: code={}, name={}", code, name);
                    }
                } else {
                    logger.warn("Invalid CSV line format: {}", line);
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing CSV", e);
            throw e;
        }
        return mkb10List;
    }

    BufferedReader getCsvReader() throws Exception {
        try {
            logger.info("Attempting to load CSV from URL: {}", MKB10_CSV_URL);
            return new BufferedReader(new InputStreamReader(new URL(MKB10_CSV_URL).openStream(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.warn("Failed to load CSV from URL, attempting fallback file: {}", FALLBACK_CSV_PATH);
            return Files.newBufferedReader(Paths.get(getClass().getClassLoader().getResource("mkb10.csv").toURI()), StandardCharsets.UTF_8);
        }
    }

    @Override
    public Mkb10 getMkb10ByCode(String code) {
        return mkb10Cache.get(code, key -> mkb10Repository.findById(key).orElse(null));
    }

    @Override
    public List<Mkb10> getAllMkb10() {
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
    @Override
    public Page<Mkb10> searchMkb10ByCodeOrName(String search, Pageable pageable) {
        logger.info("Searching MKB10 data with query: {}, pageable: {}", search, pageable);
        if (search == null || search.trim().isEmpty()) {
            logger.info("Search query is empty, returning all MKB10 data");
            return getAllMkb10(pageable);
        }
        Page<Mkb10> result = mkb10Repository.findByCodeOrNameContainingIgnoreCase(search.trim(), pageable);
        result.forEach(mkb10 -> mkb10Cache.put(mkb10.getCode(), mkb10));
        if (result.isEmpty()) {
            logger.warn("No MKB10 data found for search query: {}", search);
        }
        return result;
    }
}