/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
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
        stage.setTitle("CMPT481 Term Project - Mouse Warping");
        stage.setScene(scene);
        stage.show();
        uiRoot.requestFocus();
    }

    public static void main(String[] args)
    {
        launch();
    }
}