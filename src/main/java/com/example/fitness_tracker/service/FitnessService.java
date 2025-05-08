package com.example.fitness_tracker.service;

import com.example.fitness_tracker.model.UserData;
import com.example.fitness_tracker.model.HistoryRecord;

import java.util.List;

public interface FitnessService {
    String calculateProgress(UserData userData);
    String checkOvertraining(UserData userData);
    String convertMilesToKm(double miles);

    void saveProgressCalculation(UserData userData, String result);
    void saveWorkoutData(UserData userData, String recommendation);
    void saveConversion(double miles, String result);

    List<HistoryRecord> loadHistory();
    void updateHistoryRecord(int id, HistoryRecord record);
    void deleteHistoryRecord(int id);

    List<HistoryRecord> searchHistoryRecords(String searchTerm);
    List<HistoryRecord> getHistoryRecordsSorted(String sortBy, boolean ascending);
    List<HistoryRecord> filterHistoryRecordsByType(String operationType);
}
