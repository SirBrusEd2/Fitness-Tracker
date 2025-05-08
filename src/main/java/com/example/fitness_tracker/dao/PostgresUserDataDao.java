package com.example.fitness_tracker.dao;

import com.example.fitness_tracker.model.UserData;
import com.example.fitness_tracker.model.HistoryRecord;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresUserDataDao implements UserDataDao {
    private Connection connection;

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
