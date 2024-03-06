/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

public class TrialRecord
{
    long timeElapsedMilliseconds;
    double indexOfDifficulty;

    /**
     * Creates a new trial record
     * @param time - the time elapsed
     * @param id - the index of difficulty
     */
    public TrialRecord(long time, double id)
    {
        this.timeElapsedMilliseconds = time;
        this.indexOfDifficulty = id;
    }
}
