/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.scene.canvas.GraphicsContext;

/**
 * Interface for target types
 */
public interface Target {
    void drawTarget(GraphicsContext gc);
    double getX();
    double getY();
    boolean isSelected();
    boolean contains(double cx, double cy);
    void select();
    void deselect();
}
