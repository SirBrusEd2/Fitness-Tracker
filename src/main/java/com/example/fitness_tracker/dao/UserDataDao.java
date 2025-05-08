package com.example.fitness_tracker.dao;

import com.example.fitness_tracker.model.UserData;
import com.example.fitness_tracker.model.HistoryRecord;
import java.util.List;
import java.util.Optional;

public interface UserDataDao {
    void saveUserData(UserData userData);
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

    List<HistoryRecord> searchHistoryRecords(String searchTerm);
    List<HistoryRecord> getHistoryRecordsSorted(String sortBy, boolean ascending);
    List<HistoryRecord> filterHistoryRecordsByType(String operationType);
}
