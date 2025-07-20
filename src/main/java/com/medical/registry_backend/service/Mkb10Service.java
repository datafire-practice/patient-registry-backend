package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Mkb10;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface Mkb10Service {

    /**
     * Обновляет справочник MKB10, загружая данные из CSV-файла.
     * Очищает существующие данные в БД, сохраняет новые и сбрасывает кэш.
     */
    void updateMkb10Data();

    /**
     * Получает запись MKB10 по коду, используя кэш или БД.
     * @param code Код MKB10 (например, A00.0)
     * @return Объект Mkb10 или null, если запись не найдена
     */
    Mkb10 getMkb10ByCode(String code);

    /**
     * Получает все записи MKB10 из БД, обновляя кэш.
     * @return Список всех записей Mkb10
     */
    List<Mkb10> getAllMkb10();

    /**
     * Получает записи MKB10 с поддержкой пагинации.
     * @param pageable Параметры пагинации (страница, размер, сортировка)
     * @return Страница с записями Mkb10
     */
    Page<Mkb10> getAllMkb10(Pageable pageable);
    Page<Mkb10> searchMkb10ByCodeOrName(String search, Pageable pageable);
}