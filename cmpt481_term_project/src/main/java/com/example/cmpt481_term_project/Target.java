/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Target {
    private double x, y, r;
    private boolean selected;

    /**
     * Creates new target
     *
     * @param nx - x coordinate
     * @param ny - y coordinate
     */
    public Target(double nx, double ny, double ry) {
        x = nx;
        y = ny;
        r = ry;
        selected = false;
    }

    /**
     * Draws the current target
     *
     * @param gc        - GraphicsContext to use to draw
     * @param targetNum - Number drawn inside target
     */
    public void drawTargets(GraphicsContext gc, int targetNum) {
        if (selected) {
            gc.setFill(Color.TOMATO);
        } else {
            gc.setFill(Color.LIGHTBLUE);
        }
        gc.setStroke(Color.BLACK);
        gc.fillOval(x - r, y - r, r * 2, r * 2);
        gc.strokeOval(x - r, y - r, r * 2, r * 2);
        gc.setFill(Color.BLACK);
        gc.fillText(String.valueOf(targetNum), x, y);
    }

    /**
     * Gets the target radius
     *
     * @return radius
     */
    public double getRadius() {
        return this.r;
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

    /**
     * Returns the selection status of the target
     *
     * @return teh y coordinate
     */
    public boolean isSelected() {
        return this.selected;
    }


    /**
     * Returns a bool of if a point is within the target
     *
     * @param cx - the point x coordinate
     * @param cy - the point y coordinate
     * @return - true if it is contained, false otherwise
     */
    public boolean contains(double cx, double cy) {
        return dist(cx, cy, x, y) <= r;
    }

    /**
     * Selects the current target
     */
    public void select() {
        this.selected = true;
    }

    /**
     * Deselects the current target
     */
    public void deselect() {
        this.selected = false;
    }

    /**
     * Returns the distance of one point to another
     *
     * @param x1 x coordinate 1
     * @param y1 y coordinate 1
     * @param x2 x coordinate 2
     * @param y2 y coordinate 2
     * @return The distance between the two points
     */
    private double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

}
