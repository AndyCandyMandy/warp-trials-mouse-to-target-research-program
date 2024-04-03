/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.util.*;

public class AppModel {
    private List<AppModelListener> subscribers;
    private List<Target> targets;
    private List<WarpLocation> warps;

    // Warping information
    private WarpTrail warpTrail;
    private boolean showWarps;
    private int numOfWarps;

    private int height;
    private int width;
    private int numTargets;
    private int numTrials;
    private int numBlocks;
    private int targetRadius;
    private int currTarget;
    private double fittsID;

    // Mouse information
    private double mouseX;
    private double mouseY;
    private int numOfErrors;
    private long selectionTime;

    // Grid-defined Warp Mechanism Attributes
    private List<GridPointer> gridPoints;
    private int gridRow = 5; // Default 5
    private int gridCollumn = 4; // Default 4


    // System-defined Warp Mechanism attributes
    protected boolean sysDefTargetSelection = false;
    protected List<Point2D> sysDefClickPositions;
    protected List<Point2D> sysDefWarpLocations;


    // Flick mechanism related attributes
    private double flickX, flickY;
    private final double minFlickDistance = 30.0;
    private boolean trackingFlick;


    public enum AppMode {MECH_SELECT, TRIAL_SELECT, PRE_TRIAL, TRIAL, DONE}

    private AppMode currentMode;

    public enum Mechanism {NO_MECH, GRID, USR_KEY, SYS_DEF, FLICK}

    public enum TrialMode {RANDOM_TARGETS, CLUSTER_TARGETS, REAL_UI}

    private TrialMode trialMode;

    // Constants for changing the number of clusters, trials and blocks of a run
    private final int NUM_CLUSTERS = 4;
    private final int NUM_TRIALS = 20;
    private final int NUM_BLOCKS = 3;

    private Target[] clusterPoints = new CircleTarget[NUM_CLUSTERS];

    // Real UI image
    Image uiImage;


    private Mechanism currentMechanism;
    Random random = new Random();
    Timer fadeTimer;
    TimerTask fadeTask;

    // String[] for storing data from trials
    private ArrayList<String[]> trialData;


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

        this.width = w;
        this.height = h;
        this.targetRadius = 30;
        this.numTargets = 50;
        this.numBlocks = NUM_BLOCKS;
        this.numTrials = NUM_TRIALS;
        this.showWarps = false;
        this.numOfWarps = 0;
        this.fittsID = 0.0;

        this.mouseX = 0;
        this.mouseY = 0;

        this.numOfErrors = 0;
        this.selectionTime = 0;

        setUpGridPoints(getGridRow(), getGridCollumn());

        this.currentMode = AppMode.MECH_SELECT;

        // set default trial mode to RANDOM_TARGETS
        this.trialMode = TrialMode.CLUSTER_TARGETS;


        // Create timer and timertask for fading out mouse trail
        fadeTimer = new Timer();

        // Flick mechanism related stuff
        flickX = 0.0;
        flickY = 0.0;
        trackingFlick = false;

        // get UI image
        uiImage = new Image("/UnityUI.png");

