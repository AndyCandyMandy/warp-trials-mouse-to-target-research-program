/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
                gc.fillText("""
                        Welcome to Warp Trials! In this program, you will go through a series of trials,
                        each with their own unique mechanism for completing the trial differently.
                        
                        In order to complete a trial, you must click on the red-highlighted target
                        which will be prompted on your screen.

                        To begin, please select a mechanism by pressing [0-4].
                        
                        
                        
                        [0] Standard Trial (No Mechanism)
                        [1] Grid Warping Trial 
                        [2] User-Defined Warping Trial 
                        [3] System-Defined Warping Trial 
                        [4] Flick Warping Trial
                        """,
                        myCanvas.getWidth() / 2, 50);
            }
            case TRIAL_SELECT -> {
                // clear canvas
                gc.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
                gc.fillText("Please select a trial type [1-3].", myCanvas.getWidth() / 2, 50);
                gc.fillText("1 for Random, 2 for clusters, 3 for real UI", myCanvas.getWidth() / 2, 80);
            }
            case PRE_TRIAL -> {
                // clear canvas
                gc.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
                if (model.getCurrentMechanism() == AppModel.Mechanism.NO_MECH) {
                    gc.fillText("""
                        Mechanism 0 - Standard Trial:
                        
                        Complete a trial by clicking on the red highlighted target.
                        The system will alert you once the trial is finished.
                        
                        Press ENTER to start.""",
                        myCanvas.getWidth() / 2, 50);
                }
                if (model.getCurrentMechanism() == AppModel.Mechanism.GRID) {
                    gc.fillText("""
                        Mechanism 1 - Grid Warping:
                        
                        In this trial, you can bring up a grid on the screen by pressing the 'Shift' key and 'CTRL" key. 
                        Warping is controlled through utilizing the WASD keys while holding "Shift" to navigate a pre-determined grid. 
                        The system will alert you once the trial is finished.
                        
                        Press ENTER to start.""",
                        myCanvas.getWidth() / 2, 50);
                }
                if (model.getCurrentMechanism() == AppModel.Mechanism.USR_KEY) {
                    gc.fillText("""
                        Mechanism 2 - User-Defined Warping:
                        
                        In this trial, you can mark any 4 locations on the canvas of your choose to instantly warp your mouse cursor to.
                        The spots can be toggled ON or OFF by pressing the 'W' key or SHIFT + CONTROL keys.
                        The system will alert you once the trial is finished.
                        
                        Press ENTER to start.""",
                        myCanvas.getWidth() / 2, 50);
                }
                if (model.getCurrentMechanism() == AppModel.Mechanism.SYS_DEF) {
                    gc.fillText("""
                        Mechanism 3 - System-Defined Warping:
                        
                        Click 20 areas on the screen of your choosing (you can even repeat the same targets). The system will then
                        determine your 4 most clicked locations based on the areas of your 20 clicks.
                        Once done, you will be presented with highlighted targets to click.
                        The system will alert you once the trial is finished.
                        
                        Press ENTER to start.""",
                        myCanvas.getWidth() / 2, 50);
                }
                if (model.getCurrentMechanism() == AppModel.Mechanism.FLICK) {
                    gc.fillText("""
                        Mechanism 4 - Flick Warping:
                        
                        In this trial, you can mark any 4 locations on the canvas of your choose to instantly warp your mouse cursor to.
                        The spots can be toggled ON or OFF by pressing the 'W' key or SHIFT + CONTROL keys.
                        
                        In order to warp, hold the CONTROL key (COMMAND key on Mac) and move your cursor in the direction of the warp location.
                        The system will alert you once the trial is finished.
                        
                        Press ENTER to start.""",
                        myCanvas.getWidth() / 2, 50);
                }
            }
            case TRIAL -> {

                switch (model.getTrialMode()) {
                    case REAL_UI:
                        // draw the REAL UI and populate the targets
                        gc.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());

                        // draw image
                        gc.drawImage(model.getUIImage(), 0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

                        // draw rectangular targets
                        for (Target t : model.getTargets()) {
                            t.drawTarget(gc);
                        }
                        break;

                    case CLUSTER_TARGETS:
                    case RANDOM_TARGETS:
                        // clear canvas
                        gc.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
                        gc.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 10));
                        // draw targets
                        int targetNumber = 1;
                        for (Target t : model.getTargets()) {
                            t.drawTarget(gc);
                            targetNumber++;
                        }
                        break;
                }
                if (model.isWarpsVisible()) {

                    // draw grid if in "GRID" mechanism state
                    if (model.getCurrentMechanism() == AppModel.Mechanism.GRID) {
                        drawGrid( model.getGridCollumn(), model.getGridRow());
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
     * @param y - The amount of squares on the Y-axis of the grid
     * @param x - The amount of squares on the X-axis of the grid
     */
    public void drawGrid(int y, int x) {
        double xPos = getHeight() / y;
        double yPos = getWidth() / x;
        gc.setFill(Color.rgb(0, 255, 50, 0.5));
        while (xPos < getHeight() || yPos < getWidth()) {
            if (xPos < getHeight()) {
                gc.fillRect(0, xPos, getWidth(), 5);
                xPos += getHeight() / y;
            }
            if (yPos < getWidth()) {
                gc.fillRect(yPos, 0, 5, getHeight());
                yPos += getWidth() / x;
            }
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
        myCanvas.setOnKeyReleased(controller::handleKeyReleased);
        myCanvas.setOnMouseMoved(controller::handleMouseMoved);
    }

}
