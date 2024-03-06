package com.example.cmpt481_term_project;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class BlobView extends StackPane implements BlobModelListener, IModelListener {
    GraphicsContext gc;
    Canvas myCanvas;
    BlobModel model;
    InteractionModel iModel;
    BlobController controller;
    PixelReader reader;

    public BlobView() {
        myCanvas = new Canvas(800, 800);
        gc = myCanvas.getGraphicsContext2D();

        this.getChildren().add(myCanvas);

    }

    private void setupOffScreen() {
        Canvas offscreenCanvas = new Canvas(800, 800);
        GraphicsContext offscreenGC = offscreenCanvas.getGraphicsContext2D();
        offscreenGC.clearRect(0, 0, 800, 800);

        offscreenGC.setFill(Color.RED);

        offscreenGC.beginPath();
        offscreenGC.moveTo(iModel.getLassoPoints().get(0).getX(), iModel.getLassoPoints().get(0).getY());
        for (Point2D point : iModel.getLassoPoints()) {
            offscreenGC.lineTo(point.getX(), point.getY());
        }
        offscreenGC.closePath();

        offscreenGC.fill();
        WritableImage buffer = offscreenCanvas.snapshot(null, null); // converts the canvas to a snapshot
        reader = buffer.getPixelReader(); // gets pixel information from the snapshot
    }

    private void draw() {
        if (!iModel.getTrainingMode()) {
            gc.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
            int blobNumber = 1;

            for (Blob b : model.getBlobs()) {
                if (iModel.getSelected().contains(b)) {
                    gc.setFill(Color.TOMATO);
                } else {
                    gc.setFill(Color.BEIGE);
                }
                gc.setFill(Color.BLACK);
                //gc.setFont(new Font());
                blobNumber++;
            }

            for (Point2D point : iModel.getLassoPoints()) {
                gc.setFill(Color.GRAY);
                gc.fillOval(point.getX(), point.getY(), 2, 2);
            }

            if (iModel.getLassoPoints().size() > 0) {
                gc.setStroke(Color.BLACK);
                int lastIndex = iModel.getLassoPoints().size() - 1;
                double x1 = iModel.getLassoPoints().get(0).getX();
                double y1 = iModel.getLassoPoints().get(0).getY();
                double x2 = iModel.getLassoPoints().get(lastIndex).getX();
                double y2 = iModel.getLassoPoints().get(lastIndex).getY();
                if (x2 < x1 && y2 < y1) // top left
                    gc.strokeRect(x2, y2, x1 - x2, y1 - y2);
                else if (x2 > x1 && y2 < y1) // top right
                    gc.strokeRect(x1, y2, x2 - x1, y1 - y2);
                else if (x2 < x1 && y2 > y1)
                    gc.strokeRect(x2, y1, x1 - x2, y2 - y1);
                else // bottom right
                    gc.strokeRect(x1, y1, x2 - x1, y2 - y1);
            }
        } else {
            // In training mode, draw canvas different
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());

            gc.setFill(Color.ORANGE);
            Blob currBlob = model.getBlobs().get(iModel.getCurrentTargetIndex());

        }

    }

    public void setModel(BlobModel newModel) {
        model = newModel;
    }

    public void setIModel(InteractionModel newIModel) {
        iModel = newIModel;
    }

    @Override
    public void modelChanged() {
        draw();

    }

    @Override
    public void iModelChanged() {
        draw();
    }

    public void setController(BlobController controller) {
        this.controller = controller;
        myCanvas.setOnMousePressed(controller::handlePress);
        myCanvas.setOnMouseDragged(controller::handleDragged);
        myCanvas.setOnMouseReleased(this::handleReleased);
    }

    private void handleReleased(MouseEvent mouseEvent) {
        if (iModel.getLassoPoints().size() > 0) {
            // setup offscreen canvas
            setupOffScreen();
        }
        controller.handleReleased(reader, mouseEvent);
    }
}
