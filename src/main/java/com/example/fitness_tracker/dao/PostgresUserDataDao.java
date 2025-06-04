package com.example.fitness_tracker.dao;

import com.example.fitness_tracker.model.UserData;
import com.example.fitness_tracker.model.HistoryRecord;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация UserDataDao для работы с PostgreSQL.
 * Обеспечивает подключение к базе данных и выполнение SQL запросов.
 */
public class PostgresUserDataDao implements UserDataDao {
    private Connection connection;

    /**
     * Создает соединение с PostgreSQL и необходимые таблицы, если они не существуют.
     */
    public PostgresUserDataDao() {
        try {
            String url = "jdbc:postgresql://localhost:7777/ft_bd";
            String user = "postgres";
            String password = "postgres";
            connection = DriverManager.getConnection(url, user, password);
            createTablesIfNotExist();
        } catch (SQLException e) {
            showErrorAlert("Ошибка подключения", e.getMessage());
            throw new RuntimeException("Failed to connect to PostgreSQL", e);
        }
    }

    private void createTablesIfNotExist() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Таблица пользовательских данных
            stmt.execute("CREATE TABLE IF NOT EXISTS user_data (" +
                    "id SERIAL PRIMARY KEY," +
                    "current_weight DOUBLE PRECISION," +
                    "target_weight DOUBLE PRECISION," +
                    "target_date DATE," +
                    "workouts_this_week INTEGER," +
                    "avg_workout_duration INTEGER)");

