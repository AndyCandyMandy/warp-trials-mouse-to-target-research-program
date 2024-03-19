package com.example.cmpt481_term_project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.color;

public class RectTarget implements Target{
    private double x, y, w, h;
    private boolean selected;



    public RectTarget(double nx, double ny, double nw, double nh) {
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        selected = false;
    }
    @Override
    public void drawTarget(GraphicsContext gc) {
        double s = gc.getLineWidth();
        if (selected) {
            gc.setFill(color(1, 1, 0, 0.4));
        } else {
            gc.setFill(color(0, 0, 0, 0));
        }

        gc.fillRect(x, y, w, h);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(s);
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public boolean contains(double cx, double cy) {
        return false;
    }

    @Override
    public void select() {
        this.selected = true;
    }

    @Override
    public void deselect() {
        this.selected = false;
    }
}
