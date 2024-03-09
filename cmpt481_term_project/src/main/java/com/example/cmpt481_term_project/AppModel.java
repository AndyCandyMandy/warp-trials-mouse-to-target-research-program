/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

import java.util.*;

public class AppModel {
    private List<AppModelListener> subscribers;
    private List<Target> targets;
    private List<WarpLocation> warps;
    private List<GridPointer> gridPoints;
    private WarpTrail warpTrail;
    private boolean showWarps;
    private int height;
    private int width;
    private int numTargets;
    private int numTrials;
    private int targetRadius;
    private int currTarget;
    private double mouseX;
    private double mouseY;


    // System-defined Warp Mechanism attributes
    protected boolean sysDefTargetSelection = false;
    protected List<Point2D> sysDefClickPositions;
    protected List<Point2D> sysDefWarpLocations;


    // Flick mechanism related attributes
    private double flickX, flickY;
    private final double minFlickDistance = 30.0;
    private boolean trackingFlick;




    public enum AppMode {MECH_SELECT, PRE_TRIAL, TRIAL, DONE}

    private AppMode currentMode;

    public enum Mechanism {GRID, USR_KEY, SYS_DEF, FLICK}



    private Mechanism currentMechanism;
    Random random = new Random();
    Timer fadeTimer;
    TimerTask fadeTask;


    /**
     * Creates new app model
     */
    public AppModel(int w, int h) {
        subscribers = new ArrayList<>();
        targets = new ArrayList<>();
        warps = new ArrayList<>();
        warpTrail = new WarpTrail(0.0, 0.0, 0.0, 0.0);
        sysDefClickPositions = new ArrayList<>();
        gridPoints = new ArrayList<>();
        warpTrail = new WarpTrail(0.0, 0.0, 0.0, 0.0, 0.0);

        this.width = w;
        this.height = h;
        this.targetRadius = 30;
        this.numTargets = 50;
        this.numTrials = 10;
        this.showWarps = false;

        this.mouseX = 0;
        this.mouseY = 0;

        setUpGridPoints((double) this.width / 100, (double) this.height / 100);

        this.currentMode = AppMode.MECH_SELECT;

        // Create timer and timertask for fading out mouse trail
        fadeTimer = new Timer();

        // Flick mechanism related stuff
        flickX = 0.0;
        flickY = 0.0;
        trackingFlick = false;
    }

    /**
     * Method that starts a fade timer that repeats at a specified rate
     */
    public void startTrailFadeTimer() {
        fadeTimer.cancel();
        fadeTimer.purge();
        fadeTimer = new Timer();

        warpTrail.reset();
        fadeTask = new TimerTask()
        {
            public void run()
            {
                // Reduce thickness and opacity until it disappears
                if (warpTrail.getOpacity() > 0) {
                    warpTrail.fadeStep();
                    notifySubscribers();
                } else {
                    this.cancel();
                }
            }


        };
        fadeTimer.scheduleAtFixedRate(fadeTask, 0L, 50L);
    }

    /**
     * Saves the flick starting coordinates for drawing the flick line
     */
    public void saveFlickStartCoords() {
        // Save mouse coords where hotkey was first pressed
        this.flickX = this.mouseX;
        this.flickY = this.mouseY;
    }

    /**
     * Checks and returns if the min distance has been reached for creating the flick line
     * @return - True if you reached the min distance, false otherwise
     */
    public boolean reachedMinFlickDistance() {
        // Calculate distance from flick start to current mouse position
        return calculateDistance(flickX, flickY, mouseX, mouseY) >= minFlickDistance;
    }

    /**
     * Sets the flag indicating that the flick is being tracked
     * @param b - Flag that indicates that the flick is being tracked
     */
    public void setFlickTracking(boolean b) {
        this.trackingFlick = b;
    }

    /**
     * Returns the status of the flick tracking flag
     * @return
     */
    public boolean trackingFlick() {
        return trackingFlick;
    }

