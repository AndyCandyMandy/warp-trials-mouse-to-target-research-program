/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.robot.Robot;

public class AppController {
    AppModel model;

    Canvas canvas;

    /**
     * Handles a key press
     *
     * @param keyEvent - The key event
     */
    public void handleKeyPressed(KeyEvent keyEvent) {
        switch (model.getCurrentMode()) {
            case MECH_SELECT -> {
                if (keyEvent.getCode() == KeyCode.DIGIT1 || keyEvent.getCode() == KeyCode.DIGIT2 ||
                        keyEvent.getCode() == KeyCode.DIGIT3 || keyEvent.getCode() == KeyCode.DIGIT4) {
                    model.setMechanism(keyEvent.getCode());
                    model.nextMode();
                }
            }
            case PRE_TRIAL -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    model.nextMode();
                }
            }
            case TRIAL -> {
                 if (keyEvent.getCode() == KeyCode.W) {
                    model.toggleWarps();
                }
                 // Display hotkey bar and warp location(s)
                 if (keyEvent.isControlDown() && keyEvent.isShiftDown()) {
                     if (!model.getWarps().isEmpty()) {
                         // Show/hide warp location(s)
                         model.toggleWarps();
                         System.out.println(model.isWarpsVisible());
                     }
                 }
                 else if (keyEvent.getCode() == KeyCode.DIGIT1 && !model.getWarps().isEmpty()) {
                     warpMouse(1);
                 }
                 else if (keyEvent.getCode() == KeyCode.DIGIT2 && model.getWarps().size() > 1) {
                     warpMouse(2);
                 }
                 else if (keyEvent.getCode() == KeyCode.DIGIT3 && model.getWarps().size() > 2) {
                     warpMouse(3);
                 }
                 else if (keyEvent.getCode() == KeyCode.DIGIT4 && model.getWarps().size() > 3) {
                     warpMouse(4);
                 }

            }
            case DONE -> model.nextMode();
        }
    }

    public void warpMouse(int locationNumber) {
        // Get the warp location in the list
        double warpX = model.getWarps().get(locationNumber - 1).getX();
        double warpY = model.getWarps().get(locationNumber - 1).getY();

        // Convert canvas coords to screen coords for accurate mouse warping
        Point2D screenCoords = canvas.localToScreen(warpX, warpY);

        // Use Robot to move mouse to location
        try {
            Robot robot = new Robot();
            robot.mouseMove(screenCoords.getX(), screenCoords.getY());
            model.setWarpTrail(warpX, warpY, model.getMouseX(), model.getMouseY());

            // TODO - Bug, double warping causes line to be misaligned
            // Because the mouse has warped/"moved" the mouseX and mouseY need to be updated
            model.setMouseX(warpX);
            model.setMouseY(warpY);
            // Method to start fade timer
            model.startTrailFadeTimer();
        }
        catch (Exception e){
            System.out.println("Could not move your mouse successfully");
        }
    }

    /**
     * Empty constructor
     */
    public AppController() {
    }

    /**
     * Sets the controllers model
     *
     * @param newModel the model
     */
    public void setModel(AppModel newModel) {
        model = newModel;
    }
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }


    /**
     * Handles a mouse press
     *
     * @param event - The mouse event
     */
    public void handlePress(MouseEvent event) {

    }

    /**
     * Handles a mouse button release event
     *
     * @param event - The mouse event
     */
    public void handleReleased(MouseEvent event) {
        switch (model.getCurrentMode()) {
            case TRIAL -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    model.recordClick(event.getX(), event.getY());
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    // The capacity for warp locations is locked 4 areas. This check verifies the current number
                    if (model.getWarps().size() != 4) {
                        model.addWarp(new WarpLocation(event.getX(), event.getY()));
                    }
                    else {
                        System.out.println("You have reached your warp capacity");
                    }
                }
            }
        }
    }

    public void handleMouseMoved(MouseEvent mouseEvent) {
        model.setMouseX(mouseEvent.getX());
        model.setMouseY(mouseEvent.getY());
    }

}
