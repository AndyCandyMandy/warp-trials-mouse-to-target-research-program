package com.example.cmpt481_term_project;

import java.lang.reflect.Array;
import java.util.*;

public class BlobModel {
    private List<BlobModelListener> subscribers;
    private List<Blob> blobs;

    public BlobModel() {
        subscribers = new ArrayList<>();
        blobs = new ArrayList<>();
    }

    public void addBlob(double x, double y) {
        blobs.add(new Blob(x,y));
        notifySubscribers();
    }

    public void moveBlob(ArrayList<Blob> selected, double dx, double dy) {
        for (Blob blob : selected)
        {
            blob.move(dx,dy);
        }
        notifySubscribers();
    }

    public void addSubscriber(BlobModelListener sub) {
        subscribers.add(sub);
    }

    private void notifySubscribers() {
        subscribers.forEach(s -> s.modelChanged());
    }

    public List<Blob> getBlobs() {
        return blobs;
    }

    public boolean hitBlob(double x, double y) {
        for (Blob b : blobs) {
            if (b.contains(x,y)) return true;
        }
        return false;
    }

    public Blob whichHit(double x, double y) {
        for (Blob b : blobs) {
            if (b.contains(x,y)) return b;
        }
        return null;
    }

    public void resizeBlob(ArrayList<Blob> selected, double dX) {
        for (Blob blob : selected)
        {
            blob.setRadius(blob.getRadius() + dX);
        }
        notifySubscribers();
    }

    public void deleteSelectedBlobs(ArrayList<Blob> selected) {
        for (Blob blob : selected) {
            blobs.remove(blob);
        }
        notifySubscribers();
    }

    public void copyBlobsToModel(ArrayList<Blob> blobsToAdd) {
        this.blobs.addAll(blobsToAdd);
        notifySubscribers();
    }

}
