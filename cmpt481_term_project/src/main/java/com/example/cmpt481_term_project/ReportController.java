/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

public class ReportController
{
    BlobModel model;
    InteractionModel iModel;

    /**
     * Empty constructor
     */
    public ReportController()
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


}
