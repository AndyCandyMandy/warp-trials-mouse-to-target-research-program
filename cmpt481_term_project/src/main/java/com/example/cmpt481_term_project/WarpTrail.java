/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WarpTrail {
    private double endPointX, endPointY, mouseX, mouseY, opacity, thickness;
    private final double defaultThickness = 3.0;
    private final double defaultOpacity = 1.0;
    private final double thicknessFadeStep = 0.09;
    private final double opacityFadeStep = 0.03;

    /**
     * Constructor for the warp trail
     * @param endPointX - trail end x coord
     * @param endPointY - trail end y coord
     * @param mouseX - trail start x coord
     * @param mouseY - trail start y coord
     */
    public WarpTrail(double endPointX, double endPointY, double mouseX, double mouseY) {
        this.endPointX = endPointX;
        this.endPointY = endPointY;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.opacity = defaultOpacity;
        this.thickness = defaultThickness;
    }

    /**
     * Updates the trail coordinates
     * @param endPointX - trail end x coord
     * @param endPointY - trail end y coord
     * @param mouseX - trail start x coord
     * @param mouseY - trail start y coord
     */
    public void setCoords(double endPointX, double endPointY, double mouseX, double mouseY) {
        this.endPointX = endPointX;
        this.endPointY = endPointY;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.thickness = defaultThickness;
        this.opacity = defaultOpacity;
    }

    /**
     * Method that returns the current trail opacity
     * @return - Returns the opacity
     */
    public double getOpacity() {
        return opacity;
    }

    /**
     * Method that fades the trail by one step
     */
    public void fadeStep() {
        this.opacity = Math.max(this.opacity - opacityFadeStep, 0.00);
        this.thickness = Math.max(this.thickness - thicknessFadeStep, 0.00);
    }

    /**
     * Method that draws the trail to the canvas using the graphics context supplied
     * @param gc - Graphics context used to draw
     */
    public void draw(GraphicsContext gc) {
        double oldStroke = gc.getLineWidth();
        gc.setLineWidth(thickness);
        gc.setStroke(new Color(0,0,0,opacity));
        gc.strokeLine(this.endPointX, this.endPointY, this.mouseX, this.mouseY);
        gc.setLineWidth(oldStroke);
    }

    /**
     * Method that resets opacity and thickness of the trail.
     */
    public void reset() {
        this.thickness = defaultThickness;
        this.opacity = defaultOpacity;
    }
}
