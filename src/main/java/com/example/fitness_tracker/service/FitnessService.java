package com.example.fitness_tracker.service;

import com.example.fitness_tracker.model.UserData;
import com.example.fitness_tracker.model.HistoryRecord;

import java.util.List;

/**
 * Интерфейс сервисного слоя приложения.
 * Определяет бизнес-логику и операции с данными.
 */
public interface FitnessService {
    /**
     * Рассчитывает прогресс потери веса.
     * @param userData Данные пользователя
     * @return Результат расчета
     */
    String calculateProgress(UserData userData);
    /**
     * Проверяет наличие перетренированности.
     * @param userData Данные пользователя
     * @return Рекомендация по нагрузке
     */
    String checkOvertraining(UserData userData);
    /**
     * Конвертирует мили в километры.
     * @param miles Значение в милях
     * @return Результат конвертации
     */
    String convertMilesToKm(double miles);

    /**
     * Сохраняет расчет прогресса в историю.
     * @param userData данные пользователя
     * @param result результат расчета
     */
    void saveProgressCalculation(UserData userData, String result);
    /**
     * Сохраняет данные тренировки и рекомендации в историю.
     * @param userData данные пользователя
     * @param recommendation сгенерированная рекомендация
     */
    void saveWorkoutData(UserData userData, String recommendation);
    /**
     * Сохраняет результат конвертации в историю.
     * @param miles значение в милях
     * @param result результат конвертации
     */
    void saveConversion(double miles, String result);

    /**
     * Загружает историю операций.
     * @return список записей истории
     */
    List<HistoryRecord> loadHistory();
    void updateHistoryRecord(int id, HistoryRecord record);
    void deleteHistoryRecord(int id);

    /**
     * Ищет записи истории по ключевому слову.
     * @param searchTerm ключевое слово для поиска
     * @return список найденных записей
     */
    List<HistoryRecord> searchHistoryRecords(String searchTerm);
    /**
     * Возвращает отсортированные записи истории.
     * @param sortBy поле для сортировки
     * @param ascending направление сортировки
     * @return отсортированный список записей
     */
    List<HistoryRecord> getHistoryRecordsSorted(String sortBy, boolean ascending);
    /**
     * Фильтрует записи истории по типу операции.
     * @param operationType тип операции
     * @return отфильтрованный список записей
     */
    List<HistoryRecord> filterHistoryRecordsByType(String operationType);
}
