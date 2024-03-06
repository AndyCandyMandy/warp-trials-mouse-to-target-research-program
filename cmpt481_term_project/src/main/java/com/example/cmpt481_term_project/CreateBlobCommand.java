/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

import java.util.ArrayList;

public class CreateBlobCommand implements TargetCommand
{
    BlobModel model;
    ArrayList<Blob> createdBlobs;

    /**
     * Command for creating new blob(s)
     * @param newModel - the model to add blobs to
     * @param blob - the blobs to add to model
     */
    public CreateBlobCommand(BlobModel newModel, ArrayList<Blob> blob)
    {
        this.model = newModel;
        this.createdBlobs = blob;
    }

    /**
     * Perform command
     */
    @Override
    public void doCommand()
    {
        model.addBlobs(createdBlobs);
    }

    /**
     * Undo command
     */
    @Override
    public void undoCommand()
    {
        model.deleteSelectedBlobs(createdBlobs);
    }
}
