/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

import javafx.beans.Observable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class EditorView extends StackPane implements BlobModelListener, IModelListener
{
    GraphicsContext gc;
    Canvas myCanvas;
    BlobModel model;
    InteractionModel iModel;
    BlobController controller;
    PixelReader reader;
    double viewWidth;
    double viewHeight;

    /**
     * Create new editor view
     * @param w - the view width
     * @param h - the view height
     */
    public EditorView(double w, double h)
    {
        this.viewWidth = w;
        this.viewHeight = h;
        myCanvas = new Canvas(w, h);
        gc = myCanvas.getGraphicsContext2D();
        this.getChildren().add(myCanvas);
    }



    /**
     * Sets up a duplicate canvas offscreen for drawing the lasso region in red
     */
    private void setupOffScreen()
    {
        Canvas offscreenCanvas = new Canvas(viewWidth, viewHeight);
        GraphicsContext offscreenGC = offscreenCanvas.getGraphicsContext2D();
        offscreenGC.clearRect(0, 0, viewWidth, viewHeight);

        offscreenGC.setFill(Color.RED);

        offscreenGC.beginPath();
        offscreenGC.moveTo(iModel.getLassoPoints().get(0).getX(), iModel.getLassoPoints().get(0).getY());
        for (Point2D point : iModel.getLassoPoints())
        {
            offscreenGC.lineTo(point.getX(), point.getY());
        }
        offscreenGC.closePath();

        offscreenGC.fill();
        WritableImage buffer = offscreenCanvas.snapshot(null, null); // converts the canvas to a snapshot
        reader = buffer.getPixelReader(); // gets pixel information from the snapshot
    }

    /**
     * Draws the items on the canvas
     */
    private void draw()
    {
        // Editor mode
        if (iModel.getAppMode() == InteractionModel.AppMode.EDITOR)
        {
            // clear canvas
            gc.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
            // draw each blob and blob number
            int blobNumber = 1;
            for (Blob b : model.getBlobs())
            {
                if (iModel.getSelected().contains(b))
                {
                    gc.setFill(Color.TOMATO);
                } else
                {
                    gc.setFill(Color.LIGHTBLUE);
                }
                gc.fillOval(b.getX() - b.getRadius(), b.getY() - b.getRadius(), b.getRadius() * 2, b.getRadius() * 2);
                gc.setFill(Color.BLACK);
                gc.fillText(String.valueOf(blobNumber), b.getX(), b.getY());
                blobNumber++;
            }
            // draw lasso outline dots
            for (Point2D point : iModel.getLassoPoints())
            {
                gc.setFill(Color.GRAY);
                gc.fillOval(point.getX(), point.getY(), 2, 2);
            }
            // draw selection rectangle
            if (iModel.getLassoPoints().size() > 0)
            {
                gc.setStroke(Color.BLACK);
                int lastIndex = iModel.getLassoPoints().size() - 1;
                double x1 = iModel.getLassoPoints().get(0).getX();
                double y1 = iModel.getLassoPoints().get(0).getY();
                double x2 = iModel.getLassoPoints().get(lastIndex).getX();
                double y2 = iModel.getLassoPoints().get(lastIndex).getY();
                if (x2 < x1 && y2 < y1) // top left
                {
                    gc.strokeRect(x2, y2, x1 - x2, y1 - y2);
                } else if (x2 > x1 && y2 < y1) // top right
                {
                    gc.strokeRect(x1, y2, x2 - x1, y1 - y2);
                } else if (x2 < x1 && y2 > y1)
                {
                    gc.strokeRect(x2, y1, x1 - x2, y2 - y1);
                } else // bottom right
                {
                    gc.strokeRect(x1, y1, x2 - x1, y2 - y1);
                }
            }
        }
        else if (iModel.getAppMode() == InteractionModel.AppMode.TRAINING)
        {
            gc.setFill(Color.LIGHTSKYBLUE);
            gc.fillRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
            gc.setFill(Color.PALEVIOLETRED);
            Blob currBlob = model.getBlobs().get(iModel.getCurrentTargetIndex());
            gc.fillOval(currBlob.getX() - currBlob.getRadius(), currBlob.getY() - currBlob.getRadius(), currBlob.getRadius() * 2, currBlob.getRadius() * 2);
        }

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
     * Sets the view's I model
     * @param newIModel - The I model to set
     */
    public void setIModel(InteractionModel newIModel)
    {
        iModel = newIModel;
    }

    /**
     * Model has changed, need to redraw canvas
     */
    @Override
    public void modelChanged()
    {
        draw();
    }

    /**
     * I Model has changed, need to redraw the canvas
     */
    @Override
    public void iModelChanged()
    {
        draw();
    }

    /**
     * Sets the view's controller
     * @param controller - The controller to set
     */
    public void setController(BlobController controller)
    {
        this.controller = controller;
        myCanvas.setOnMousePressed(controller::handlePress);
        myCanvas.setOnMouseDragged(controller::handleDragged);
        myCanvas.setOnMouseReleased(this::handleReleased);

        this.widthProperty().addListener(this::setCanvasWidth);
        this.heightProperty().addListener(this::setCanvasHeight);
    }

    /**
     * Event handling for a mouse release - used for creating and passing in the lasso selection reader
     * @param mouseEvent - The mouse released event
     */
    private void handleReleased(MouseEvent mouseEvent)
    {
        if (iModel.getLassoPoints().size() > 0)
        {
            // setup offscreen canvas
            setupOffScreen();
        }
        controller.handleReleased(reader, mouseEvent);
    }

    /**
     * Sets the canvas width when window is resized
     */
    public void setCanvasWidth(Observable observable, Number oldVal, Number newVal)
    {
        //System.out.println("Adjusting width");
        viewWidth = newVal.doubleValue();
        myCanvas.setWidth(viewWidth);
    }

    /**
     * Sets the canvas height when window is resized
     */
    public void setCanvasHeight(Observable observable, Number oldVal, Number newVal)
    {
        viewHeight = newVal.doubleValue();
        myCanvas.setHeight(viewHeight);
    }
}
