package com.example.fitness_tracker.service;

import com.example.fitness_tracker.dao.UserDataDao;
import com.example.fitness_tracker.model.UserData;
import com.example.fitness_tracker.model.HistoryRecord;

import java.time.LocalDate;
import java.util.List;

public class FitnessServiceImpl implements FitnessService {
    private final UserDataDao userDataDao;

    public FitnessServiceImpl(UserDataDao userDataDao) {
        this.userDataDao = userDataDao;
    }

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

    @Override
    public String convertMilesToKm(double miles) {
        double km = miles * 1.60934;
        return String.format("%.2f миль = %.2f км", miles, km);
    }

    @Override
    public void saveProgressCalculation(UserData userData, String result) {
        userDataDao.saveUserData(userData);
        userDataDao.saveHistoryRecord("Расчет прогресса", result);
    }

    @Override
    public void saveWorkoutData(UserData userData, String recommendation) {
        userDataDao.saveUserData(userData);
        userDataDao.saveHistoryRecord("Анализ нагрузки", recommendation);
    }

    @Override
    public void saveConversion(double miles, String result) {
        userDataDao.saveHistoryRecord("Конвертация", result);
    }

    @Override
    public List<HistoryRecord> loadHistory() {
        return userDataDao.getHistoryRecords();
    }

    @Override
    public void updateHistoryRecord(int id, HistoryRecord record) {
        userDataDao.updateHistoryRecord(id, record);
    }

    @Override
    public void deleteHistoryRecord(int id) {
        userDataDao.deleteHistoryRecord(id);
    }

    @Override
    public List<HistoryRecord> searchHistoryRecords(String searchTerm) {
        return userDataDao.searchHistoryRecords(searchTerm);
    }

    @Override
    public List<HistoryRecord> getHistoryRecordsSorted(String sortBy, boolean ascending) {
        return userDataDao.getHistoryRecordsSorted(sortBy, ascending);
    }

    @Override
    public List<HistoryRecord> filterHistoryRecordsByType(String operationType) {
        return userDataDao.filterHistoryRecordsByType(operationType);
    }
}
