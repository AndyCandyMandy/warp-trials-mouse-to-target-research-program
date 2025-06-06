/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.warp_trial_program;

import javafx.scene.canvas.GraphicsContext;

public class GridPointer {

    int snapRadius = 75;
    double x, y;
    public boolean inRadius = false;

    /**
     * Creates clickable portion on the grid
     *
     * @param x         - x coordinate
     * @param y         - y coordinate
     */
    public GridPointer(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void mouseInRadius(double mouseX, double mouseY) {
        if (mouseX > this.x - (this.snapRadius / 2) && mouseX < this.x + (this.snapRadius / 2) &&
                mouseY > this.y - (this.snapRadius / 2) && mouseY < this.y + (this.snapRadius / 2)) {
            this.inRadius = true;
        }
        else {
            this.inRadius = false;
        }
    }

    public boolean getInRadius() {
        return this.inRadius;
    }

    public double getRadius() {
        return this.snapRadius;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

}
