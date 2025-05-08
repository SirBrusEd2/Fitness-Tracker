package com.example.fitness_tracker.model;

import java.time.LocalDate;

public class UserData {
    private double currentWeight;
    private double targetWeight;
    private LocalDate targetDate;
    private int workoutsThisWeek;
    private int avgWorkoutDuration;

    public UserData() {}

    public UserData(double currentWeight, double targetWeight, LocalDate targetDate,
                    int workoutsThisWeek, int avgWorkoutDuration) {
        this.currentWeight = currentWeight;
        this.targetWeight = targetWeight;
        this.targetDate = targetDate;
        this.workoutsThisWeek = workoutsThisWeek;
        this.avgWorkoutDuration = avgWorkoutDuration;
    }

    // Геттеры
    public double getCurrentWeight() {
        return currentWeight;
    }

    public double getTargetWeight() {
        return targetWeight;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public int getWorkoutsThisWeek() {
        return workoutsThisWeek;
    }

    public int getAvgWorkoutDuration() {
        return avgWorkoutDuration;
    }

    // Сеттеры
    public void setCurrentWeight(double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public void setWorkoutsThisWeek(int workoutsThisWeek) {
        this.workoutsThisWeek = workoutsThisWeek;
    }

    public void setAvgWorkoutDuration(int avgWorkoutDuration) {
        this.avgWorkoutDuration = avgWorkoutDuration;
    }
}