    /**
     * Method for getting and returning the flick target based on the longer flick line
     * @param x2 - the start position x coord
     * @param y2 - the start position y coord
     * @param x3 - the end position x coord
     * @param y3 - the end position y coord
     * @return
     */
    public int getClosestFlickTarget(double x2, double y2, double x3, double y3) {
        int closestWarp = -1;
        double dist = Double.MAX_VALUE;
        for (int i = 0; i < warps.size(); i++) {
            if (calculateDistanceToLine(x2, y2, x3,y3, warps.get(i).getX(), warps.get(i).getY()) < dist &&
                    calculateAngle(x3, y3, x2, y2, warps.get(i).getX(), warps.get(i).getY()) < 45.0) {
                closestWarp = i + 1;
                dist = calculateDistanceToLine(x2, y2, x3,y3, warps.get(i).getX(), warps.get(i).getY());
            }
        }
        //System.out.println("Angle: " + calculateAngle(x3, y3, x2, y2, warps.get(closestWarp - 1).getX(), warps.get(closestWarp - 1).getY()));
        return closestWarp;
    }

    /**
     * Method that calculates the distance between two points
     * @param x1 - 1st point, x coord
     * @param y1 - 1st point, y coord
     * @param x2 - 2nd point, x coord
     * @param y2 - 2nd point, y coord
     * @return - Returns the distance between points
     */
    private static double calculateDistance(double x1, double y1, double x2, double y2) {
        // distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

        return distance;
    }

