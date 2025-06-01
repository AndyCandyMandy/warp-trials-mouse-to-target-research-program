/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.scene.layout.StackPane;

public class MainUI extends StackPane {
    AppView appView;
    AppModel model;
    AppController controller;

    private int w = 1400;
    private int h = 800;

    /**
     * Sets up the views, model, IModel, and controller as well as linking
     */
    public MainUI() {
        model = new AppModel(w, h);
        controller = new AppController();
        appView = new AppView(w, h);

        controller.setModel(model);
        appView.setModel(model);

        model.addSubscriber(appView);

        appView.setController(controller);

        this.getChildren().addAll(appView);
        appView.setVisible(true);
        appView.modelChanged();

        // MainUI has keyboard focus - pass key events to controller
        this.setOnKeyPressed(controller::handleKeyPressed);
        this.setOnMouseMoved(controller::handleMouseMoved);
    }

}