            // Таблица истории операций
            stmt.execute("CREATE TABLE IF NOT EXISTS history (" +
                    "id SERIAL PRIMARY KEY," +
                    "operation_date DATE," +
                    "operation_type VARCHAR(50)," +
                    "details TEXT," +
                    "active BOOLEAN DEFAULT true)");
        }
    }

    /**
     * Сохраняет данные пользователя в базу данных.
     * @param userData Данные пользователя для сохранения
     */
    @Override
    public void saveUserData(UserData userData) {
        String sql = "INSERT INTO user_data (current_weight, target_weight, target_date, " +
                "workouts_this_week, avg_workout_duration) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, userData.getCurrentWeight());
            pstmt.setDouble(2, userData.getTargetWeight());
            pstmt.setDate(3, Date.valueOf(userData.getTargetDate()));
            pstmt.setInt(4, userData.getWorkoutsThisWeek());
            pstmt.setInt(5, userData.getAvgWorkoutDuration());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            showErrorAlert("Ошибка сохранения", e.getMessage());
        }
    }

    /**
     * Сохраняет запись истории в базу данных.
     * @param operationType Тип операции
     * @param details Детали операции
     */
    @Override
    public void saveHistoryRecord(String operationType, String details) {
        String sql = "INSERT INTO history (operation_date, operation_type, details, active) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setString(2, operationType);
            pstmt.setString(3, details);
            pstmt.setBoolean(4, true);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            showErrorAlert("Ошибка сохранения истории", e.getMessage());
        }
    }

    /**
     * Получает все записи истории из базы данных.
     * @return список записей истории, отсортированный по дате (новые сначала)
     */
    @Override
    public List<HistoryRecord> getHistoryRecords() {
        List<HistoryRecord> records = new ArrayList<>();
        String sql = "SELECT id, operation_date, operation_type, details, active FROM history ORDER BY operation_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                HistoryRecord record = new HistoryRecord(
                        rs.getInt("id"),
                        rs.getDate("operation_date").toLocalDate(),
                        rs.getString("operation_type"),
                        rs.getString("details"),
                        rs.getBoolean("active")
                );
                records.add(record);
            }
        } catch (SQLException e) {
            showErrorAlert("Ошибка загрузки истории", "Не удалось загрузить историю: " + e.getMessage());
            e.printStackTrace();
        }
        return records;
    }

    /**
     * Получает данные пользователя по ID.
     * @param id идентификатор пользователя
     * @return Optional с данными пользователя, если они существуют
     */
    @Override
    public Optional<UserData> getUserDataById(int id) {
        String sql = "SELECT * FROM user_data WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new UserData(
                        rs.getDouble("current_weight"),
                        rs.getDouble("target_weight"),
                        rs.getDate("target_date").toLocalDate(),
                        rs.getInt("workouts_this_week"),
                        rs.getInt("avg_workout_duration")
                ));
            }
        } catch (SQLException e) {
            showErrorAlert("Ошибка получения данных", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Обновляет данные пользователя в базе данных.
     * @param id идентификатор пользователя
     * @param userData новые данные пользователя
     */
    @Override
    public void updateUserData(int id, UserData userData) {
        String sql = "UPDATE user_data SET current_weight = ?, target_weight = ?, " +
                "target_date = ?, workouts_this_week = ?, avg_workout_duration = ? " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, userData.getCurrentWeight());
            pstmt.setDouble(2, userData.getTargetWeight());
            pstmt.setDate(3, Date.valueOf(userData.getTargetDate()));
            pstmt.setInt(4, userData.getWorkoutsThisWeek());
            pstmt.setInt(5, userData.getAvgWorkoutDuration());
            pstmt.setInt(6, id);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            showErrorAlert("Ошибка обновления данных", e.getMessage());
        }
    }

    /**
     * Удаляет данные пользователя из базы данных.
     * @param id идентификатор пользователя
     */
    @Override
    public void deleteUserData(int id) {
        String sql = "DELETE FROM user_data WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showErrorAlert("Ошибка удаления данных", e.getMessage());
        }
    }

    /**
     * Получает запись истории по ID.
     * @param id идентификатор записи истории
     * @return Optional с записью истории, если она существует
     */
    // Методы для работы с историей
    @Override
    public Optional<HistoryRecord> getHistoryRecordById(int id) {
        String sql = "SELECT * FROM history WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new HistoryRecord(
                        rs.getInt("id"),  // Добавляем ID
                        rs.getDate("operation_date").toLocalDate(),
                        rs.getString("operation_type"),
                        rs.getString("details"),
                        rs.getBoolean("active")
                ));
            }
        } catch (SQLException e) {
            showErrorAlert("Ошибка получения записи истории", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Обновляет запись истории в базе данных.
     * @param id идентификатор записи
     * @param record новые данные записи истории
     */
    @Override
    public void updateHistoryRecord(int id, HistoryRecord record) {
        String sql = "UPDATE history SET operation_date = ?, operation_type = ?, details = ?, active = ? " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(record.getDate()));
            pstmt.setString(2, record.getOperationType());
            pstmt.setString(3, record.getDetails());
            pstmt.setBoolean(4, record.isActive());
            pstmt.setInt(5, id);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            showErrorAlert("Ошибка обновления истории", e.getMessage());
        }
    }

    /**
     * Удаляет запись истории из базы данных.
     * @param id идентификатор записи
     */
    @Override
    public void deleteHistoryRecord(int id) {
        String sql = "DELETE FROM history WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showErrorAlert("Ошибка удаления записи истории", e.getMessage());
        }
    }

    /**
     * Обновляет статус активности записи истории.
     * @param id идентификатор записи
     * @param newStatus новый статус активности (true/false)
     */
    @Override
    public void updateHistoryRecordStatus(int id, boolean newStatus) {
        String sql = "UPDATE history SET active = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, newStatus);
            pstmt.setInt(2, id);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            showErrorAlert("Ошибка обновления статуса", e.getMessage());
        }
    }

    /**
     * Ищет записи истории по ключевому слову (без учета регистра).
     * @param searchTerm ключевое слово для поиска
     * @return список найденных записей, отсортированный по дате (новые сначала)
     */
    @Override
    public List<HistoryRecord> searchHistoryRecords(String searchTerm) {
        List<HistoryRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM history WHERE " +
                "operation_type ILIKE ? OR details ILIKE ? " +
                "ORDER BY operation_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(new HistoryRecord(
                        rs.getInt("id"),
                        rs.getDate("operation_date").toLocalDate(),
                        rs.getString("operation_type"),
                        rs.getString("details"),
                        rs.getBoolean("active")
                ));
            }
        } catch (SQLException e) {
            showErrorAlert("Ошибка поиска", e.getMessage());
        }
        return records;
    }

    /**
     * Возвращает записи истории, отсортированные по указанному полю.
     * @param sortBy поле для сортировки (operation_date, operation_type, details)
     * @param ascending направление сортировки (true - по возрастанию)
     * @return отсортированный список записей
     */
    @Override
    public List<HistoryRecord> getHistoryRecordsSorted(String sortBy, boolean ascending) {
        List<HistoryRecord> records = new ArrayList<>();
        String direction = ascending ? "ASC" : "DESC";
        String sql = "SELECT * FROM history ORDER BY " + sortBy + " " + direction;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                records.add(new HistoryRecord(
                        rs.getInt("id"),
                        rs.getDate("operation_date").toLocalDate(),
                        rs.getString("operation_type"),
                        rs.getString("details"),
                        rs.getBoolean("active")
                ));
            }
        } catch (SQLException e) {
            showErrorAlert("Ошибка сортировки", e.getMessage());
        }
        return records;
    }

    /**
     * Фильтрует записи истории по типу операции.
     * @param operationType тип операции для фильтрации
     * @return отфильтрованный список записей, отсортированный по дате (новые сначала)
     */
    @Override
    public List<HistoryRecord> filterHistoryRecordsByType(String operationType) {
        List<HistoryRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM history WHERE operation_type = ? ORDER BY operation_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, operationType);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(new HistoryRecord(
                        rs.getInt("id"),
                        rs.getDate("operation_date").toLocalDate(),
                        rs.getString("operation_type"),
                        rs.getString("details"),
                        rs.getBoolean("active")
                ));
            }
        } catch (SQLException e) {
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
