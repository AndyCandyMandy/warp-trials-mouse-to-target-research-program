package com.example.cmpt481_term_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        MainUI uiRoot = new MainUI();
        Scene scene = new Scene(uiRoot);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        uiRoot.requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}