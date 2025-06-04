package com.example.fitness_tracker;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Главный класс приложения, запускающий JavaFX приложение.
 */
public class Application extends javafx.application.Application {
    /**
     * Запускает приложение и отображает главное окно.
     * @param stage Основное окно приложения
     * @throws IOException Если не удается загрузить FXML файл
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 700);
        stage.setTitle("Фитнес-трекер");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Точка входа в приложение.
     * @param args Аргументы командной строки
     */
    public static void main(String[] args) {
        launch();
    }
}