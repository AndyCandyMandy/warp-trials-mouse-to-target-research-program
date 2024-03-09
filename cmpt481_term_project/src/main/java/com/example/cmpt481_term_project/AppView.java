/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
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
                for (Target t : model.getTargets()) {
                    t.drawTargets(gc, targetNumber);
                    targetNumber++;
                }


                if (model.isWarpsVisible()) {

                    // draw grid if in "GRID" mechanism state
                    if (model.getCurrentMechanism() == AppModel.Mechanism.GRID) {
                        drawGrid(9,15);
                    }
                    // draw warp locations
                    int warpNumber = 1;
                    for (WarpLocation w : model.getWarps()) {
                        w.drawWarpLocations(gc, warpNumber);
                        warpNumber++;
                    }
                }
                // Draw warp trail
                model.getWarpTrail().draw(gc);
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
     * Creates a visible grid based on the inputted parameters and the general size of the canvas
     *
     * @param x - The amount of squares on the X-axis of the grid
     * @param y - The amount of squares on the Y-axis of the grid
     */
    public void drawGrid(int x, int y) {
        double xPos = getHeight()/x;
        double yPos = getWidth()/y;
        gc.setFill(Color.rgb(0, 255, 50, 0.5));
        while (xPos < getHeight() || yPos < getWidth()) {
            if (xPos < getHeight()) {
                gc.fillRect(0, xPos, getWidth(), 5);
                xPos += getHeight()/x;
            }
            if (yPos < getWidth()) {
                gc.fillRect(yPos, 0, 5, getHeight());
                yPos += getWidth()/y;
            }
        }
        xPos = getHeight()/x;
        yPos = getWidth()/y;
        for (int i = 1; i <= y - 1; i++) {
            for (int j = 1; j <= x - 1; j++) {
                model.addGridPoint(new GridPointer(xPos, yPos));
                yPos += getWidth()/y;
            }
            yPos = getWidth()/y;
            xPos += getHeight()/x;
        }

    }

    /**
     * Sets the AppView model
     *
     * @param newModel - The model to set
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
     *
     * @param controller - Controller to set
     */
    public void setController(AppController controller) {
        this.controller = controller;

        // Canvas is set from controller to help with mouse warping
        this.controller.setCanvas(myCanvas);

        myCanvas.setOnMousePressed(controller::handlePress);
        myCanvas.setOnMouseReleased(controller::handleReleased);
        myCanvas.setOnKeyReleased(controller::handleKeyPressed);
        myCanvas.setOnMouseMoved(controller::handleMouseMoved);
    }

}
