/*
NAMES: Aaron Mee, Siddharth Padakanti, Andy Giang, Jesse Paterson
NSIDS: ajm403, kca647, iaz102, xgm608
STUDENT NUMBER: 11173570, 11311844, 11326516, 11310937
COURSE: CMPT481 - Term Project
*/
package com.example.cmpt481_term_project;

import javafx.scene.input.KeyCode;

import java.util.*;

public class AppModel {
    private List<AppModelListener> subscribers;
    private List<Target> targets;
    private List<WarpLocation> warps;
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

        this.width = w;
        this.height = h;
        this.targetRadius = 30;
        this.numTargets = 50;
        this.numTrials = 10;
        this.showWarps = false;

        this.mouseX = 0;
        this.mouseY = 0;

        this.currentMode = AppMode.MECH_SELECT;

        // Create timer and timertask for fading out mouse trail
        fadeTimer = new Timer();

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
        // Method for recording a click during trial mode
        if (hitTarget((int) x, (int) y)) {
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

        this.currTarget = random.nextInt(numTargets);
        targets.get(currTarget).select();

        notifySubscribers();
    }

}
