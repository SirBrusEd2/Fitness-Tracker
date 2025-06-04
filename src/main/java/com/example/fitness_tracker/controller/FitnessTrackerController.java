package com.example.fitness_tracker.controller;

import com.example.fitness_tracker.dao.FileUserDataDao;
import com.example.fitness_tracker.dao.InMemoryUserDataDao;
import com.example.fitness_tracker.dao.PostgresUserDataDao;
import com.example.fitness_tracker.dao.UserDataDao;
import com.example.fitness_tracker.model.UserData;
import com.example.fitness_tracker.model.HistoryRecord;
import com.example.fitness_tracker.service.FitnessService;
import com.example.fitness_tracker.service.FitnessServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;

/**
 * Основной контроллер приложения Fitness Tracker.
 * Управляет пользовательским интерфейсом и обработкой событий.
 * Обеспечивает взаимодействие между UI и сервисным слоем.
 */
public class FitnessTrackerController {
    @FXML private TextField currentWeightField;
    @FXML private TextField targetWeightField;
    @FXML private TextField daysPeriod;
    @FXML private TextField workoutsField;
    @FXML private TextField durationField;
    @FXML private TextField milesField;
    @FXML private Label progressResult;
    @FXML private Label recommendationResult;
    @FXML private Label conversionResult;
    @FXML private TableView<HistoryRecord> historyTable;

    @FXML private GridPane editForm;
    @FXML private TextField editIdField;
    @FXML private TextField editOperationTypeField;
    @FXML private TextField editDetailsField;
    @FXML private CheckBox editStatusCheckBox;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private CheckBox ascendingCheckBox;
    @FXML private RadioButton dbRadioButton;
    @FXML private RadioButton fileRadioButton;
    @FXML private RadioButton memoryRadioButton;

    private UserDataDao dataSource;
    private FitnessService fitnessService;
    private final ObservableList<HistoryRecord> historyData = FXCollections.observableArrayList();

    public FitnessTrackerController() {
        this.dataSource = new PostgresUserDataDao(); // По умолчанию выбираем базу данных
        this.fitnessService = new FitnessServiceImpl(dataSource);
    }

