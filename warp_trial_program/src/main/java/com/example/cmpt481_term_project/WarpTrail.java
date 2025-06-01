/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class WarpTrail {
    private double endPointX, endPointY, mouseX, mouseY, opacity, thickness;
    private final double defaultThickness = 3.0;
    private final double defaultOpacity = 1.0;
    private final double thicknessFadeStep = 0.09;
    private final double opacityFadeStep = 0.03;

    private boolean drawTrail;

    private Color startTrailColor = new Color(1.0, 0, 1.0, 1.0);
    private Color endTrailColor = new Color(0, 0, 0, 0);

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
        this.drawTrail = false;
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
        this.drawTrail = true;
    }

    public boolean isDrawn(){
        return this.drawTrail;
    }

    public void setDrawn(boolean b){
        this.drawTrail = b;
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
        //this.opacity = Math.max(this.opacity - opacityFadeStep, 0.00);
        this.opacity = this.opacity - opacityFadeStep;
        if (this.opacity < 0) {
            this.opacity = 0;
            this.drawTrail = false;
        }
        //this.thickness = Math.max(this.thickness - thicknessFadeStep, 0.00);
        this.thickness = this.thickness - thicknessFadeStep;
        if (this.thickness < 0) {
            this.thickness = 0;
            this.drawTrail = false;
        }
    }

    /**
     * Method that draws the trail to the canvas using the graphics context supplied
     * @param gc - Graphics context used to draw
     */
    public void draw(GraphicsContext gc) {
        double oldStroke = gc.getLineWidth();
        gc.setLineWidth(thickness);
        //gc.setStroke(new Color(0,0,0,opacity));
        LinearGradient linearGradient = new LinearGradient(this.endPointX, this.endPointY, this.mouseX, this.mouseY, false, CycleMethod.REFLECT, new Stop(0,startTrailColor),new Stop(1,endTrailColor));
        gc.setStroke(linearGradient);
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
