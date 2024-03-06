/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class AppController {
    AppModel model;

    /**
     * Handles a key press
     *
     * @param keyEvent - The key event
     */
    public void handleKeyRelease(KeyEvent keyEvent) {
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
            }
            case DONE -> {
                model.nextMode();
            }
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
                    model.addWarp(new WarpLocation(event.getX(), event.getY()));
                }
            }
        }

    }

}
