package com.example.fitness_tracker.service;

import com.example.fitness_tracker.dao.UserDataDao;
import com.example.fitness_tracker.model.UserData;
import com.example.fitness_tracker.model.HistoryRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * Реализация сервисного слоя приложения.
 * Содержит бизнес-логику и делегирует операции с данными DAO.
 */
public class FitnessServiceImpl implements FitnessService {
    private final UserDataDao userDataDao;

    /**
     * Создает экземпляр сервиса с указанным DAO.
     * @param userDataDao DAO для работы с данными
     */
    public FitnessServiceImpl(UserDataDao userDataDao) {
        this.userDataDao = userDataDao;
    }

    /**
     * Рассчитывает прогресс потери веса с учетом безопасных темпов.
     * @param userData Данные пользователя
     * @return Форматированная строка с результатами расчета
     */
    @Override
    public String calculateProgress(UserData userData) {
        // Рассчитываем количество дней до цели
        long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), userData.getTargetDate());
        double weightDiff = userData.getCurrentWeight() - userData.getTargetWeight();

        if (days <= 0) {
            return "Ошибка: срок должен быть в будущем!";
        }

        // Расчет по неделям (более здоровый подход)
        double weeklyLoss = weightDiff / (days / 7.0);
        double dailyLoss = weeklyLoss / 7;

        String result = String.format("Для цели нужно терять %.2f кг/неделю (%.2f кг/день)",
                weeklyLoss, dailyLoss);

        // Предупреждения о здоровье
        if (weeklyLoss > 1.0) {
            result += "\n⚠️ Внимание! Потеря более 1 кг/неделю может быть вредна для здоровья!";
        } else if (weeklyLoss < 0.5) {
            result += "\n💡 Можно немного увеличить дефицит калорий";
        } else {
            result += "\n✅ Это безопасный и эффективный темп похудения";
        }

        return result;
    }

    /**
     * Анализирует нагрузку пользователя и дает рекомендации.
     * @param userData Данные пользователя
     * @return Рекомендация по тренировочной нагрузке
     */
    @Override
    public String checkOvertraining(UserData userData) {
        int totalMinutes = userData.getWorkoutsThisWeek() * userData.getAvgWorkoutDuration();

        if (userData.getWorkoutsThisWeek() > 6 || totalMinutes > 420) {
            return "Перетренированность! Отдых 2-3 дня.";
        } else if (userData.getWorkoutsThisWeek() > 4 || totalMinutes > 300) {
            return "Высокая нагрузка. Отдых 1-2 дня.";
        } else if (userData.getWorkoutsThisWeek() < 3 || totalMinutes < 150) {
            return "Можно добавить 1-2 тренировки.";
        } else {
            return "Оптимальная нагрузка!";
        }
    }

    /**
     * Конвертирует мили в километры.
     * @param miles значение в милях
     * @return строка с результатом конвертации
     */
    @Override
    public String convertMilesToKm(double miles) {
        double km = miles * 1.60934;
        return String.format("%.2f миль = %.2f км", miles, km);
    }

    /**
     * Сохраняет расчет прогресса в историю.
     * @param userData данные пользователя
     * @param result результат расчета
     */
    @Override
    public void saveProgressCalculation(UserData userData, String result) {
        userDataDao.saveUserData(userData);
        userDataDao.saveHistoryRecord("Расчет прогресса", result);
    }

    /**
     * Сохраняет данные тренировки и рекомендации в историю.
     * @param userData данные пользователя
     * @param recommendation сгенерированная рекомендация
     */
    @Override
    public void saveWorkoutData(UserData userData, String recommendation) {
        userDataDao.saveUserData(userData);
        userDataDao.saveHistoryRecord("Анализ нагрузки", recommendation);
    }

    /**
     * Сохраняет результат конвертации в историю.
     * @param miles значение в милях
     * @param result результат конвертации
     */
    @Override
    public void saveConversion(double miles, String result) {
        userDataDao.saveHistoryRecord("Конвертация", result);
    }

    /**
     * Загружает историю операций из DAO.
     * @return список записей истории
     */
    @Override
    public List<HistoryRecord> loadHistory() {
        return userDataDao.getHistoryRecords();
    }

    /**
     * Обновляет запись истории через DAO.
     * @param id идентификатор записи
     * @param record новые данные записи
     */
    @Override
    public void updateHistoryRecord(int id, HistoryRecord record) {
        userDataDao.updateHistoryRecord(id, record);
    }

    /**
     * Удаляет запись истории через DAO.
     * @param id идентификатор записи
     */
    @Override
    public void deleteHistoryRecord(int id) {
        userDataDao.deleteHistoryRecord(id);
    }

    /**
     * Ищет записи истории через DAO.
     * @param searchTerm ключевое слово для поиска
     * @return список найденных записей
     */
    @Override
    public List<HistoryRecord> searchHistoryRecords(String searchTerm) {
        return userDataDao.searchHistoryRecords(searchTerm);
    }

    /**
     * Возвращает отсортированные записи истории через DAO.
     * @param sortBy поле для сортировки
     * @param ascending направление сортировки
     * @return отсортированный список записей
     */
    @Override
    public List<HistoryRecord> getHistoryRecordsSorted(String sortBy, boolean ascending) {
        return userDataDao.getHistoryRecordsSorted(sortBy, ascending);
    }

    /**
     * Фильтрует записи истории по типу операции через DAO.
     * @param operationType тип операции
     * @return отфильтрованный список записей
     */
    @Override
    public List<HistoryRecord> filterHistoryRecordsByType(String operationType) {
        return userDataDao.filterHistoryRecordsByType(operationType);
    }
}
