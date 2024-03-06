/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application
{
    @Override
    public void start(Stage stage)
    {
        MainUI uiRoot = new MainUI();
        Scene scene = new Scene(uiRoot);
        stage.setTitle("Target Trainer");
        stage.setScene(scene);
        stage.show();
        uiRoot.requestFocus();
    }

    public static void main(String[] args)
    {
        launch();
    }
}