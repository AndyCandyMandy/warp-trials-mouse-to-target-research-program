/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.animation.FadeTransition;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class WarpLocation {
    private double x, y, r;

    /**
     * Creates new warp location
     *
     * @param nx         - x coordinate
     * @param ny         - y coordinate
     */
    public WarpLocation(double nx, double ny) {
        this.x = nx;
        this.y = ny;
        this.r = 25;
    }

    /**
     * Draws the warp location
     *
     * @param gc - GraphicsContext to use to draw
     */
    public void drawWarpLocations(GraphicsContext gc, int warpNumber) {
        gc.setFill(Color.rgb(0, 255, 50, 0.4));
        gc.fillOval(x - r, y - r, r * 2, r * 2);
        gc.setFill(Color.BLACK);
        gc.fillText(String.valueOf(warpNumber), x, y);
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
