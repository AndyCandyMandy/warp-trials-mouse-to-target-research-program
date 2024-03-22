/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.color;

public class RectTarget implements Target{
    // Atttributes: x, y, width and height, and select status
    private double x, y, w, h;
    private boolean selected;


    /**
     * Constructor for a rectangular target
     * @param nx - x value of top left corner
     * @param ny - y value of top left corner
     * @param nw - width of target
     * @param nh - height of target
     */
    public RectTarget(double nx, double ny, double nw, double nh) {
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        selected = false;
    }

    /**
     * Draw the rectangular target
     * @param gc - the Graphics context to draw to
     */
    @Override
    public void drawTarget(GraphicsContext gc) {
        double s = gc.getLineWidth();
        if (selected) {
            gc.setFill(color(1, 0, 1, 0.4));
        } else {
            gc.setFill(color(1, 1, 1, 0));
        }

        gc.fillRect(x, y, w, h);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(s);
    }

    /**
     * Returns the x value of the top left corner
     * @return - x value of top left corner
     */
    @Override
    public double getX() {
        return x;
    }

    /**
     * Returns the y value of teh top left corner
     * @return - y value of top left corner
     */
    @Override
    public double getY() {
        return y;
    }

    /**
     * Retruns bool value for if it is selected
     * @return - true if selected, false otherwise
     */
    @Override
    public boolean isSelected() {
        return false;
    }

    /**
     * Checks if a point is in the target
     * @param cx - x value to check
     * @param cy - y value to check
     * @return - returns true if point is contained, false otherwise
     */
    @Override
    public boolean contains(double cx, double cy) {
        return cx >= x && cx <= x + w && cy >= y && cy <= y + h;
    }

    /**
     * Selects the target
     */
    @Override
    public void select() {
        this.selected = true;
    }

    /**
     * Deselects the target
     */
    @Override
    public void deselect() {
        this.selected = false;
    }

    /**
     * Returns string version of the target - for exporting target details
     * @return - String value of x, y, width and height
     */
    @Override
    public String toString(){
        return this.x + " " + this.y + " " + this.w + " " + this.h;
    }
}
