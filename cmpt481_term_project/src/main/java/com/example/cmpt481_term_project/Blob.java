/*
NAME: Jesse Paterson
NSID: xgm608
STUDENT NUMBER: 11310937
COURSE: CMPT381
*/
package com.example.cmpt481_term_project;

public class Blob
{
    private double x, y, r;

    /**
     * Creates new blob
     * @param nx - x coordinate
     * @param ny - y coordinate
     */
    public Blob(double nx, double ny)
    {
        x = nx;
        y = ny;
        r = 50;
    }

    /**
     * Gets the blob radius
     * @return radius
     */
    public double getRadius()
    {
        return this.r;
    }

    /**
     * Sets the radius
     * @param newRadius the new radius
     */
    public void setRadius(double newRadius)
    {
        if (newRadius > 5)
        {
            this.r = newRadius;
        } else
        {
            r = 5;
        }
    }

    /**
     * Gets the x position
     * @return teh x coordinate
     */
    public double getX()
    {
        return x;
    }

    /**
     * Gets the y position
     * @return teh y coordinate
     */
    public double getY()
    {
        return y;
    }

    /**
     * Moves the blob
     * @param dx - the change in x
     * @param dy - the change in y
     */
    public void move(double dx, double dy)
    {
        x += dx;
        y += dy;
    }

    /**
     * Returns a boolean of if a point is within the blob
     * @param cx - the point x coordinate
     * @param cy - the point y coordinate
     * @return - true if it is contained, false otherwise
     */
    public boolean contains(double cx, double cy)
    {
        return dist(cx, cy, x, y) <= r;
    }

    /**
     * Returns the distance of one point to another
     * @param x1 x coordinate 1
     * @param y1 y coordinate 1
     * @param x2 x coordinate 2
     * @param y2 y coordinate 2
     * @return The distance between the two points
     */
    private double dist(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
