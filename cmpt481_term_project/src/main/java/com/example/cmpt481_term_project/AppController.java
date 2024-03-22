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

    /* Attributes for target creation
    double x, y, w, h; */

    /**
     * Handles a key press
     *
     * @param keyEvent - The key event
     */
    public void handleKeyPressed(KeyEvent keyEvent) {
        switch (model.getCurrentMode()) {
            case MECH_SELECT -> {
                if (keyEvent.getCode() == KeyCode.DIGIT0 || keyEvent.getCode() == KeyCode.DIGIT1 ||
                        keyEvent.getCode() == KeyCode.DIGIT2 ||
                        keyEvent.getCode() == KeyCode.DIGIT3 || keyEvent.getCode() == KeyCode.DIGIT4) {
                    //Pre-places warp locations on grid if KeyCode is 1
                    if (keyEvent.getCode() == KeyCode.DIGIT1) {
                        for (GridPointer point : model.getGridList()) {
                            model.addWarp(new WarpLocation(point.getX(), point.getY()));
                        }
                    }

                    model.setMechanism(keyEvent.getCode());
                    model.nextMode();
                }
            }
            case TRIAL_SELECT -> {
                if (keyEvent.getCode() == KeyCode.DIGIT1 || keyEvent.getCode() == KeyCode.DIGIT2 ||
                        keyEvent.getCode() == KeyCode.DIGIT3) {
                    model.setTrialMode(keyEvent.getCode());
                    model.nextMode();
                }
            }
            case PRE_TRIAL -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    model.nextMode();
                }
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    System.out.println("Escape pressed");
                    model.returnToMechanismSelect();
                }
            }
            case TRIAL -> {
                /*
                 code for outputting all target information - USED FOR TARGET CREATION
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    for (Target t : model.getTargets()) {
                        // get string of each created target, and output to console
                        System.out.println(t.toString());
                    }
                } */
                if (keyEvent.getCode() == KeyCode.W && model.getCurrentMechanism() != AppModel.Mechanism.GRID) {
                    model.toggleWarps();
                }
                switch (model.getCurrentMechanism()) {
                    case GRID: {
                        if (!model.getWarps().isEmpty() && keyEvent.isShiftDown()) {
                            if (keyEvent.isControlDown()) {
                                model.toggleWarps();
                            } else if (keyEvent.getCode() == KeyCode.W) {
                                warpMouse(model.warpUpGrid(model.getMouseX(), model.getMouseY() - 5));
                            } else if (keyEvent.getCode() == KeyCode.S) {
                                warpMouse(model.warpDownGrid(model.getMouseX(), model.getMouseY() + 5));
                            } else if (keyEvent.getCode() == KeyCode.A) {
                                warpMouse(model.warpLeftGrid(model.getMouseX() - 5, model.getMouseY()));
                            } else if (keyEvent.getCode() == KeyCode.D) {
                                warpMouse(model.warpRightGrid(model.getMouseX() + 5, model.getMouseY()));
                            }
                        }

                        break;
                    }
                    case USR_KEY: {
                        // Display hotkey bar and warp location(s)
                        if (keyEvent.isControlDown() && keyEvent.isShiftDown()) {
                            if (!model.getWarps().isEmpty()) {
                                // Show/hide warp location(s)
                                model.toggleWarps();
                            }
                        } else {
                            warp(keyEvent.getCode());
                        }
                        break;
                    }
                    case SYS_DEF: {
                        if (model.getWarps().size() != 4 && !model.sysDefTargetSelection) {
                            for (Point2D warpLocation : model.sysDefWarpLocations) {
                                model.addWarp(new WarpLocation(warpLocation.getX(), warpLocation.getY()));
                            }
                        }
                        // Display hotkey bar and warp location(s)
                        if (keyEvent.isControlDown() && keyEvent.isShiftDown()) {
                            if (!model.getWarps().isEmpty()) {
                                // Show/hide warp location(s)
                                model.toggleWarps();
                                System.out.println(model.isWarpsVisible());
                            }
                        } else {
                            warp(keyEvent.getCode());
                        }
                        break;
                    }
                    case FLICK: {
                        if (keyEvent.getCode() == KeyCode.CONTROL || keyEvent.getCode() == KeyCode.COMMAND) {
                            model.setFlickTracking(true);
                            model.saveFlickStartCoords();
                        }
                        break;
                    }
                }
            }
            case DONE -> model.nextMode();
        }
    }

    private void warp(KeyCode k) {
        if (k == KeyCode.DIGIT1 && !model.getWarps().isEmpty()) {
            warpMouse(1);
        } else if (k == KeyCode.DIGIT2 && model.getWarps().size() > 1) {
            warpMouse(2);
        } else if (k == KeyCode.DIGIT3 && model.getWarps().size() > 2) {
            warpMouse(3);
        } else if (k == KeyCode.DIGIT4 && model.getWarps().size() > 3) {
            warpMouse(4);
        }
    }


    public void warpMouse(int locationNumber) {
        // Patch a tiny bug. Will delete later
        if (locationNumber == 0) {
            return;
        }

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
        } catch (Exception e) {
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
        /* Adding code here for creating the UI targets - FOR TARGET CREATION
        x = event.getX();
        y = event.getY(); */
    }

    /**
     * Handles a mouse button release event
     *
     * @param event - The mouse event
     */
    public void handleReleased(MouseEvent event) {
        switch (model.getCurrentMode()) {
            case TRIAL -> {
                // Record clicks and errors
                if (event.getButton() == MouseButton.PRIMARY) {
                    model.recordClick(event.getX(), event.getY());
                    // Check whether user made error
                    if (!model.hitTarget(event.getX(), event.getY())) {
                        model.addToErrorCount();
                    }
                    // Check whether user made correct selection
                    else {
                        // Print results to console
                        System.out.println("Time to select (ms):      " + model.getSelectionTime() + "\n" +
                                "Number of click errors:   " + model.getErrorCount() + "\n" +
                                "Number of warps:          " + model.getWarpCount() + "\n" +
                                "Current Mechanism:        " + model.getCurrentMechanism() + "\n");

                        // Reset the timer for the next selection
                        model.getElapsedTime();
                        // Reset number of errors
                        model.resetErrorCount();
                    }
                } else if (event.getButton() == MouseButton.SECONDARY && model.getCurrentMechanism() != AppModel.Mechanism.SYS_DEF) {
                    // The capacity for warp locations is locked 4 areas. This check verifies the current number
                    if (model.getWarps().size() != 4) {


                        if (model.getCurrentMechanism() == AppModel.Mechanism.SYS_DEF) {
                            model.addWarp(new WarpLocation(event.getX(), event.getY()));
                        } else if (model.getCurrentMechanism() == AppModel.Mechanism.USR_KEY) {
                            model.addWarp(new WarpLocation(event.getX(), event.getY()));
                        } else if (model.getCurrentMechanism() == AppModel.Mechanism.FLICK) {
                            model.addWarp(new WarpLocation(event.getX(), event.getY()));
                        }

                    } else {
                        System.out.println("You have reached your warp capacity");
                    }
                }
                /* More for creating targets - FOR TARGET CREATION
                w = event.getX() - x;
                h = event.getY() - y;
                RectTarget t = new RectTarget(x, y, w, h);
                t.select();
                model.addTarget(t); */
            }
        }
    }

    public void handleMouseMoved(MouseEvent mouseEvent) {
        model.setMouseX(mouseEvent.getX());
        model.setMouseY(mouseEvent.getY());
        switch (model.getCurrentMode()) {
            case TRIAL -> {
                switch (model.getCurrentMechanism()) {
                    case FLICK -> {
                        // check for minimum required distance from flick start position
                        if (model.trackingFlick() && model.reachedMinFlickDistance()) {
                            // Draw a line, then determine the best target
                            double x2 = model.getFlickX();
                            double y2 = model.getFlickY();
                            double x1 = model.getMouseX();
                            double y1 = model.getMouseY();
                            double x3, y3;
                            double len = Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0));
                            // Increase line length by 100 pixels
                            x3 = x2 + (x2 - x1) / len * 1000;
                            y3 = y2 + (y2 - y1) / len * 1000;
                            // get the warp target closest to the new line
                            int warp = -1;
                            warp = model.getClosestFlickTarget(x2, y2, x3, y3);
                            if (warp != -1) {
                                warpMouse(warp);
                            }
                            model.setFlickTracking(false);
                        }
                    }
                }
            }
        }

    }

    public void handleKeyReleased(KeyEvent keyEvent) {
        switch (model.getCurrentMode()) {
            case TRIAL -> {
                switch (model.getCurrentMechanism()) {
                    case FLICK -> {
                        if (keyEvent.getCode() == KeyCode.CONTROL) {
                            model.setFlickTracking(false);
                        }
                    }
                }
            }
        }
    }
}

