/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

import javafx.beans.Observable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.layout.StackPane;


public class ReportView extends StackPane implements IModelListener
{
    BlobModel model;
    ReportController controller;
    InteractionModel iModel;
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    final ScatterChart<Number,Number> sc = new ScatterChart<>(xAxis, yAxis);
    double viewWidth;
    double viewHeight;

    public ReportView( double w, double h)
    {
        this.viewWidth = w;
        this.viewHeight = h;

        xAxis.setLabel("Index of Difficulty [bits]");
        yAxis.setLabel("Movement Time [ms]");
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
        sc.setTitle("Targeting Performance");

        this.getChildren().addAll(sc);
    }

    /**
     * Sets the view's model
     * @param newModel - The model to set
     */
    public void setModel(BlobModel newModel)
    {
        model = newModel;
    }

    /**
     * Sets the view's IModel
     * @param newIModel - The IModel to set
     */
    public void setIModel(InteractionModel newIModel)
    {
        iModel = newIModel;
        sc.getData().add(iModel.getDataSeries());
    }

    /**
     * Sets the view's controller
     * @param controller - The controller to set
     */
    public void setController(ReportController controller)
    {
        this.controller = controller;
        this.widthProperty().addListener(this::setCanvasWidth);
        this.heightProperty().addListener(this::setCanvasHeight);
    }

    /**
     * Update data points when the imodel changes
     */
    @Override
    public void iModelChanged()
    {
        // update data points
        sc.getData().clear();
        sc.getData().add(iModel.getDataSeries());
    }

    /**
     * Sets the canvas width when window is resized
     */
    public void setCanvasWidth(Observable observable, Number oldVal, Number newVal)
    {
        viewWidth = newVal.doubleValue();
        this.setWidth(viewWidth);
    }

    /**
     * Sets the canvas height when window is resized
     */
    public void setCanvasHeight(Observable observable, Number oldVal, Number newVal)
    {
        viewHeight = newVal.doubleValue();
        this.setHeight(viewHeight);
    }
}
