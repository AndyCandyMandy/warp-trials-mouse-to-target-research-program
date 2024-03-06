/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

import java.util.ArrayList;

public class MoveBlobsCommand implements TargetCommand
{
    BlobModel model;
    ArrayList<Blob> blobsMoved;
    int dx;
    int dy;

    /**
     * Command for moving blobs
     * @param newModel - THe model to change
     * @param newBlobs - the blobs to move
     * @param dx - The horizontal distance to move
     * @param dy - The vertical distance to move
     */
    public MoveBlobsCommand(BlobModel newModel, ArrayList<Blob> newBlobs, int dx, int dy)
    {
        this.model = newModel;
        this.blobsMoved = newBlobs;
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Performs the command
     */
    @Override
    public void doCommand()
    {
        model.moveBlob(blobsMoved, -dx, -dy);
    }

    /**
     * Undoes the command
     */
    @Override
    public void undoCommand()
    {
        model.moveBlob(blobsMoved, dx, dy);
    }
}
