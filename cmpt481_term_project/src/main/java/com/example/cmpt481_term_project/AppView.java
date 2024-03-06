/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class AppView extends StackPane implements AppModelListener {
    GraphicsContext gc;
    Canvas myCanvas;
    AppModel model;
    AppController controller;

    /**
     * Creates the basic app view
     */
    public AppView() {
        myCanvas = new Canvas(1500, 900);
        gc = myCanvas.getGraphicsContext2D();

        this.getChildren().add(myCanvas);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
    }

    /**
     * Draws to the canvas
     */
    private void draw() {
        // Draw the actual model based on the current app mode

        switch (model.getCurrentMode()) {
            case MECH_SELECT -> {
                // clear canvas
                gc.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
                gc.fillText("Please select a mechanism [1-4].", myCanvas.getWidth() / 2, 50);
            }
            case PRE_TRIAL -> {
                // clear canvas
                gc.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
                gc.fillText("Click on the targets, press ENTER to start.", myCanvas.getWidth() / 2, 50);
            }
            case TRIAL -> {
                // clear canvas
                gc.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
                gc.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 10));
                // draw targets
                int targetNumber = 1;
                for (Target t : model.getTargets())
                {
                    if (t.isSelected())
                    {
                        gc.setFill(Color.TOMATO);
                    } else
                    {
                        gc.setFill(Color.LIGHTBLUE);
                    }
                    gc.fillOval(t.getX() - t.getRadius(), t.getY() - t.getRadius(), t.getRadius() * 2, t.getRadius() * 2);
                    gc.strokeOval(t.getX() - t.getRadius(), t.getY() - t.getRadius(), t.getRadius() * 2, t.getRadius() * 2);
                    gc.setFill(Color.BLACK);
                    gc.fillText(String.valueOf(targetNumber), t.getX(), t.getY());
                    targetNumber++;
                }
            }
            case DONE -> {
                // clear canvas
                gc.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
                gc.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
                gc.fillText("Trial Complete, press any key to exit.", myCanvas.getWidth() / 2, 50);
            }

        }
    }

    /**
     * Sets the AppView model
     * @param newModel  - The model to set
     */
    public void setModel(AppModel newModel) {
        model = newModel;
    }

    /**
     * Handles model changes - redraws canvas
     */
    @Override
    public void modelChanged() {
        draw();
    }

    /**
     * Sets the AppView controller
     * @param controller - Controller to set
     */
    public void setController(AppController controller) {
        this.controller = controller;
        myCanvas.setOnMousePressed(controller::handlePress);
        myCanvas.setOnMouseReleased(controller::handleReleased);
        myCanvas.setOnKeyReleased(controller::handleKeyRelease);
    }

}
