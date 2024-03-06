/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WarpLocation {
    private double x, y, r;

    /**
     * Creates new warp location
     *
     * @param nx - x coordinate
     * @param ny - y coordinate
     */
    public WarpLocation(double nx, double ny) {
        x = nx;
        y = ny;
        r = 25;
    }

    /**
     * Draws the warp location
     *
     * @param gc - GraphicsContext to use to draw
     */
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.TOMATO);
        gc.strokeOval(x - r, y - r, r * 2, r * 2);
    }

    /**
     * Gets the x position
     *
     * @return teh x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y position
     *
     * @return teh y coordinate
     */
    public double getY() {
        return y;
    }


}
