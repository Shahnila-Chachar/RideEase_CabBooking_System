package com.rideease.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        Label heading = new Label("Welcome to RideEase!");

        StackPane root = new StackPane(heading);

        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("RideEase - Cab Booking System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}