        // setup data arraylist with header
        this.trialData = new ArrayList<>();
        this.trialData.add(new String[]{"MechID", "BlockNum", "TrialNum", "NumError", "NumWarp", "ElapsedTime", "FittsID"});
    }

    public double getFittsID(){
        return fittsID;
    }

    /**
     * Returns the current selection time
     */
    public long getSelectionTime() {
        return System.currentTimeMillis() - selectionTime;
    }

    /**
     * Method for adding a data entry to the stored data
     */
    public void recordDataEntry() {
        // Check whether we are on first trial (numTrials should be 20 then be reduced every successful click),
        // if true, fittsID should be 0

        // Remember the previous position
        double prevX = mouseX;
        double prevY = mouseY;

        if (!targets.isEmpty()) {

            Target currentTarget = targets.get(currTarget);

            if (numTrials < 20) {
                fittsID = calculateDistance(prevX, prevY, currentTarget.getX(), currentTarget.getY());
            }

            // Print the results
            this.trialData.add(new String[]{this.currentMechanism.toString(), String.valueOf(this.numBlocks),
                    String.valueOf(this.numTrials), String.valueOf(this.numOfErrors), String.valueOf(this.numOfWarps),
                    String.valueOf(this.getSelectionTime()), String.valueOf(this.fittsID)});
        }
    }

    /**
     * Method to export all data as a csv file
     */
    private void exportAllData() {
        // set filename - mechanism (5) and trial mode (3)
        // 15 Different combinations
        String filename = this.currentMechanism.toString() + "-" + this.trialMode.toString() + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Iterate over the rows and write them to the CSV file
            for (String[] row : this.trialData) {
                // Iterate over columns within a row
                for (int i = 0; i < row.length; i++) {
                    writer.append(row[i]);
                    // Add a comma if it's not the last column
                    if (i < row.length - 1) {
                        writer.append(",");
                    }
                }
                // Add a new line character after each row
                writer.append("\n");
            }
            System.out.println("CSV file " + filename + " has been created successfully.");
        } catch (IOException e) {
            System.err.println("Error occurred while writing to CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Either starts or resets the timer for the trial
     */
    public void getElapsedTime() {
        selectionTime = System.currentTimeMillis();
    }

    /**
     * Increments the warp count by 1
     */
    public void addToWarpCount() {
        numOfWarps++;
    }

    /**
     * Returns the error count
     */
    public int getWarpCount() {
        return numOfWarps;
    }

    /**
     * Resets the error count to 0
     */
    public void resetWarpCount() {
        numOfWarps = 0;
    }

    /**
     * Increments the error count by 1
     */
    public void addToErrorCount() {
        numOfErrors++;
    }

    /**
     * Returns the error count
     */
    public int getErrorCount() {
        return numOfErrors;
    }

    /**
     * Resets the error count to 0
     */
    public void resetErrorCount() {
        numOfErrors = 0;
    }

    /**
     * Method that starts a fade timer that repeats at a specified rate
     */
    public void startTrailFadeTimer() {
        fadeTimer.cancel();
        fadeTimer.purge();
        fadeTimer = new Timer();

        warpTrail.reset();
        fadeTask = new TimerTask() {
            public void run() {
                // Reduce thickness and opacity until it disappears
                if (warpTrail.getOpacity() > 0) {
                    warpTrail.fadeStep();
                    notifySubscribers();
                } else {
                    warpTrail.setDrawn(false);
                    this.cancel();
                }
            }


        };
        fadeTimer.scheduleAtFixedRate(fadeTask, 0L, 50L);
    }

    /**
     * Get the UI image
     */
    public Image getUIImage() {
        return this.uiImage;
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
     *
     * @return - True if you reached the min distance, false otherwise
     */
    public boolean reachedMinFlickDistance() {
        // Calculate distance from flick start to current mouse position
        return calculateDistance(flickX, flickY, mouseX, mouseY) >= minFlickDistance;
    }

    /**
     * Sets the flag indicating that the flick is being tracked
     *
     * @param b - Flag that indicates that the flick is being tracked
     */
    public void setFlickTracking(boolean b) {
        this.trackingFlick = b;
    }

    /**
     * Returns the status of the flick tracking flag
     *
     * @return
     */
    public boolean trackingFlick() {
        return trackingFlick;
    }

    /**
     * Method for getting and returning the flick target based on the longer flick line
     *
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
            if (calculateDistanceToLine(x2, y2, x3, y3, warps.get(i).getX(), warps.get(i).getY()) < dist &&
                    calculateAngle(x3, y3, x2, y2, warps.get(i).getX(), warps.get(i).getY()) < 45.0) {
                closestWarp = i + 1;
                dist = calculateDistanceToLine(x2, y2, x3, y3, warps.get(i).getX(), warps.get(i).getY());
            }
        }
        //System.out.println("Angle: " + calculateAngle(x3, y3, x2, y2, warps.get(closestWarp - 1).getX(), warps.get(closestWarp - 1).getY()));
        return closestWarp;
    }

    /**
     * Method that calculates the distance between two points
     *
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
     *
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
     *
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
     *
     * @return - Returns the flick x coord
     */
    public double getFlickX() {
        return flickX;
    }

    /**
     * Method for getting the saved flick y coord
     *
     * @return - Returns the flick y coord
     */
    public double getFlickY() {
        return flickY;
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
     *
     * @return - Returns the WarpLocation ArrayList
     */
    public List<WarpLocation> getWarps() {
        return warps;
    }

    /**
     * Returns if warps are toggled to be visible
     *
     * @return
     */
    public boolean isWarpsVisible() {
        return showWarps;
    }

    public List<GridPointer> getGridList() {
        return this.gridPoints;
    }

    public int getGridRow() {
        return this.gridRow;
    }

    public int getGridCollumn() {
        return this.gridCollumn;
    }

    public void setUpGridPoints(double x, double y) {
        double xPos = this.width / x;
        double yPos = this.height / y;

        for (int i = 1; i <= y - 1; i++) {
            for (int j = 1; j <= x - 1; j++) {
                this.gridPoints.add(new GridPointer(xPos, yPos));
                xPos += this.width / x;
            }

            xPos = this.width / x;
            yPos += this.height / y;
        }
    }

    public int warpUpGrid(double x, double y) {
        double adjacentY = 0;
        double shortestX = this.width;
        int numPos = 0;

        for (int i = 1; i <= this.gridPoints.size(); i++) {
            double len = Math.sqrt(Math.pow(x - this.gridPoints.get(i - 1).getX(), 2) + Math.pow(y - this.gridPoints.get(i - 1).getY(), 2));
            if (this.gridPoints.get(i - 1).getY() <= y && this.gridPoints.get(i - 1).getY() >= adjacentY) {
                adjacentY = this.gridPoints.get(i - 1).getY();


                if (y != this.gridPoints.get(i - 1).getY() && len < shortestX) {
                    shortestX = len;
                    numPos = i;
                }

            }
        }
        return numPos;
    }

    public int warpDownGrid(double x, double y) {
        double adjacentY = this.height;
        double shortestX = this.width;
        int numPos = 0;

        for (int i = 1; i <= this.gridPoints.size(); i++) {
            double len = Math.sqrt(Math.pow(x - this.gridPoints.get(i - 1).getX(), 2) + Math.pow(y - this.gridPoints.get(i - 1).getY(), 2));
            if (this.gridPoints.get(i - 1).getY() >= y && this.gridPoints.get(i - 1).getY() <= adjacentY) {
                adjacentY = this.gridPoints.get(i - 1).getY();


                if (y != this.gridPoints.get(i - 1).getY() && len < shortestX) {
                    shortestX = len;
                    numPos = i;
                }

            }
        }
        return numPos;
    }

    public int warpLeftGrid(double x, double y) {
        double adjacentX = 0;
        double shortestY = this.height;
        int numPos = 0;

        for (int i = 1; i <= this.gridPoints.size(); i++) {
            double len = Math.sqrt(Math.pow(x - this.gridPoints.get(i - 1).getX(), 2) + Math.pow(y - this.gridPoints.get(i - 1).getY(), 2));
            if (this.gridPoints.get(i - 1).getX() <= x && this.gridPoints.get(i - 1).getX() >= adjacentX) {
                adjacentX = this.gridPoints.get(i - 1).getX();


                if (x != this.gridPoints.get(i - 1).getX() && len <= shortestY) {
                    shortestY = len;
                    numPos = i;
                }

            }
        }
        return numPos;
    }

    public int warpRightGrid(double x, double y) {
        double adjacentX = this.width;
        double shortestY = this.height;
        int numPos = 0;

        for (int i = 1; i <= this.gridPoints.size(); i++) {
            double len = Math.sqrt(Math.pow(x - this.gridPoints.get(i - 1).getX(), 2) + Math.pow(y - this.gridPoints.get(i - 1).getY(), 2));
            if (this.gridPoints.get(i - 1).getX() >= x && this.gridPoints.get(i - 1).getX() <= adjacentX) {
                adjacentX = this.gridPoints.get(i - 1).getX();


                if (y != this.gridPoints.get(i - 1).getX() && len < shortestY) {
                    shortestY = len;
                    numPos = i;
                }

            }
        }
        return numPos;
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
    public boolean hitTarget(double x, double y) {
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
     * Returns the current trial mode
     */
    public TrialMode getTrialMode() {
        return trialMode;
    }

    /**
     * Records a click event during trials
     */
    public void recordClick(double x, double y) {
        int oldTarget = currTarget;
        // Method for adding user click positions
        if (sysDefTargetSelection) {
            sysDefClickPositions.add(new Point2D(x, y));
            // If n number of coordinates has been clicked...
            if (sysDefClickPositions.size() > 19) {
                sysDefTargetSelection = false;
                //...generate 4 average warp locations..
                sysDefWarpLocations = sysDefGenerateWarpLocations((ArrayList<Point2D>) sysDefClickPositions);
                //...and initialize normal target selection
                this.currTarget = random.nextInt(targets.size());
                targets.get(currTarget).select();
                notifySubscribers();
            }
        }
        // Method for recording a click during trial mode
        else if (targets.size() > 0 && hitTarget((int) x, (int) y)) {
            // Deselect old target
            targets.get(currTarget).deselect();
            // Select new target
            while (currTarget == oldTarget) {
                this.currTarget = random.nextInt(targets.size());
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
            case DIGIT0 -> {
                this.currentMechanism = Mechanism.NO_MECH;
            }
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

    /**
     * Sets the models mechanism
     */
    public void setTrialMode(KeyCode k) {
        switch (k) {
            case DIGIT1 -> {
                this.trialMode = TrialMode.RANDOM_TARGETS;
            }
            case DIGIT2 -> {
                this.trialMode = TrialMode.CLUSTER_TARGETS;
            }
            case DIGIT3 -> {
                this.trialMode = TrialMode.REAL_UI;
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
                this.currentMode = AppMode.TRIAL_SELECT;
            }
            case TRIAL_SELECT -> {
                this.currentMode = AppMode.PRE_TRIAL;
            }
            case PRE_TRIAL -> {
                // Start timer for trial
                getElapsedTime();

                this.currentMode = AppMode.TRIAL;
                if (getCurrentMechanism() == Mechanism.SYS_DEF) {
                    sysDefTargetSelection = true;
                }
                switch (this.trialMode) {
                    case RANDOM_TARGETS -> {
                        generateRandomTargets();
                    }
                    case REAL_UI -> {
                        generateUITargets();
                    }
                    case CLUSTER_TARGETS -> {
                        generateClusteredTargets();
                    }
                }
            }
            case TRIAL -> {
                this.numBlocks = this.numBlocks - 1;
                if (this.numBlocks == 0) {
                    this.currentMode = AppMode.DONE;
                    // Export the data to a csv file
                    exportAllData();
                } else {
                    this.numTrials = NUM_TRIALS;
                    sysDefClickPositions = new ArrayList<>();
                    sysDefWarpLocations = new ArrayList<>();
                    warps = new ArrayList<>();
                    this.targets = new ArrayList<>();
                    this.clusterPoints = new CircleTarget[NUM_CLUSTERS];
                    this.currentMode = AppMode.PRE_TRIAL;
                }
            }
            case DONE -> {
                System.exit(1);
            }
        }
        notifySubscribers();
    }

    public void returnToMechanismSelect() {
        switch (this.currentMode) {
            case PRE_TRIAL -> this.currentMode = AppMode.MECH_SELECT;
        }
        notifySubscribers();
    }

    /**
     * Method for generating clustered targets for a trial
     */
    public void generateClusteredTargets() {
        random = new Random();
        int min = targetRadius * 6;
        int maxX = width - min;
        int maxY = height - min;


        // Create 5 random points to cluster targets around

        for (int i = 0; i < NUM_CLUSTERS; i++) {
            // create locations where targets can be clustered around
            while (true) {
                boolean overlap = false;
                int targetX = random.nextInt(maxX - min + 1) + min;
                int targetY = random.nextInt(maxY - min + 1) + min;
                for (Target t : clusterPoints) {
                    if (t != null && Math.sqrt(Math.pow(targetX - t.getX(), 2) + Math.pow(targetY - t.getY(), 2)) < min * 2) {
                        overlap = true;
                        break;
                    }
                }
                if (!overlap) {
                    CircleTarget newCircleTarget = new CircleTarget(targetX, targetY, min);
                    clusterPoints[i] = newCircleTarget;
                    //this.addTarget(newCircleTarget);
                    break;
                }
            }
        }
        // created point to cluster around, now create targets around those points
        min = targetRadius;
        for (int i = 0; i < numTargets; i++) {
            // create targets and give it random coords with no overlaps
            double d = ((CircleTarget) clusterPoints[0]).getRadius();
            while (true) {
                boolean overlap = false;
                int cluster = random.nextInt(NUM_CLUSTERS);
                double nxt = random.nextGaussian();
                double targetX = Math.clamp(clusterPoints[cluster].getX() + nxt * d / 2, min, maxX);
                nxt = random.nextGaussian();
                //System.out.println(nxt);
                double targetY = Math.clamp(clusterPoints[cluster].getY() + nxt * d / 2, min, maxY);
                for (Target t : targets) {
                    if (Math.sqrt(Math.pow(targetX - t.getX(), 2) + Math.pow(targetY - t.getY(), 2)) < min * 2) {
                        overlap = true;
                        break;
                    }
                }
                if (!overlap) {
                    CircleTarget newCircleTarget = new CircleTarget(targetX, targetY, targetRadius);
                    this.addTarget(newCircleTarget);
                    break;
                }
            }
        }

        // Select a random target to start if not system defined
        if (!sysDefTargetSelection) {
            this.currTarget = random.nextInt(targets.size());
            targets.get(currTarget).select();
        }
        notifySubscribers();
    }

    /**
     * Generates a selection of targets for the REAl UI trial mode
     */
    public void generateUITargets() {
        // create rectangular targets from UnityUITargets.txt
        try {
            // Get the input stream for the file from resources
            InputStream is = getClass().getClassLoader().getResourceAsStream("UnityUITargets.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line by space to extract four double values
                String[] parts = line.trim().split("\\s+");
                if (parts.length != 4) {
                    // Incorrect format, skip this line
                    System.out.println("Skipping line - Incorrect number of values: " + line);
                    continue;
                }

                // Parse the double values
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double width = Double.parseDouble(parts[2]);
                double height = Double.parseDouble(parts[3]);

                double horizRatio = this.width/1500.0;
                double vertRatio = this.height/900.0;

                // Create RectTarget object and add it to the list
                RectTarget t = new RectTarget(x * horizRatio, y * vertRatio, width * horizRatio, height * vertRatio);
                targets.add(t);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Select a random target to start if not system defined
        if (!sysDefTargetSelection) {
            this.currTarget = random.nextInt(targets.size());
            targets.get(currTarget).select();
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
                    CircleTarget newCircleTarget = new CircleTarget(targetX, targetY, targetRadius);
                    this.addTarget(newCircleTarget);
                    break;
                }
            }
        }

        if (!sysDefTargetSelection) {
            this.currTarget = random.nextInt(targets.size());
            targets.get(currTarget).select();
        }
        notifySubscribers();
    }

    /**
     * Generates Average Locations for Warping from the given list of coordinates
     * and return them in an ArrayList
     */
    public ArrayList<Point2D> sysDefGenerateWarpLocations(ArrayList<Point2D> coordinatesList) {
        int numClusters = 4;

        ArrayList<Point2D> centroids = initializeCentroids(coordinatesList, numClusters);

        for (int i = 0; i < 20; i++) {
            ArrayList<ArrayList<Point2D>> clusters = assignToClusters(coordinatesList, centroids);
            centroids = recalculateCentroids(clusters);
        }

        return centroids;
    }

    /**
     * Helper function for Mechanism-3 sysDefGenerateWarpLocations function
     */
    private ArrayList<Point2D> initializeCentroids(ArrayList<Point2D> data, int k) {
        Random random = new Random();
        ArrayList<Point2D> centroids = new ArrayList<>();
        while (centroids.size() < k) {
            int randomIndex = random.nextInt(data.size());
            Point2D point = data.get(randomIndex);
            if (!centroids.contains(point)) {
                centroids.add(point);
            }
        }
        return centroids;
    }

    /**
     * Helper function for Mechanism-3 sysDefGenerateWarpLocations function
     */
    private ArrayList<ArrayList<Point2D>> assignToClusters(ArrayList<Point2D> data, ArrayList<Point2D> centroids) {
        ArrayList<ArrayList<Point2D>> clusters = new ArrayList<>();
        for (int i = 0; i < centroids.size(); i++) {
            clusters.add(new ArrayList<>());
        }

        for (Point2D point : data) {
            double minDistance = Double.MAX_VALUE;
            int closestCluster = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = calculateDistance(point, centroids.get(i));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestCluster = i;
                }
            }
            clusters.get(closestCluster).add(point);
        }
        return clusters;
    }

    /**
     * Helper function for Mechanism-3 sysDefGenerateWarpLocations function
     */
    private ArrayList<Point2D> recalculateCentroids(ArrayList<ArrayList<Point2D>> clusters) {
        ArrayList<Point2D> newCentroids = new ArrayList<>();
        for (ArrayList<Point2D> cluster : clusters) {
            if (cluster.isEmpty()) {
                // Handle empty cluster case (reinitialize or adjust strategy)
            } else {
                double sumX = 0, sumY = 0;
                for (Point2D point : cluster) {
                    sumX += point.getX();
                    sumY += point.getY();
                }
                newCentroids.add(new Point2D(sumX / cluster.size(), sumY / cluster.size()));
            }
        }
        return newCentroids;
    }

    /**
     * Calculates the distance between two points
     */
    private static double calculateDistance(Point2D p1, Point2D p2) {
        // distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }
}
