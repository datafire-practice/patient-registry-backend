package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Mkb10;
import java.util.List;

public interface Mkb10Service {
    List<Mkb10> getAllMkb10();
    void updateMkb10FromCsv();
}