    /**
     * Calculates the distance from a point to a line
     * @param x1 - 1st point of line, x coord
     * @param y1 - 1st point of line, y coord
     * @param x2 - 2nd point of line, x coord
     * @param y2 - 2nd point of line, y coord
     * @param x0 - Point x coord
     * @param y0 - Point x coord
     * @return - Returns the distance from the point to the line
     */
    public static double calculateDistanceToLine(double x1, double y1, double x2, double y2, double x0, double y0) {
        // Using the formula for the distance from a point to a line
        double numerator = Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1);
        double denominator = Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));

        double distance = numerator / denominator;
        return distance;
    }

    /**
     * Calculates the angle formed by three points where one point is shared between the two lines
     * @param x1 - 1st point, x coord
     * @param y1 - 1st point, y coord
     * @param x2 - 2nd point, x coord - Shared point
     * @param y2 - 2nd point, y coord - Shared point
     * @param x3 - 3rd point, x coord
     * @param y3 - 3rd point, y coord
     * @return - Returns the angle formed by the three points
     */
    public static double calculateAngle(double x1, double y1, double x2, double y2, double x3, double y3) {
        // Create vectors from points
        double vec1X = x1 - x2;
        double vec1Y = y1 - y2;
        double vec2X = x3 - x2;
        double vec2Y = y3 - y2;
        double dotProduct = (vec1X * vec2X) + (vec1Y * vec2Y);
        // Calculate the magnitude of the vectors
        double mag1 = Math.sqrt(Math.pow(vec1X, 2) + Math.pow(vec1Y, 2));
        double mag2 = Math.sqrt(Math.pow(vec2X, 2) + Math.pow(vec2Y, 2));
        // Get cosine
        double cos = dotProduct / (mag1 * mag2);
        // return angle in degrees - 180 to correct
        return Math.abs(Math.toDegrees(Math.acos(cos)) - 180);
    }

    /**
     * Method for getting the saved flick x coord
     * @return - Returns the flick x coord
     */
    public double getFlickX() {
        return flickX;
    }
    /**
     * Method for getting the saved flick y coord
     * @return - Returns the flick y coord
     */
    public double getFlickY() {
        return flickY;
    }

    public Mechanism getCurrentMechanism() {
        return currentMechanism;
    }

    public double getMouseX() {
        return mouseX;
    }

    public void setMouseX(double mouseX) {
        this.mouseX = mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public void setMouseY(double mouseY) {
        this.mouseY = mouseY;
    }

    public WarpTrail getWarpTrail() {
        return warpTrail;
    }

    public void setWarpTrail(double endPointX, double endPointY, double mouseX, double mouseY) {
        warpTrail.setCoords(endPointX, endPointY, mouseX, mouseY);
        notifySubscribers();
    }

    /**
     * Adds a new target
     *
     * @param newTarget - the new target
     */
    public void addTarget(Target newTarget) {
        targets.add(newTarget);
        notifySubscribers();
    }

    /**
     * Adds a new warp location
     *
     * @param newWarp - the new warp location
     */
    public void addWarp(WarpLocation newWarp) {
        warps.add(newWarp);
        notifySubscribers();
    }

    /**
     * Toggles the showing of warp locations
     */
    public void toggleWarps() {
        this.showWarps = !this.showWarps;
        notifySubscribers();
    }

    /**
     * Method for getting list of warps
     * @return - Returns the WarpLocation ArrayList
     */
    public List<WarpLocation> getWarps() {
        return warps;
    }

    /**
     * Returns if warps are toggled to be visible
     * @return
     */
    public boolean isWarpsVisible() {
        return showWarps;
    }

    public List<GridPointer> getGridList() {
        return this.gridPoints;
    }

    public void setUpGridPoints(double x, double y) {
        double yPos = this.width/ x;
        double xPos = this.height/ y;
        for (int i = 1; i <= x - 1; i++) {
            for (int j = 1; j <= y - 1; j++) {
                this.gridPoints.add(new GridPointer(xPos, yPos));
                yPos += this.width / x;
            }
            yPos = this.width / x;
            xPos += this.height / y;
        }
    }

    public GridPointer findGridPoint(double x, double y) {
        for (GridPointer point : this.gridPoints) {
            point.mouseInRadius(x, y);
            if (point.getInRadius()) {
                return point;
            }
        }
        return null;
    }

    /**
     * Adds a subscriber to the model
     *
     * @param sub the subscriber
     */
    public void addSubscriber(AppModelListener sub) {
        subscribers.add(sub);
    }

    /**
     * Notifies all subscribers
     */
    private void notifySubscribers() {
        subscribers.forEach(AppModelListener::modelChanged);
    }

    /**
     * Gets the list of targets
     *
     * @return - THe list of targets
     */
    public List<Target> getTargets() {
        return targets;
    }

    /**
     * Checks if a specific target has been hit
     *
     * @param x - the x coordinate of the point to check
     * @param y - the y coordinate of the point to check
     * @return - true if hit, false otherwise
     */
    public boolean hitTarget(int x, int y) {
        Target targetHit = targets.get(this.currTarget);
        return targetHit.contains(x, y);
    }

    /**
     * Returns the current model mode
     */
    public AppMode getCurrentMode() {
        return currentMode;
    }

    /**
     * Records a click event during trials
     */
    public void recordClick(double x, double y) {
        int oldTarget = currTarget;
        // Method for adding user click positions
        if (sysDefTargetSelection) {
            sysDefClickPositions.add(new Point2D(x, y));
            System.out.println("Clicked target");
            // If n number of coordinates has been clicked...
            if (sysDefClickPositions.size() > 19) {
                sysDefTargetSelection = false;
                //...generate 4 average warp locations..
                sysDefWarpLocations = sysDefGenerateWarpLocations((ArrayList<Point2D>) sysDefClickPositions);
                //...and initialize normal target selection
                this.currTarget = random.nextInt(numTargets);
                targets.get(currTarget).select();
                notifySubscribers();}
        }
        // Method for recording a click during trial mode
        else if (hitTarget((int) x, (int) y)) {
            // Deselect old target
            targets.get(currTarget).deselect();
            // Select new target
            while (currTarget == oldTarget) {
                this.currTarget = random.nextInt(numTargets);
            }
            targets.get(currTarget).select();
            numTrials--;
            if (numTrials == 0) {
                nextMode();
            }
            notifySubscribers();
        }
    }

    /**
     * Sets the models mechanism
     */
    public void setMechanism(KeyCode k) {
        switch (k) {
            case DIGIT1 -> {
                this.currentMechanism = Mechanism.GRID;
            }
            case DIGIT2 -> {
                this.currentMechanism = Mechanism.USR_KEY;
            }
            case DIGIT3 -> {
                this.currentMechanism = Mechanism.SYS_DEF;

            }
            case DIGIT4 -> {
                this.currentMechanism = Mechanism.FLICK;
            }
        }
    }

    public Mechanism getCurrentMechanism() {
        return this.currentMechanism;
    }

    /**
     * Advances the current model mode
     */
    public void nextMode() {
        switch (this.currentMode) {
            case MECH_SELECT -> {
                this.currentMode = AppMode.PRE_TRIAL;
            }
            case PRE_TRIAL -> {
                this.currentMode = AppMode.TRIAL;
                if (getCurrentMechanism() == Mechanism.SYS_DEF) {
                    sysDefTargetSelection = true;
                }
                generateRandomTargets();
            }
            case TRIAL -> {
                this.currentMode = AppMode.DONE;
            }
            case DONE -> {
                System.exit(0);
            }
        }
        notifySubscribers();
    }

    /**
     * Generates a random selection of targets with no overlaps
     */
    public void generateRandomTargets() {
        random = new Random();
        int maxX = width - targetRadius;
        int maxY = height - targetRadius;
        int min = targetRadius * 2;

        for (int i = 0; i < numTargets; i++) {
            // create targets and give it random coords with no overlaps
            while (true) {
                boolean overlap = false;
                int targetX = random.nextInt(maxX - min + 1) + min;
                int targetY = random.nextInt(maxY - min + 1) + min;
                for (Target t : targets) {
                    if (Math.sqrt(Math.pow(targetX - t.getX(), 2) + Math.pow(targetY - t.getY(), 2)) < min) {
                        overlap = true;
                        break;
                    }
                }
                if (!overlap) {
                    Target newTarget = new Target(targetX, targetY, targetRadius);
                    this.addTarget(newTarget);
                    break;
                }
            }
        }

        if (!sysDefTargetSelection) {
            this.currTarget = random.nextInt(numTargets);
            targets.get(currTarget).select();

            notifySubscribers();
        }
    }

    /**
     * Generates Average Locations for Warping from the given list of coordinates
     * and return them in an ArrayList
     */
    public ArrayList sysDefGenerateWarpLocations(ArrayList<Point2D> coordinatesList) {

        ArrayList<Point2D> warpCoordinates = new ArrayList<>();

        Collections.sort(coordinatesList, new Comparator<Point2D>() {
            @Override
            public int compare(Point2D p1, Point2D p2) {
                if (p1.getX() < p2.getX()) {
                    return -1;
                } else if (p1.getX() > p2.getX()) {
                    return 1;
                } else {
                    // X values are equal, compare Y
                    if (p1.getY() < p2.getY()) {
                        return -1;
                    } else if (p1.getY() > p2.getY()) {
                        return 1;
                    } else {
                        return 0; // Points are equal
                    }
                }
            }
        });

        int size = coordinatesList.size();
        int subArraySize = size / 4;

        ArrayList<Point2D> array1 = new ArrayList<>(coordinatesList.subList(0, subArraySize));
        ArrayList<Point2D> array2 = new ArrayList<>(coordinatesList.subList(subArraySize, 2 * subArraySize));
        ArrayList<Point2D> array3 = new ArrayList<>(coordinatesList.subList(2 * subArraySize, 3 * subArraySize));
        ArrayList<Point2D> array4 = new ArrayList<>(coordinatesList.subList(3 * subArraySize, size));

        warpCoordinates.add(calculateAverage(array1));
        warpCoordinates.add(calculateAverage(array2));
        warpCoordinates.add(calculateAverage(array3));
        warpCoordinates.add(calculateAverage(array4));

        return warpCoordinates;
    }

    /*
     * Helper function to calculate the average Point2D
     */
    private Point2D calculateAverage(ArrayList<Point2D> points) {
        double sumX = 0;
        double sumY = 0;

        for (Point2D point : points) {
            sumX += point.getX();
            sumY += point.getY();
        }

        return new Point2D(sumX / points.size(), sumY / points.size());
    }
}
