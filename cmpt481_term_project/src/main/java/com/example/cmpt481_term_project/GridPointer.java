package com.example.cmpt481_term_project;

public class GridPointer {

    int snapRadius = 75;
    double x, y;
    public boolean inRadius = false;

    /**
     * Creates a radius where the mouse will snap to a certain location
     *
     * @param x         - x coordinate
     * @param y         - y coordinate
     */
    public GridPointer(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setMouseInRadius(double mouseX, double mouseY) {
        this.inRadius = !this.inRadius && mouseX > this.x - (this.snapRadius / 2) && mouseX < this.x + (this.snapRadius / 2) &&
                mouseY > this.y - (this.snapRadius / 2) && mouseY < this.y + (this.snapRadius / 2);
    }

    public boolean getInRadius() {
        return this.inRadius;
    }

    public double getSnapRadius() {
        return this.snapRadius;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

}
