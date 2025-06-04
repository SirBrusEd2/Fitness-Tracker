package com.example.fitness_tracker.dao;

import com.example.fitness_tracker.model.UserData;
import com.example.fitness_tracker.model.HistoryRecord;
import javafx.scene.control.Alert;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация UserDataDao для хранения данных в файловой системе.
 * Сохраняет данные в текстовых файлах и обеспечивает базовые CRUD операции.
 */
public class FileUserDataDao implements UserDataDao {
    private static final String USER_DATA_FILE = "user_data.txt";
    private static final String HISTORY_FILE = "history.txt";
    private static final String FILE_PATH = "history_records.txt";

    /**
     * Сохраняет данные пользователя в файл.
     * @param userData Данные пользователя для сохранения
     */
    @Override
    public void saveUserData(UserData userData) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_DATA_FILE, true))) {
            writer.println(userData.getCurrentWeight() + "," + userData.getTargetWeight() + "," +
                    userData.getTargetDate() + "," + userData.getWorkoutsThisWeek() + "," +
                    userData.getAvgWorkoutDuration());
        } catch (IOException e) {
            showErrorAlert("Ошибка сохранения", e.getMessage());
        }
    }

    /**
     * Сохраняет запись истории в файл.
     * @param operationType Тип операции
     * @param details Детали операции
     */
    @Override
    public void saveHistoryRecord(String operationType, String details) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            int maxId = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    int id = Integer.parseInt(parts[0]);
                    if (id > maxId) {
                        maxId = id;
                    }
                }
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
                writer.println((maxId + 1) + "," + LocalDate.now() + "," + operationType + "," + details + "," + true);
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка сохранения истории", e.getMessage());
        }
    }

    /**
     * Получает все записи истории из файла.
     * @return Список записей истории
     */
    @Override
    public List<HistoryRecord> getHistoryRecords() {
        List<HistoryRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) { // Проверяем, что есть хотя бы 5 частей
                    int id = Integer.parseInt(parts[0]);
                    LocalDate date = LocalDate.parse(parts[1]);
                    String operationType = parts[2];
                    String details = parts[3];
                    boolean isActive = Boolean.parseBoolean(parts[4]);
                    records.add(new HistoryRecord(id, date, operationType, details, isActive));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }




    @Override
    public Optional<UserData> getUserDataById(int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    return Optional.of(new UserData(
                            Double.parseDouble(parts[0]),
                            Double.parseDouble(parts[1]),
                            LocalDate.parse(parts[2]),
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4])
                    ));
                }
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка получения данных", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void updateUserData(int id, UserData userData) {
        // В файле нет ID, поэтому просто перезаписываем файл
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_DATA_FILE))) {
            writer.println(userData.getCurrentWeight() + "," + userData.getTargetWeight() + "," +
                    userData.getTargetDate() + "," + userData.getWorkoutsThisWeek() + "," +
                    userData.getAvgWorkoutDuration());
        } catch (IOException e) {
            showErrorAlert("Ошибка обновления данных", e.getMessage());
        }
    }

    @Override
    public void deleteUserData(int id) {
        // В файле нет ID, поэтому просто очищаем файл
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_DATA_FILE))) {
            writer.print("");
        } catch (IOException e) {
            showErrorAlert("Ошибка удаления данных", e.getMessage());
        }
    }

    @Override
    public Optional<HistoryRecord> getHistoryRecordById(int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    return Optional.of(new HistoryRecord(
                            0, // ID не используется в файле
                            LocalDate.parse(parts[0]),
                            parts[1],
                            parts[2],
                            Boolean.parseBoolean(parts[3])
                    ));
                }
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка получения записи истории", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void updateHistoryRecord(int id, HistoryRecord record) {
        // В файле нет ID, поэтому просто перезаписываем файл
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            writer.println(record.getDate() + "," + record.getOperationType() + "," +
                    record.getDetails() + "," + record.isActive());
        } catch (IOException e) {
            showErrorAlert("Ошибка обновления истории", e.getMessage());
        }
    }

    @Override
    public void deleteHistoryRecord(int id) {
        // В файле нет ID, поэтому просто очищаем файл
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            writer.print("");
        } catch (IOException e) {
            showErrorAlert("Ошибка удаления записи истории", e.getMessage());
        }
    }

    @Override
    public void updateHistoryRecordStatus(int id, boolean newStatus) {
        // В файле нет ID, поэтому просто перезаписываем файл
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    lines.add(parts[0] + "," + parts[1] + "," + parts[2] + "," + newStatus);
                }
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
                for (String updatedLine : lines) {
                    writer.println(updatedLine);
                }
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка обновления статуса", e.getMessage());
        }
    }

    @Override
    public List<HistoryRecord> searchHistoryRecords(String searchTerm) {
        List<HistoryRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && (parts[1].contains(searchTerm) || parts[2].contains(searchTerm))) {
                    HistoryRecord record = new HistoryRecord(
                            0, // ID не используется в файле
                            LocalDate.parse(parts[0]),
                            parts[1],
                            parts[2],
                            Boolean.parseBoolean(parts[3])
                    );
                    records.add(record);
                }
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка поиска", e.getMessage());
        }
        return records;
    }

    @Override
    public List<HistoryRecord> getHistoryRecordsSorted(String sortBy, boolean ascending) {
        List<HistoryRecord> records = getHistoryRecords();
        // Простая сортировка в памяти
        records.sort((r1, r2) -> {
            switch (sortBy) {
                case "operation_date":
                    return ascending ? r1.getDate().compareTo(r2.getDate()) : r2.getDate().compareTo(r1.getDate());
                case "operation_type":
                    return ascending ? r1.getOperationType().compareTo(r2.getOperationType()) : r2.getOperationType().compareTo(r1.getOperationType());
                case "details":
                    return ascending ? r1.getDetails().compareTo(r2.getDetails()) : r2.getDetails().compareTo(r1.getDetails());
                default:
                    return 0;
            }
        });
        return records;
    }

    @Override
    public List<HistoryRecord> filterHistoryRecordsByType(String operationType) {
        List<HistoryRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[1].equals(operationType)) {
                    HistoryRecord record = new HistoryRecord(
                            0, // ID не используется в файле
                            LocalDate.parse(parts[0]),
                            parts[1],
                            parts[2],
                            Boolean.parseBoolean(parts[3])
                    );
                    records.add(record);
                }
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка фильтрации", e.getMessage());
        }
        return records;
    }

    private void showErrorAlert(String title, String message) {
        System.err.println("ERROR: " + title + " - " + message);  // Логирование в консоль
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
