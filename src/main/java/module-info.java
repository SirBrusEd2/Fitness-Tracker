module com.example.fitness_tracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.net.http;
    requires org.postgresql.jdbc;
    requires google.api.client;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.services.sheets;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires java.sql;


    // Открываем пакеты для JavaFX FXML
    opens com.example.fitness_tracker to javafx.fxml;
    opens com.example.fitness_tracker.controller to javafx.fxml;
    opens com.example.fitness_tracker.model to javafx.fxml, com.fasterxml.jackson.databind;

    exports com.example.fitness_tracker;
    exports com.example.fitness_tracker.controller;
    exports com.example.fitness_tracker.model;
}