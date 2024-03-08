package com.example.cmpt481_term_project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WarpTrail {
    private double endPointX, endPointY, mouseX, mouseY, opacity;

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
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public double getOpacity() {
        return opacity;
    }

    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.strokeLine(this.endPointX, this.endPointY, this.mouseX, this.mouseY);

        opacity = opacity - 0.1;


    }
}
