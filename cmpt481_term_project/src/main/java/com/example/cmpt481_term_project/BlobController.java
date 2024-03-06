/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

import javafx.geometry.Point2D;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BlobController
{
    BlobModel model;
    InteractionModel iModel;
    double prevX, prevY;
    double firstX, firstY;
    double dX, dY;
    long startTime;
    long finishTime;
    enum State {READY, PREPARE_CREATE, DRAGGING, RESIZING, DRAG_SELECT, TRAINING_MODE}
    State currentState = State.READY;

    /**
     * Handles a key press
     * @param keyEvent - The key event
     */
    public void handleDiagramKeyPress(KeyEvent keyEvent)
    {
        switch (currentState)
        {
            case READY -> {
                if (iModel.getSelected().size() != 0 && keyEvent.getCode() == KeyCode.DELETE)
                {
                    // delete the selected node and all associated links
                    DeleteBlobCommand deleteBlobCommand = new DeleteBlobCommand(model, iModel.getSelected());
                    deleteBlobCommand.doCommand();
                    iModel.addCommand(deleteBlobCommand);
                    iModel.unselect();
                } else if (iModel.getSelected().size() != 0 && keyEvent.getCode() == KeyCode.C && keyEvent.isControlDown())
                {
                    iModel.copySelected();
                } else if (iModel.getSelected().size() != 0 && keyEvent.getCode() == KeyCode.X && keyEvent.isControlDown())
                {
                    iModel.cutSelected();
                    DeleteBlobCommand deleteBlobCommand = new DeleteBlobCommand(model, iModel.getSelected());
                    deleteBlobCommand.doCommand();
                    iModel.addCommand(deleteBlobCommand);
                    iModel.unselect();
                } else if (keyEvent.getCode() == KeyCode.V && keyEvent.isControlDown())
                {
                    ArrayList<Blob> blobsToAdd = iModel.pasteFromClipboard();
                    CreateBlobCommand createBlobCommand = new CreateBlobCommand(model, blobsToAdd);
                    createBlobCommand.doCommand();
                    iModel.addCommand(createBlobCommand);
                } else if (keyEvent.getCode() == KeyCode.T && keyEvent.isControlDown())
                {
                    if (model.getBlobs().size() > 0)
                    {
                        //System.out.println("Entered training mode");
                        currentState = State.TRAINING_MODE;
                        startTime = 0;
                        finishTime = 0;
                        iModel.enableTrainingMode();
                    }
                } else if (keyEvent.getCode() == KeyCode.Z && keyEvent.isControlDown())
                {
                    // undo
                    iModel.undo();
                } else if (keyEvent.getCode() == KeyCode.R && keyEvent.isControlDown())
                {
                    // redo
                    iModel.redo();
                } else if (keyEvent.getCode() == KeyCode.E && keyEvent.isControlDown())
                {
                    currentState = State.READY;
                    iModel.enableEditorMode();
                }
            }
            case TRAINING_MODE -> {
                if (keyEvent.getCode() == KeyCode.E && keyEvent.isControlDown())
                {
                    currentState = State.READY;
                    iModel.enableEditorMode();
                }
            }

        }
    }

    /**
     * Empty constructor
     */
    public BlobController()
    {
    }

    /**
     * Sets the controllers model
     * @param newModel the model
     */
    public void setModel(BlobModel newModel)
    {
        model = newModel;
    }

    /**
     * Sets the controllers I model
     * @param newIModel the new I model
     */
    public void setIModel(InteractionModel newIModel)
    {
        iModel = newIModel;
    }

    /**
     * Handles a mouse press
     * @param event - The mouse event
     */
    public void handlePress(MouseEvent event)
    {
        switch (currentState)
        {
            case READY -> {
                if (model.hitBlob(event.getX(), event.getY()))
                {
                    Blob b = model.whichHit(event.getX(), event.getY());
                    if (event.isControlDown())
                    {
                        if (iModel.getSelected().contains(b))
                        {
                            iModel.removeSelectedBlob(b);
                        } else
                        {
                            iModel.addSelectedBlob(b);
                        }
                    } else
                    {
                        if (!iModel.getSelected().contains(b))
                        {
                            iModel.unselect();
                            iModel.addSelectedBlob(b);
                        }
                    }
                    prevX = event.getX();
                    prevY = event.getY();
                    firstX = event.getX();
                    firstY = event.getY();
                    if (event.isShiftDown())
                    {
                        currentState = State.RESIZING;
                    } else
                    {
                        currentState = State.DRAGGING;
                    }
                } else if (event.isShiftDown())
                {
                    currentState = State.PREPARE_CREATE;
                } else if (!model.hitBlob(event.getX(), event.getY()))
                {
                    iModel.unselect();
                    // enter lasso select mode
                    currentState = State.DRAG_SELECT;
                }
            }
            case TRAINING_MODE -> {
                if (model.hitTarget(iModel.getCurrentTargetIndex(), (int) event.getX(), (int) event.getY()))
                {
                    if (iModel.getCurrentTargetIndex() != 0)
                    {
                        // create Trial Record
                        finishTime = System.currentTimeMillis();
                        long elapsedTime = finishTime - startTime;
                        // ID = log2(2D/W)
                        double distanceDoubled = 2 * calculateDistanceBetweenBlobs(model.getBlobs().get(iModel.getCurrentTargetIndex() - 1), model.getBlobs().get(iModel.getCurrentTargetIndex()));
                        double width = model.getBlobs().get(iModel.getCurrentTargetIndex()).getRadius() * 2;
                        double id = Math.log((distanceDoubled / width)) / Math.log(2);
                        TrialRecord newTrial = new TrialRecord(elapsedTime,id);
                        iModel.addDataPoint(newTrial.indexOfDifficulty, newTrial.timeElapsedMilliseconds);
                    }
                    startTime = System.currentTimeMillis();

                    // increment
                    int nextIndex = iModel.getCurrentTargetIndex() + 1;
                    if (nextIndex != model.getBlobs().size())
                    {
                        iModel.incrementTargetIndex();
                    }
                    if (nextIndex == model.getBlobs().size())
                    {
                        currentState = State.READY;
                        // System.out.println("Training Over");
                        iModel.enableEditorMode();
                        iModel.enableReportMode();
                        startTime = 0;
                        finishTime = 0;
                    }
                }
            }
        }
    }

    /**
     * Calculates distance between two blobs
     * @param b1 - first blob
     * @param b2 - second blob
     * @return - the distance between them
     */
    public double calculateDistanceBetweenBlobs(Blob b1, Blob b2)
    {
        return Math.sqrt((b2.getY() - b1.getY()) * (b2.getY() - b1.getY()) + (b2.getX() - b1.getX()) * (b2.getX() - b1.getX()));
    }

    /**
     * Handles a mouse drag event
     * @param event - The mouse event
     */
    public void handleDragged(MouseEvent event)
    {
        switch (currentState)
        {
            case PREPARE_CREATE -> currentState = State.READY;
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

    /**
     * Handles a mouse button release event
     * @param reader - The offscreen canvas reader for lasso selection
     * @param event  - The mouse event
     */
    public void handleReleased(PixelReader reader, MouseEvent event)
    {
        switch (currentState)
        {
            case PREPARE_CREATE -> {
                Blob newBlob = new Blob(event.getX(), event.getY());
                ArrayList<Blob> newList = new ArrayList<>();
                newList.add(newBlob);
                CreateBlobCommand createBlobCommand = new CreateBlobCommand(model, newList);
                iModel.addCommand(createBlobCommand);
                createBlobCommand.doCommand();
                currentState = State.READY;
            }
            case DRAGGING -> {
                if ((int) ((firstX - prevX) + (firstY - prevY)) != 0)
                {
                    MoveBlobsCommand moveCommand = new MoveBlobsCommand(model, iModel.getSelected(), (int) (firstX - prevX), (int) (firstY - prevY));
                    iModel.addCommand(moveCommand);
                }
                currentState = State.READY;
            }
            case RESIZING -> {
                if ((int) (firstX - prevX) != 0)
                {
                    ResizeBlobCommand resizeBlobCommand = new ResizeBlobCommand(model, iModel.getSelected(), (int) (firstX - prevX));
                    iModel.addCommand(resizeBlobCommand);
                }
                currentState = State.READY;
            }
            case DRAG_SELECT -> {
                // actually perform the selection
                ArrayList<Blob> lassoSelectedBlobs = new ArrayList<>();
                ArrayList<Blob> rectSelectedBlobs = new ArrayList<>();

                if (iModel.getLassoPoints().size() > 0)
                {
                    // select via lasso
                    for (Blob blob : model.getBlobs())
                    {
                        // check if blobs center pixel is RED in the pixel reader
                        if (reader.getColor((int) blob.getX(), (int) blob.getY()).equals(Color.RED))
                        {
                            lassoSelectedBlobs.add(blob);
                        }

                        // get rectangle selection
                        int lastIndex = iModel.getLassoPoints().size() - 1;
                        double x1 = iModel.getLassoPoints().get(0).getX();
                        double y1 = iModel.getLassoPoints().get(0).getY();
                        double x2 = iModel.getLassoPoints().get(lastIndex).getX();
                        double y2 = iModel.getLassoPoints().get(lastIndex).getY();
                        Point2D topLeft;
                        Point2D bottomRight;
                        if (x2 < x1 && y2 < y1) // drawn bottom right to top left
                        {
                            topLeft = new Point2D(x2, y2);
                            bottomRight = new Point2D(x1, y1);
                        } else if (x2 > x1 && y2 < y1) // drawn bottom left to top right
                        {
                            topLeft = new Point2D(x1, y2);
                            bottomRight = new Point2D(x2, y1);
                        } else if (x2 < x1 && y2 > y1) // drawn top right to bottom left
                        {
                            topLeft = new Point2D(x2, y1);
                            bottomRight = new Point2D(x1, y2);
                        } else // drawn top left to bottom right
                        {
                            topLeft = new Point2D(x1, y1);
                            bottomRight = new Point2D(x2, y2);
                        }
                        // make rectangle selection
                        if (topLeft.getX() <= blob.getX() && bottomRight.getX() >= blob.getX())
                        {
                            if (topLeft.getY() <= blob.getY() && bottomRight.getY() >= blob.getY())
                            {
                                // blob center point in within rectangle
                                rectSelectedBlobs.add(blob);
                            }
                        }
                    }
                }
                // choose blob selection with most blobs
                ArrayList<Blob> blobSelectionToUse;
                if (lassoSelectedBlobs.size() > rectSelectedBlobs.size())
                {
                    blobSelectionToUse = lassoSelectedBlobs;
                } else
                {
                    blobSelectionToUse = rectSelectedBlobs;
                }

                // toggle blob selection if control is down
                if (event.isControlDown())
                {
                    ArrayList<Blob> toggledSelection = toggleSelected(iModel.getSelected(), blobSelectionToUse);
                    iModel.setSelectedBlobs(toggledSelection);
                } else
                {
                    // select the blobs
                    iModel.setSelectedBlobs(blobSelectionToUse);
                }
                iModel.clearLassoPoints();
                currentState = State.READY;
            }

        }
    }

    /**
     * Helper method for toggling selected blobs
     * @param selected         - the currently selected
     * @param newSelectedBlobs - the lasso selected blobs
     * @return - Returns the toggle selected arraylist of blobs
     */
    private ArrayList<Blob> toggleSelected(ArrayList<Blob> selected, ArrayList<Blob> newSelectedBlobs)
    {
        ArrayList<Blob> newSelection = new ArrayList<>(newSelectedBlobs);
        for (Blob selectedBlob : selected)
        {
            if (newSelectedBlobs.contains(selectedBlob))
            {
                newSelection.remove(selectedBlob);
            } else
            {
                newSelection.add(selectedBlob);
            }
        }
        return newSelection;
    }
}
