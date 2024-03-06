/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class InteractionModel
{
    private List<IModelListener> subscribers;
    private ArrayList<Blob> selectedBlobs;
    private ArrayList<Point2D> lassoPoints;
    private ArrayList<Blob> blobClipboard;
    private int currentTargetIndex;
    private ArrayList<TargetCommand> undoCommandStack;
    private ArrayList<TargetCommand> redoCommandStack;
    private ArrayList<AppModeListener> modeSubs;

    public boolean getTrainingMode() {
        return true;
    }

    public enum AppMode {EDITOR, TRAINING, REPORT}
    private AppMode currentMode;
    XYChart.Series dataPoints;

    /**
     * Creates a new interaction model
     */
    public InteractionModel()
    {
        undoCommandStack = new ArrayList<>();
        redoCommandStack = new ArrayList<>();
        subscribers = new ArrayList<>();
        selectedBlobs = new ArrayList<>();
        lassoPoints = new ArrayList<>();
        blobClipboard = new ArrayList<>();
        modeSubs = new ArrayList<>();
        currentMode = AppMode.EDITOR;
        currentTargetIndex = 0;
        dataPoints = new XYChart.Series();
    }

    /**
     * Notifies mode listeners
     */
    public void notifyModeSubscribers()
    {
        for (AppModeListener sub : modeSubs)
        {
            sub.AppModeChanged();
        }
    }

    /**
     * Adds a new Trial datapoint to the chart points
     * @param x - the x value
     * @param y - the y value
     */
    public void addDataPoint(double x, double y)
    {
        dataPoints.getData().add(new XYChart.Data(x, y));
    }

    /**
     * Returns the series of data points
     * @return - The chart data points
     */
    public XYChart.Series getDataSeries()
    {
        return dataPoints;
    }

    /**
     * Adds a new app mode subscriber
     * @param sub - the new subscriber
     */
    public void addModeSubscriber(AppModeListener sub)
    {
        this.modeSubs.add(sub);
    }

    /**
     * Adds a new command to the undo stack
     * @param newCommand - The command to add
     */
    public void addCommand(TargetCommand newCommand)
    {
        redoCommandStack.clear();
        undoCommandStack.add(newCommand);
    }

    /**
     * Undoes the command at the top of the command stack, moving it to the redo command stack
     */
    public void undo()
    {
        unselect(); // clear selection to avoid weird selection undo errors
        if (undoCommandStack.size() <= 0)
        {
            System.out.println("No command to undo");
        } else
        {
            TargetCommand commandToUndo = undoCommandStack.get(undoCommandStack.size() - 1);
            commandToUndo.undoCommand();
            redoCommandStack.add(commandToUndo);
            undoCommandStack.remove(commandToUndo);
        }
    }

    /**
     * Redoes the command at the top of the redo command stack, moves that command to the command undo stack
     */
    public void redo()
    {
        unselect(); // clear selection to avoid weird selection undo errors
        if (redoCommandStack.size() <= 0)
        {
            System.out.println("No command to redo");
        } else
        {
            TargetCommand commandToRedo = redoCommandStack.get(redoCommandStack.size() - 1);
            commandToRedo.doCommand();
            undoCommandStack.add(commandToRedo);
            redoCommandStack.remove(commandToRedo);
        }
    }

    /**
     * Adds a subscriber to listen to IModel changes
     * @param sub - The new subscriber
     */
    public void addSubscriber(IModelListener sub)
    {
        subscribers.add(sub);
    }

    /**
     * Notifies all subscribers
     */
    private void notifySubscribers()
    {
        subscribers.forEach(IModelListener::iModelChanged);
    }

    /**
     * Adds a blob to the current selection
     * @param b - The blob to add
     */
    public void addSelectedBlob(Blob b)
    {
        this.selectedBlobs.add(b);
        notifySubscribers();
    }

    /**
     * Removes a blob from selection
     * @param b - The blob to remove
     */
    public void removeSelectedBlob(Blob b)
    {
        this.selectedBlobs.remove(b);
        notifySubscribers();
    }

    /**
     * Unselects all blobs
     */
    public void unselect()
    {
        selectedBlobs = new ArrayList<>();
        notifySubscribers();
    }

    /**
     * Returns the selected blobs
     * @return - The selected blobs
     */
    public ArrayList<Blob> getSelected()
    {
        return selectedBlobs;
    }

    /**
     * Clears the lasso selection points
     */
    public void clearLassoPoints()
    {
        this.lassoPoints.clear();
        notifySubscribers();
    }

    /**
     * Adds a new lasso point to the list of lasso selection points
     * @param newPoint - THe point to add
     */
    public void addLassoPoint(Point2D newPoint)
    {
        this.lassoPoints.add(newPoint);
        notifySubscribers();
    }

    /**
     * Returns all lasso points
     * @return - The list of all lasso points
     */
    public ArrayList<Point2D> getLassoPoints()
    {
        return this.lassoPoints;
    }

    /**
     * Sets the list of selected blobs
     * @param toSelect - The blobs to select
     */
    public void setSelectedBlobs(ArrayList<Blob> toSelect)
    {
        this.selectedBlobs = toSelect;
        notifySubscribers();
    }

    /**
     * Performs a deep copy of the selected blobs, copying them to the selection clipboard
     */
    public void copySelected()
    {
        // deep clone of selected blobs
        this.blobClipboard = new ArrayList<>();
        for (Blob blob : selectedBlobs)
        {
            Blob newBlob = new Blob(blob.getX(), blob.getY());
            newBlob.setRadius(blob.getRadius());
            blobClipboard.add(newBlob);
        }
    }

    /**
     * Deep copies selected blobs to clipboard
     */
    public void cutSelected()
    {
        copySelected();
    }

    /**
     * Deep copies blobs from the clipboard, returning it
     * @return - The deep copy of the clipboard to be added to the model
     */
    public ArrayList<Blob> pasteFromClipboard()
    {
        this.selectedBlobs = new ArrayList<>();
        for (Blob blob : blobClipboard)
        {
            Blob newBlob = new Blob(blob.getX(), blob.getY());
            newBlob.setRadius(blob.getRadius());
            selectedBlobs.add(newBlob);
        }
        return selectedBlobs;
    }

    /**
     * Enables training mode
     */
    public void enableTrainingMode()
    {
        currentMode = AppMode.TRAINING;
        currentTargetIndex = 0;
        notifyModeSubscribers();
        notifySubscribers();
    }

    /**
     * Enables Editor mode
     */
    public void enableEditorMode()
    {
        currentMode = AppMode.EDITOR;
        currentTargetIndex = 0;
        notifyModeSubscribers();
        notifySubscribers();
    }

    /**
     * Enable report mode
     */
    public void enableReportMode()
    {
        currentMode = AppMode.REPORT;
        // System.out.println("Entered Report Mode");
        notifyModeSubscribers();
    }

    /**
     * Returns the current app mode
     * @return - The current app mode
     */
    public AppMode getAppMode()
    {
        return currentMode;
    }

    /**
     * Gets the current targets index
     * @return - THe index of the current target
     */
    public int getCurrentTargetIndex()
    {
        return currentTargetIndex;
    }

    /**
     * Increments the current targets index
     */
    public void incrementTargetIndex()
    {
        this.currentTargetIndex++;
        notifySubscribers();
    }


}

