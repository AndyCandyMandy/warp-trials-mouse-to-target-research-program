package com.example.cmpt481_term_project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WarpTrail {
    private double endPointX, endPointY, mouseX, mouseY, opacity, thickness;
    private final double defaultThickness = 3.0;
    private final double defaultOpacity = 1.0;
    public WarpTrail(double endPointX, double endPointY, double mouseX, double mouseY, double opacity) {
        this.endPointX = endPointX;
        this.endPointY = endPointY;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.opacity = opacity;
    }

    public void setCoords(double endPointX, double endPointY, double mouseX, double mouseY) {
        this.endPointX = endPointX;
        this.endPointY = endPointY;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.thickness = defaultThickness;
        this.opacity = defaultOpacity;
    }

    public double getOpacity() {
        return opacity;
    }

    public void fadeStep() {
        this.opacity = Math.max(this.opacity - 0.03, 0.00);
        this.thickness = Math.max(this.thickness - 0.09, 0.00);
    }

    public void draw(GraphicsContext gc) {
        double oldStroke = gc.getLineWidth();
        gc.setLineWidth(thickness);
        gc.setStroke(new Color(0,0,0,opacity));
        gc.strokeLine(this.endPointX, this.endPointY, this.mouseX, this.mouseY);
        gc.setLineWidth(oldStroke);
    }

    public void reset() {
        this.thickness = defaultThickness;
        this.opacity = defaultOpacity;
    }
}
