package com.example.fitness_tracker.dao;

import com.example.fitness_tracker.model.UserData;
import com.example.fitness_tracker.model.HistoryRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryUserDataDao implements UserDataDao {
    // Singleton instance
    private static final InMemoryUserDataDao INSTANCE = new InMemoryUserDataDao();

    private final ObservableList<UserData> userDataList = FXCollections.observableArrayList();
    private final ObservableList<HistoryRecord> historyRecords = FXCollections.observableArrayList();

    private InMemoryUserDataDao() {
        // Конструктор пустой
    }

    public static InMemoryUserDataDao getInstance() {
        return INSTANCE;
    }

    @Override
    public void saveUserData(UserData userData) {
        // Всегда храним только одну запись пользователя
        if (userDataList.isEmpty()) {
            userDataList.add(userData);
        } else {
            userDataList.set(0, userData);
        }
    }

    @Override
    public void saveHistoryRecord(String operationType, String details) {
        HistoryRecord record = new HistoryRecord(
                LocalDate.now(),
                operationType,
                details,
                true
        );
        historyRecords.add(record);
    }

    @Override
    public List<HistoryRecord> getHistoryRecords() {
        return List.copyOf(historyRecords);
    }

    @Override
    public Optional<UserData> getUserDataById(int index) {
        if (index == 0 && !userDataList.isEmpty()) {
            return Optional.of(userDataList.get(0));
        }
        return Optional.empty();
    }

    @Override
    public void updateUserData(int index, UserData userData) {
        if (index == 0 && !userDataList.isEmpty()) {
            userDataList.set(0, userData);
        }
    }

    @Override
    public void deleteUserData(int index) {
        if (index == 0 && !userDataList.isEmpty()) {
            userDataList.clear();
        }
    }

    @Override
    public Optional<HistoryRecord> getHistoryRecordById(int index) {
        if (index >= 0 && index < historyRecords.size()) {
            return Optional.of(historyRecords.get(index));
        }
        return Optional.empty();
    }

    @Override
    public void updateHistoryRecord(int index, HistoryRecord record) {
        if (index >= 0 && index < historyRecords.size()) {
            historyRecords.set(index, record);
        }
    }

    @Override
    public void deleteHistoryRecord(int index) {
        if (index >= 0 && index < historyRecords.size()) {
            historyRecords.remove(index);
        }
    }

    @Override
    public void updateHistoryRecordStatus(int index, boolean newStatus) {
        if (index >= 0 && index < historyRecords.size()) {
            historyRecords.get(index).setActive(newStatus);
        }
    }

    @Override
    public List<HistoryRecord> searchHistoryRecords(String searchTerm) {
        return historyRecords.stream()
                .filter(record -> record.getOperationType().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        record.getDetails().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<HistoryRecord> getHistoryRecordsSorted(String sortBy, boolean ascending) {
        return historyRecords.stream()
                .sorted((r1, r2) -> {
                    int comparison = 0;
                    switch (sortBy) {
                        case "operation_date":
                            comparison = r1.getDate().compareTo(r2.getDate());
                            break;
                        case "operation_type":
                            comparison = r1.getOperationType().compareToIgnoreCase(r2.getOperationType());
                            break;
                        case "details":
                            comparison = r1.getDetails().compareToIgnoreCase(r2.getDetails());
                            break;
                    }
                    return ascending ? comparison : -comparison;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<HistoryRecord> filterHistoryRecordsByType(String operationType) {
        return historyRecords.stream()
                .filter(record -> record.getOperationType().equalsIgnoreCase(operationType))
                .collect(Collectors.toList());
    }

    public void clearAllData() {
        userDataList.clear();
        historyRecords.clear();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}