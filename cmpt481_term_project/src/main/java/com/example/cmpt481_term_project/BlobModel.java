/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

import java.util.*;

public class BlobModel
{
    private List<BlobModelListener> subscribers;
    private List<Blob> blobs;

    /**
     * Creates new blob model
     */
    public BlobModel()
    {
        subscribers = new ArrayList<>();
        blobs = new ArrayList<>();
    }

    /**
     * Adds a new blob
     * @param newBlob - the new blob
     */
    public void addBlob(Blob newBlob)
    {
        blobs.add(newBlob);
        notifySubscribers();
    }

    /**
     * Moves a blob
     * @param selected the blob to move
     * @param dx       the change in x to move
     * @param dy       the change in y to move
     */
    public void moveBlob(ArrayList<Blob> selected, double dx, double dy)
    {
        for (Blob blob : selected)
        {
            blob.move(dx, dy);
        }
        notifySubscribers();
    }

    /**
     * Adds a subscriber to the model
     * @param sub the subscriber
     */
    public void addSubscriber(BlobModelListener sub)
    {
        subscribers.add(sub);
    }

    /**
     * Notifies all subscribers
     */
    private void notifySubscribers()
    {
        subscribers.forEach(BlobModelListener::modelChanged);
    }

    /**
     * Gets the list of blobs
     * @return - THe list of blobs
     */
    public List<Blob> getBlobs()
    {
        return blobs;
    }

    /**
     * Returns a boolean if a point has hit a blob
     * @param x - the x coordinate of the point
     * @param y - the y coordinate of the point
     * @return true if point hit a blob, false otherwise
     */
    public boolean hitBlob(double x, double y)
    {
        for (Blob b : blobs)
        {
            if (b.contains(x, y))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns which blob got hit
     * @param x the x coordinate of the point to check
     * @param y the y coordinate of the point to check
     * @return The blob which got hit
     */
    public Blob whichHit(double x, double y)
    {
        Blob blobHit = null;
        for (Blob b : blobs)
        {
            if (b.contains(x, y))
            {
                blobHit = b;
            }
        }
        return blobHit;
    }

    /**
     * Checks if a specific blob has been hit
     * @param targetIndex - The index of the blob to check
     * @param x - the x coordinate of the point to check
     * @param y - the y coordinate of the point to check
     * @return - true if hit, false otherwise
     */
    public boolean hitTarget(int targetIndex, int x, int y)
    {
        if (targetIndex >= blobs.size())
        {
            throw new IllegalArgumentException("Target index out of range");
        }
        Blob blobHit = blobs.get(targetIndex);
        return blobHit.contains(x,y);
    }

    /**
     * Resizes a list of blob
     * @param selected - the blobs to resize
     * @param dX       - the amount to resize by
     */
    public void resizeBlob(ArrayList<Blob> selected, double dX)
    {
        for (Blob blob : selected)
        {
            blob.setRadius(blob.getRadius() + dX);
        }
        notifySubscribers();
    }

    /**
     * Deletes blobs
     * @param selected blobs to delete
     */
    public void deleteSelectedBlobs(ArrayList<Blob> selected)
    {
        for (Blob blob : selected)
        {
            blobs.remove(blob);
        }
        notifySubscribers();
    }

    /**
     * Adds blobs
     * @param createdBlobs blobs to add
     */
    public void addBlobs(ArrayList<Blob> createdBlobs)
    {
        for (Blob b : createdBlobs)
        {
            addBlob(b);
        }
    }
}