    /**
     * Инициализирует контроллер и настраивает UI компоненты.
     * Загружает историю операций и настраивает обработчики событий.
     */
    @FXML
    public void initialize() {
        // Инициализация RadioButton для выбора источника данных
        ToggleGroup dataSourceGroup = new ToggleGroup();
        dbRadioButton.setToggleGroup(dataSourceGroup);
        fileRadioButton.setToggleGroup(dataSourceGroup);
        memoryRadioButton.setToggleGroup(dataSourceGroup);

        dbRadioButton.setSelected(true);

        dbRadioButton.setOnAction(event -> {
            if (dbRadioButton.isSelected()) {
                dataSource = new PostgresUserDataDao();
                fitnessService = new FitnessServiceImpl(dataSource);
                loadHistory(); // Перезагружаем историю при смене источника данных
            }
        });

        fileRadioButton.setOnAction(event -> {
            if (fileRadioButton.isSelected()) {
                dataSource = new FileUserDataDao();
                fitnessService = new FitnessServiceImpl(dataSource);
                loadHistory(); // Перезагружаем историю при смене источника данных
            }
        });

        memoryRadioButton.setOnAction(event -> {
            if (memoryRadioButton.isSelected()) {
                dataSource = InMemoryUserDataDao.getInstance();
                fitnessService = new FitnessServiceImpl(dataSource);
                loadHistory(); // Перезагружаем историю при смене источника данных
            }
        });

        historyTable.setItems(historyData);
        loadHistory(); // Загружаем историю при старте

        // Инициализация ComboBox для сортировки и фильтрации
        sortComboBox.getItems().addAll(
                "Дата", "Тип операции", "Детали"
        );
        sortComboBox.getSelectionModel().selectFirst();

        filterComboBox.getItems().addAll(
                "Все", "Расчет прогресса", "Анализ нагрузки", "Конвертация"
        );
        filterComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Рассчитывает прогресс потери веса на основе введенных данных.
     * Сохраняет результат в выбранном источнике данных.
     */
    @FXML
    private void calculateProgress() {
        try {
            double currentWeight = Double.parseDouble(currentWeightField.getText());
            double targetWeight = Double.parseDouble(targetWeightField.getText());
            int days = Integer.parseInt(daysPeriod.getText());

            UserData userData = new UserData(currentWeight, targetWeight,
                    LocalDate.now().plusDays(days), 0, 0);

            String result = fitnessService.calculateProgress(userData);
            progressResult.setText(result);

            // Сохраняем в выбранный источник данных
            fitnessService.saveProgressCalculation(userData, result);

            // Добавляем в историю
            HistoryRecord record = new HistoryRecord(0, LocalDate.now(), "Расчет прогресса", result, true);
            historyData.add(record);

        } catch (Exception e) {
            progressResult.setText("Ошибка ввода данных!");
            e.printStackTrace();
        }
    }

    /**
     * Проверяет наличие перетренированности на основе введенных данных.
     * Сохраняет результат в выбранном источнике данных.
     */
    @FXML
    private void checkOvertraining() {
        try {
            int workouts = Integer.parseInt(workoutsField.getText());
            int duration = Integer.parseInt(durationField.getText());

            UserData userData = new UserData(0, 0, LocalDate.now(), workouts, duration);

            String recommendation = fitnessService.checkOvertraining(userData);
            recommendationResult.setText(recommendation);

            // Сохраняем в выбранный источник данных
            fitnessService.saveWorkoutData(userData, recommendation);

            // Добавляем в историю
            HistoryRecord record = new HistoryRecord(0, LocalDate.now(), "Анализ нагрузки", recommendation, true);
            historyData.add(record);

        } catch (NumberFormatException e) {
            recommendationResult.setText("Ошибка ввода данных! Убедитесь, что введены корректные числа.");
        } catch (Exception e) {
            recommendationResult.setText("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Конвертирует мили в километры.
     * Сохраняет результат в выбранном источнике данных.
     */
    @FXML
    private void convertToKm() {
        try {
            double miles = Double.parseDouble(milesField.getText());
            String result = fitnessService.convertMilesToKm(miles);
            conversionResult.setText(result);

            // Сохраняем в выбранный источник данных
            fitnessService.saveConversion(miles, result);

            // Добавляем в историю
            HistoryRecord record = new HistoryRecord(0, LocalDate.now(), "Конвертация", result, true);
            historyData.add(record);

        } catch (Exception e) {
            conversionResult.setText("Ошибка ввода данных!");
            e.printStackTrace();
        }
    }

    /**
     * Загружает историю операций из выбранного источника данных.
     */
    @FXML
    private void loadHistory() {
        try {
            List<HistoryRecord> records = dataSource.getHistoryRecords();
            historyData.setAll(records);
        } catch (Exception e) {
            showAlert("Ошибка загрузки истории", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Редактирует выбранную запись истории.
     */
    @FXML
    private void editHistoryRecord() {
        HistoryRecord selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            editIdField.setText(String.valueOf(selected.getId()));
            editOperationTypeField.setText(selected.getOperationType());
            editDetailsField.setText(selected.getDetails());
            editStatusCheckBox.setSelected(selected.isActive());
            editForm.setVisible(true);
        }
    }

    /**
     * Удаляет выбранную запись истории.
     */
    @FXML
    private void deleteHistoryRecord() {
        HistoryRecord selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dataSource.deleteHistoryRecord(selected.getId());
            loadHistory(); // Обновляем таблицу
        }
    }

    /**
     * Сохраняет отредактированную запись истории.
     */
    @FXML
    private void saveEditedRecord() {
        try {
            int id = Integer.parseInt(editIdField.getText());
            String operationType = editOperationTypeField.getText();
            String details = editDetailsField.getText();
            boolean isActive = editStatusCheckBox.isSelected();

            HistoryRecord updated = new HistoryRecord(
                    id,
                    LocalDate.now(),
                    operationType,
                    details,
                    isActive
            );

            dataSource.updateHistoryRecord(id, updated);
            loadHistory(); // Обновляем таблицу
            cancelEditing();
        } catch (Exception e) {
            showAlert("Ошибка сохранения", e.getMessage());
        }
    }

    /**
     * Отменяет редактирование записи истории.
     */
    @FXML
    private void cancelEditing() {
        editForm.setVisible(false);
        editIdField.clear();
        editOperationTypeField.clear();
        editDetailsField.clear();
        editStatusCheckBox.setSelected(false);
    }

    /**
     * Выполняет поиск по истории операций.
     */
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();
        if (searchTerm.isEmpty()) {
            loadHistory();
        } else {
            List<HistoryRecord> records = dataSource.searchHistoryRecords(searchTerm);
            historyData.setAll(records);
        }
    }

    /**
     * Сортирует записи истории по выбранному критерию.
     */
    @FXML
    private void handleSort() {
        String sortBy = sortComboBox.getValue();
        boolean ascending = ascendingCheckBox.isSelected();

        String sortColumn;
        switch (sortBy) {
            case "Дата": sortColumn = "operation_date"; break;
            case "Тип операции": sortColumn = "operation_type"; break;
            case "Детали": sortColumn = "details"; break;
            default: sortColumn = "operation_date";
        }

        List<HistoryRecord> records = dataSource.getHistoryRecordsSorted(sortColumn, ascending);
        historyData.setAll(records);
    }

    /**
     * Фильтрует записи истории по типу операции.
     */
    @FXML
    private void handleFilter() {
        String filterType = filterComboBox.getValue();
        if (filterType.equals("Все")) {
            loadHistory();
        } else {
            List<HistoryRecord> records = dataSource.filterHistoryRecordsByType(filterType);
            historyData.setAll(records);
        }
    }

    /**
     * Изменяет статус записи истории (активная/неактивная).
     * @param event Событие действия
     */
    @FXML
    private void toggleStatus(ActionEvent event) {
        HistoryRecord selectedRecord = historyTable.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            boolean newStatus = !selectedRecord.isActive();
            dataSource.updateHistoryRecordStatus(selectedRecord.getId(), newStatus);
            selectedRecord.setActive(newStatus);
            historyTable.refresh();
        } else {
            showAlert("Ошибка", "Не выбрана запись для изменения статуса");
        }
    }
}
