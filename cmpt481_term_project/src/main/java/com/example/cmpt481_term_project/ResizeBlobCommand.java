/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

import java.util.ArrayList;

public class ResizeBlobCommand implements TargetCommand
{
    BlobModel model;
    ArrayList<Blob> blobsResized;
    int dx;

    /**
     * Command for resizing blobs
     * @param newModel - THe model to change
     * @param blobs - the blobs to resize
     * @param dx - The horizontal distance to resize by
     */
    public ResizeBlobCommand(BlobModel newModel, ArrayList<Blob> blobs, int dx)
    {
        this.model = newModel;
        this.blobsResized = blobs;
        this.dx = dx;
    }

    /**
     * Performs the command
     */
    @Override
    public void doCommand()
    {
        model.resizeBlob(blobsResized, -dx);
    }

    /**
     * Redoes the command
     */
    @Override
    public void undoCommand()
    {
        model.resizeBlob(blobsResized, dx);
    }
}
