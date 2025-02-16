package ch.epfl.cs107.icoop;


import ch.epfl.cs107.icoop.actor.CenterOfMass;
import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.Element;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.area.*;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.icoop.handler.ICoopPlayerStatusGUI;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;

import static ch.epfl.cs107.icoop.area.ICoopArea.DEFAULT_SCALE_FACTOR;


public class ICoop extends AreaGame implements DialogHandler {
    //all the areas contained in the game ICoop
    private final String[] areas = {"Spawn", "OrbWay", "Maze"};

    private ICoopPlayer playerRed;
    private ICoopPlayer playerBlue;

    private CenterOfMass centerOfMass;

    //active dialog to display if not null
    private Dialog activeDialog = null;

    private ICoopPlayerStatusGUI statusGUIRed;
    private ICoopPlayerStatusGUI statusGUIBlue;

    private void createAreas() {
        addArea(new Spawn(this));
        addArea(new OrbWay(this));
        addArea(new Maze(this));
        addArea(new Arena(this));
    }

    /**
     * @param window (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return true if the game begins properly
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            int areaIndex = 0;
            initArea(areas[areaIndex], null);
            statusGUIRed = new ICoopPlayerStatusGUI(playerRed, false);
            statusGUIBlue = new ICoopPlayerStatusGUI(playerBlue, true);
            return true;
        }
        return false;
    }

    /**
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        ICoopArea currentArea = (ICoopArea) getCurrentArea();

        if (activeDialog != null) {
            currentArea.setActiveDialogCompleted(false);
            currentArea.draw(getWindow());
            activeDialog.draw(getWindow());

            if (getWindow().getKeyboard().get(KeyBindings.NEXT_DIALOG).isPressed()) {
                activeDialog.update(deltaTime);
            }

            if (activeDialog.isCompleted()) {
                activeDialog = null;
                currentArea.setActiveDialogCompleted(true);
            }

        } else {
            super.update(deltaTime);

            resetGame();
            transitionArea();
            updateCameraScale(currentArea);
            if (playerRed != null && playerBlue != null) {
                statusGUIRed.draw(getWindow());
                statusGUIBlue.draw(getWindow());
            }

            if (isAreaResetRequired()) {
                resetCurrentArea(currentArea);
            }
        }
    }

    /**
     * Resets the game if requested.
     */
    private void resetGame() {
        final int resetGame = KeyBindings.RESET_GAME;
        if (getWindow().getKeyboard().get(resetGame).isPressed()) {
            begin(getWindow(), getFileSystem());
        }
    }

    /**
     * Makes the players change areas if they need to.
     */
    private void transitionArea() {
        if (playerRed.getIsDoorPassing()) {
            switchArea(playerRed.getDoorPassed());
            playerRed.setIsDoorPassing(false);
        }

        if (playerBlue.getIsDoorPassing()) {
            switchArea(playerBlue.getDoorPassed());
            playerBlue.setIsDoorPassing(false);
        }
    }

    /**
     * Updates the camera scale based on the current area.
     * @param currentArea the current area to apply the camera scale factor.
     */
    private void updateCameraScale(ICoopArea currentArea) {
        if (currentArea == null) {
            throw new NullPointerException("Current area is null");
        }
        float cameraScaleFactor = (float) Math.max(DEFAULT_SCALE_FACTOR, DEFAULT_SCALE_FACTOR * 0.75);
        currentArea.setCameraScaleFactor(cameraScaleFactor);
    }

    /**
     * Determines if the area reset is required.
     * @return true if the area should be reset, false otherwise.
     */
    private boolean isAreaResetRequired() {
        final int resetArea = KeyBindings.RESET_AREA;
        return getWindow().getKeyboard().get(resetArea).isPressed() || playerRed.isDead() || playerBlue.isDead();
    }

    /**
     * Resets the current area (and sets immunity periods to zero).
     */
    private void resetCurrentArea(ICoopArea currentArea) {
        if (currentArea == null) {
            throw new NullPointerException("Current area is null");
        }
        initArea(currentArea.getTitle(), null);
        playerRed.setImmunityPeriod(0);
        playerBlue.setImmunityPeriod(0);
    }

    /**
     * Sets the area named `areaName` as current area in the game ICoop
     * @param areaName (String) title of an area
     */
    private void initArea(String areaName, Door door) {
        if (areaName == null) {
            throw new NullPointerException("areaName is null");
        }

        ICoopArea area = (ICoopArea) setCurrentArea(areaName, true);
        ArrayList<DiscreteCoordinates> coords;

        if (door == null) {
            coords = area.getPlayerSpawnPosition();
        } else {
            coords = door.getDestinationCoordinates();
        }

        playerRed = new ICoopPlayer(area, Orientation.DOWN, coords.getFirst(), Element.FIRE, 1);
        playerBlue = new ICoopPlayer(area, Orientation.DOWN, coords.getLast(), Element.WATER, 2);

        playerRed.enterArea(area, coords.getFirst());
        playerBlue.enterArea(area, coords.getLast());

        centerOfMass = new CenterOfMass(playerRed, playerBlue);
        getCurrentArea().setViewCandidate(centerOfMass);
    }

    /**
     * Switches from one area to the other
     */
    public void switchArea(Door door) {
        if (door == null) {
            throw new NullPointerException("Door is null");
        }
        playerRed.leaveArea();
        playerBlue.leaveArea();

        ICoopArea destArea = (ICoopArea) setCurrentArea(door.getDestinationName(), false);

        DiscreteCoordinates destCoordsRed = door.getDestinationCoordinates().getFirst();
        DiscreteCoordinates destCoordsBlue = door.getDestinationCoordinates().getLast();
        playerRed.enterArea(destArea, destCoordsRed);
        playerBlue.enterArea(destArea, destCoordsBlue);

        getCurrentArea().setViewCandidate(centerOfMass);
    }

    /**
     * Sets the value of the activeDialog.
     * @param dialog (Dialog): dialog to display.
     */
    @Override
    public void publish(Dialog dialog) {
        this.activeDialog = dialog;
    }

    @Override
    public String getTitle() {
        return "ICoop";
    }
}
