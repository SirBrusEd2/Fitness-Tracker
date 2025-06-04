package com.example.fitness_tracker.dao;

import com.example.fitness_tracker.model.UserData;
import com.example.fitness_tracker.model.HistoryRecord;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс Data Access Object (DAO) для работы с данными пользователя и историей операций.
 * Определяет контракт для всех реализаций DAO.
 */
public interface UserDataDao {
    /**
     * Сохраняет данные пользователя.
     * @param userData Данные пользователя для сохранения
     */
    void saveUserData(UserData userData);
    /**
     * Сохраняет запись истории.
     * @param operationType Тип операции
     * @param details Детали операции
     */
    void saveHistoryRecord(String operationType, String details);
    List<HistoryRecord> getHistoryRecords();
    Optional<UserData> getUserDataById(int id);
    void updateUserData(int id, UserData userData);
    void deleteUserData(int id);
    Optional<HistoryRecord> getHistoryRecordById(int id);
    void updateHistoryRecord(int id, HistoryRecord record);
    void deleteHistoryRecord(int id);

    // Новый метод для обновления статуса записи истории
    void updateHistoryRecordStatus(int id, boolean newStatus);

    /**
     * Получает все записи истории.
     * @return Список записей истории
     */
    List<HistoryRecord> searchHistoryRecords(String searchTerm);
    List<HistoryRecord> getHistoryRecordsSorted(String sortBy, boolean ascending);
    List<HistoryRecord> filterHistoryRecordsByType(String operationType);
}
