package com.example.cmpt481_term_project;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class InteractionModel {
    List<IModelListener> subscribers;
    ArrayList<Blob> selectedBlobs;
    ArrayList<Point2D> lassoPoints;

    ArrayList<Blob> blobClipboard;

    boolean trainingModeFlag;
    int currentTargetIndex;


    public InteractionModel() {
        subscribers = new ArrayList<>();
        selectedBlobs = new ArrayList<>();
        lassoPoints = new ArrayList<>();
        blobClipboard = new ArrayList<>();
        trainingModeFlag = false;
        currentTargetIndex = 0;
    }

    public void addSubscriber(IModelListener sub) {
        subscribers.add(sub);
    }

    private void notifySubscribers() {
        subscribers.forEach(s -> s.iModelChanged());
    }

    public void addSelectedBlob(Blob b) {
        this.selectedBlobs.add(b);
        notifySubscribers();
    }

    public void removeSelectedBlob(Blob b) {
        this.selectedBlobs.remove(b);
        notifySubscribers();
    }

    public void unselect() {
        selectedBlobs = new ArrayList<>();
        notifySubscribers();
    }

    public ArrayList<Blob> getSelected() {
        return selectedBlobs;
    }

    public void clearLassoPoints()
    {
        this.lassoPoints.clear();
        notifySubscribers();
    }

    public void addLassoPoint(Point2D newPoint)
    {
        this.lassoPoints.add(newPoint);
        notifySubscribers();
    }

    public ArrayList<Point2D> getLassoPoints()
    {
        return this.lassoPoints;
    }

    public void setSelectedBlobs(ArrayList<Blob> toSelect) {
        this.selectedBlobs = toSelect;
        notifySubscribers();
    }

    public void copySelected() {
        // deep clone of selected blobs
        this.blobClipboard = new ArrayList<>();
        for (Blob blob : selectedBlobs)
        {
            Blob newBlob = new Blob(blob.x, blob.y);
            newBlob.setRadius(blob.r);
            blobClipboard.add(newBlob);
        }

    }

    public void cutSelected() {
        copySelected();
    }

    public ArrayList<Blob> pasteFromClipboard() {
        this.selectedBlobs = new ArrayList<>();
        for (Blob blob : blobClipboard)
        {
            Blob newBlob = new Blob(blob.x, blob.y);
            newBlob.setRadius(blob.r);
            selectedBlobs.add(newBlob);
        }
        return selectedBlobs;
    }

    public void setTrainingFlag(boolean b) {
        this.trainingModeFlag = b;
        notifySubscribers();
    }

    public boolean getTrainingMode() {
        return this.trainingModeFlag;

    }

    public int getCurrentTargetIndex() {
        return currentTargetIndex;
    }

    public void setCurrentTargetIndex(int currentTargetIndex) {
        this.currentTargetIndex = currentTargetIndex;
        notifySubscribers();
    }
}

