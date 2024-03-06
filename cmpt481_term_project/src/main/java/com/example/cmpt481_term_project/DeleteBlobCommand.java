/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

import java.util.ArrayList;

public class DeleteBlobCommand implements TargetCommand
{
    BlobModel model;
    ArrayList<Blob> deletedBlobs;

    /**
     * Command for deleting new blob(s)
     * @param newModel - the model to delete blobs from
     * @param blobs - the blobs to delete
     */
    public DeleteBlobCommand(BlobModel newModel, ArrayList<Blob> blobs)
    {
        this.model = newModel;
        this.deletedBlobs = blobs;
    }

    /**
     * Perform the command
     */
    @Override
    public void doCommand()
    {
        model.deleteSelectedBlobs(deletedBlobs);
    }

    /**
     * Undo the command
     */
    @Override
    public void undoCommand()
    {
        for (Blob b : deletedBlobs)
        {
            model.addBlob(b);
        }
    }
}
