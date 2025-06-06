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

/**
 * Реализация UserDataDao для хранения данных в памяти.
 * Использует паттерн Singleton для обеспечения единственного экземпляра.
 * Хранит данные в ObservableList для удобства работы с JavaFX.
 */
public class InMemoryUserDataDao implements UserDataDao {
    // Singleton instance
    private static final InMemoryUserDataDao INSTANCE = new InMemoryUserDataDao();

    private final ObservableList<UserData> userDataList = FXCollections.observableArrayList();
    private final ObservableList<HistoryRecord> historyRecords = FXCollections.observableArrayList();

    private InMemoryUserDataDao() {
        // Конструктор пустой
    }

    /**
     * Возвращает единственный экземпляр DAO.
     * @return Экземпляр InMemoryUserDataDao
     */
    public static InMemoryUserDataDao getInstance() {
        return INSTANCE;
    }

    /**
     * Сохраняет данные пользователя в памяти.
     * @param userData Данные пользователя для сохранения
     */
    @Override
    public void saveUserData(UserData userData) {
        // Всегда храним только одну запись пользователя
        if (userDataList.isEmpty()) {
            userDataList.add(userData);
        } else {
            userDataList.set(0, userData);
        }
    }

    /**
     * Сохраняет запись истории операций в памяти.
     * @param operationType тип выполненной операции (например, "Расчет прогресса")
     * @param details детали операции (результат вычислений)
     */
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

    /**
     * Получает все записи истории операций.
     * @return неизменяемый список записей истории
     */
    @Override
    public List<HistoryRecord> getHistoryRecords() {
        return List.copyOf(historyRecords);
    }

    /**
     * Получает данные пользователя по индексу.
     * @param index индекс записи (только 0 поддерживается)
     * @return Optional с данными пользователя, если они существуют
     */
    @Override
    public Optional<UserData> getUserDataById(int index) {
        if (index == 0 && !userDataList.isEmpty()) {
            return Optional.of(userDataList.get(0));
        }
        return Optional.empty();
    }

    /**
     * Обновляет данные пользователя.
     * @param index индекс записи (только 0 поддерживается)
     * @param userData новые данные пользователя
     */
    @Override
    public void updateUserData(int index, UserData userData) {
        if (index == 0 && !userDataList.isEmpty()) {
            userDataList.set(0, userData);
        }
    }

    /**
     * Удаляет данные пользователя.
     * @param index индекс записи (только 0 поддерживается)
     */
    @Override
    public void deleteUserData(int index) {
        if (index == 0 && !userDataList.isEmpty()) {
            userDataList.clear();
        }
    }

    /**
     * Получает запись истории по индексу.
     * @param index индекс записи истории
     * @return Optional с записью истории, если она существует
     */
    @Override
    public Optional<HistoryRecord> getHistoryRecordById(int index) {
        if (index >= 0 && index < historyRecords.size()) {
            return Optional.of(historyRecords.get(index));
        }
        return Optional.empty();
    }

    /**
     * Обновляет запись истории.
     * @param index индекс записи для обновления
     * @param record новые данные записи истории
     */
    @Override
    public void updateHistoryRecord(int index, HistoryRecord record) {
        if (index >= 0 && index < historyRecords.size()) {
            historyRecords.set(index, record);
        }
    }

    /**
     * Удаляет запись истории.
     * @param index индекс записи для удаления
     */
    @Override
    public void deleteHistoryRecord(int index) {
        if (index >= 0 && index < historyRecords.size()) {
            historyRecords.remove(index);
        }
    }

    /**
     * Обновляет статус активности записи истории.
     * @param index индекс записи
     * @param newStatus новый статус активности (true/false)
     */
    @Override
    public void updateHistoryRecordStatus(int index, boolean newStatus) {
        if (index >= 0 && index < historyRecords.size()) {
            historyRecords.get(index).setActive(newStatus);
        }
    }

    /**
     * Ищет записи истории по ключевому слову.
     * @param searchTerm ключевое слово для поиска
     * @return список найденных записей истории
     */
    @Override
    public List<HistoryRecord> searchHistoryRecords(String searchTerm) {
        return historyRecords.stream()
                .filter(record -> record.getOperationType().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        record.getDetails().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает отсортированные записи истории.
     * @param sortBy поле для сортировки (date, operationType, details)
     * @param ascending направление сортировки (true - по возрастанию)
     * @return отсортированный список записей
     */
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

    /**
     * Фильтрует записи истории по типу операции.
     * @param operationType тип операции для фильтрации
     * @return отфильтрованный список записей
     */
    @Override
    public List<HistoryRecord> filterHistoryRecordsByType(String operationType) {
        return historyRecords.stream()
                .filter(record -> record.getOperationType().equalsIgnoreCase(operationType))
                .collect(Collectors.toList());
    }

    /**
     * Очищает все хранимые данные (для тестирования).
     */
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