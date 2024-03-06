package com.example.cmpt481_term_project;

import javafx.geometry.Point2D;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BlobController {
    BlobModel model;
    InteractionModel iModel;
    double prevX, prevY;
    double dX, dY;

    enum State {READY, PREPARE_CREATE, DRAGGING, RESIZING, DRAG_SELECT, TRAINING_MODE}

    State currentState = State.READY;

    public void handleDiagramKeyPress(KeyEvent keyEvent) {
        switch (currentState) {
            case READY -> {
                if (iModel.getSelected().size() != 0 && keyEvent.getCode() == KeyCode.DELETE) {
                    // delete the selected node and all associated links
                    model.deleteSelectedBlobs(iModel.getSelected());
                    iModel.unselect();
                } else if (iModel.getSelected().size() != 0 && keyEvent.getCode() == KeyCode.C && keyEvent.isControlDown()) {
                    iModel.copySelected();
                } else if (iModel.getSelected().size() != 0 && keyEvent.getCode() == KeyCode.X && keyEvent.isControlDown()) {
                    iModel.cutSelected();
                    model.deleteSelectedBlobs(iModel.getSelected());
                    iModel.unselect();
                } else if (keyEvent.getCode() == KeyCode.V && keyEvent.isControlDown()) {
                    ArrayList<Blob> blobsToAdd = iModel.pasteFromClipboard();
                    model.copyBlobsToModel(blobsToAdd);
                } else if (keyEvent.getCode() == KeyCode.T && keyEvent.isControlDown()) {
                    if (model.getBlobs().size() > 0) {
                        System.out.println("Put into training mode");
                        currentState = State.TRAINING_MODE;
                        iModel.setTrainingFlag(true);
                    }
                }
            }
        }
    }


    public BlobController() {

    }

    public void setModel(BlobModel newModel) {
        model = newModel;
    }

    public void setIModel(InteractionModel newIModel) {
        iModel = newIModel;
    }

    public void handlePress(MouseEvent event) {
        switch (currentState) {
            case READY -> {
                if (!event.isShiftDown() && model.hitBlob(event.getX(), event.getY())) {
                    Blob b = model.whichHit(event.getX(), event.getY());
                    if (event.isControlDown()) {
                        if (iModel.getSelected().contains(b)) {
                            iModel.removeSelectedBlob(b);
                        } else {
                            iModel.addSelectedBlob(b);
                        }
                    } else {
                        iModel.unselect();
                        iModel.addSelectedBlob(b);
                    }
                    prevX = event.getX();
                    prevY = event.getY();
                    currentState = State.DRAGGING;
                } else if (event.isShiftDown() && model.hitBlob(event.getX(), event.getY())) {
                    Blob b = model.whichHit(event.getX(), event.getY());
                    if (event.isControlDown()) {
                        if (iModel.getSelected().contains(b)) {
                            iModel.removeSelectedBlob(b);
                        } else {
                            iModel.addSelectedBlob(b);
                        }
                    } else {
                        iModel.unselect();
                        iModel.addSelectedBlob(b);
                    }
                    prevX = event.getX();
                    prevY = event.getY(); // TODO: probably dont need Y for resize
                    currentState = State.RESIZING;
                } else if (event.isShiftDown()) {
                    currentState = State.PREPARE_CREATE;
                } else if (!model.hitBlob(event.getX(), event.getY())) {
//                    iModel.unselect();
                    // enter lasso select mode
                    currentState = State.DRAG_SELECT;
                }
            }
            case TRAINING_MODE -> {
                if (model.hitBlob(event.getX(), event.getY()))
                {
                    Blob hitBlob = model.whichHit(event.getX(), event.getY());
                    if (hitBlob == model.getBlobs().get(iModel.currentTargetIndex))
                    {
                        // increment
                        int nextIndex = iModel.currentTargetIndex + 1;
                        iModel.setCurrentTargetIndex(nextIndex);
                        System.out.println(iModel.getCurrentTargetIndex());
                    }
                    if (iModel.getCurrentTargetIndex() == model.getBlobs().size()) // TODO: fix
                    {
                        System.out.println("Here");
                        currentState = State.READY;
                        iModel.setTrainingFlag(false);
                        iModel.setCurrentTargetIndex(0);
                    }
                }
            }
        }
    }

    public void handleDragged(MouseEvent event) {
        switch (currentState) {
            case PREPARE_CREATE -> {
                currentState = State.READY;
            }
            case DRAGGING -> {
                dX = event.getX() - prevX;
                dY = event.getY() - prevY;
                prevX = event.getX();
                prevY = event.getY();
                model.moveBlob(iModel.getSelected(), dX, dY);
            }
            case RESIZING -> {
                dX = event.getX() - prevX;
                dY = event.getY() - prevY;
                prevX = event.getX();
                prevY = event.getY();
                model.resizeBlob(iModel.getSelected(), dX);
            }
            case DRAG_SELECT -> {
                Point2D newPoint = new Point2D(event.getX(), event.getY());
                iModel.addLassoPoint(newPoint);
            }
        }
    }

    public void handleReleased(PixelReader reader, MouseEvent event) {
        switch (currentState) {
            case PREPARE_CREATE -> {
                model.addBlob(event.getX(), event.getY());
                currentState = State.READY;
            }
            case DRAGGING, RESIZING -> {
                currentState = State.READY;
            }
            case DRAG_SELECT -> {
                // actually perform the selection
                ArrayList<Blob> lassoSelected = new ArrayList<>();
                if (iModel.getLassoPoints().size() > 0) {
                    for (Blob blob : model.getBlobs()) {
                        // check if blobs pixel is RED in the pixel reader
                        if (reader.getColor((int) blob.x, (int) blob.y).equals(Color.RED)) {
                            lassoSelected.add(blob);
                        }

                    }
                }

                if (event.isControlDown()) {
                    ArrayList<Blob> toggledSelection = toggleSelected(iModel.getSelected(), lassoSelected);
                    iModel.setSelectedBlobs(toggledSelection);
                } else {
                    iModel.setSelectedBlobs(lassoSelected);
                }
                iModel.clearLassoPoints();
                currentState = State.READY;
            }

        }
    }

    private ArrayList<Blob> toggleSelected(ArrayList<Blob> selected, ArrayList<Blob> lassoSelected) {
        ArrayList<Blob> newSelection = new ArrayList<>(lassoSelected);
        for (Blob selectedBlob : selected) {
            if (lassoSelected.contains(selectedBlob)) {
                newSelection.remove(selectedBlob);
            } else {
                newSelection.add(selectedBlob);
            }
        }
        return newSelection;
    }
}
