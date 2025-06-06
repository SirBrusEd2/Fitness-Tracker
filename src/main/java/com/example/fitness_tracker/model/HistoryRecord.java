package com.example.fitness_tracker.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Модель записи истории операций.
 * Содержит информацию о дате, типе операции, деталях и статусе.
 */
public class HistoryRecord {
    private int id;  // Добавляем поле ID
    private LocalDate date;
    private String operationType;
    private String details;
    private boolean active;

    /**
     * Создает новую запись истории.
     * @param id Уникальный идентификатор записи
     * @param date Дата операции
     * @param operationType Тип операции
     * @param details Детали операции
     * @param active Статус активности записи
     */
    // Конструктор с ID
    public HistoryRecord(int id, LocalDate date, String operationType, String details, boolean active) {
        this.id = id;
        this.date = date;
        this.operationType = operationType;
        this.details = details;
        this.active = active;
    }

    // Конструктор без ID (для обратной совместимости)
    public HistoryRecord(LocalDate date, String operationType, String details, boolean active) {
        this(0, date, operationType, details, active);
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDate() { return date; }
    /**
     * Возвращает дату операции в строковом формате.
     * @return Дата в формате "dd.MM.yyyy"
     */
    public String getDateString() {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String getOperationType() { return operationType; }
    public String getDetails() { return details; }

    /**
     * Проверяет, активна ли запись.
     * @return true если запись активна
     */
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Возвращает статус записи в виде строки.
     * @return "Активно" или "Неактивно"
     */
    // Добавьте метод для строкового представления статуса
    public String getActiveStatus() {
        return active ? "Активно" : "Неактивно";
    }

}