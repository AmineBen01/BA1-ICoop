package ch.epfl.cs107.icoop.area;


import ch.epfl.cs107.icoop.ICoopBehavior;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;


/**
 * Base class for all the areas of ICoop
 */
public abstract class ICoopArea extends Area {

    public final static float DEFAULT_SCALE_FACTOR = 13.f;
    private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;

    protected final DialogHandler game;
    private boolean activeDialogCompleted;

    public ICoopArea(DialogHandler game) {
        if (game == null) {
            throw new NullPointerException("game is null");
        }
        this.game = game;
        activeDialogCompleted = false;
    }

    /**
     * @return the player's spawn position in the area
     */
    public abstract ArrayList<DiscreteCoordinates> getPlayerSpawnPosition();

    /**
     * Area specific callback to initialise the instance
     */
    protected abstract void createArea();

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            setBehavior(new ICoopBehavior(window, getTitle(), this));
            createArea();
            return true;
        }
        return false;
    }

    public boolean isActiveDialogCompleted() {
        return activeDialogCompleted;
    }

    public void setActiveDialogCompleted(boolean activeDialogCompleted) {
        this.activeDialogCompleted = activeDialogCompleted;
    }

    /**
     * Getter for ICoop's scale factor
     * @return Scale factor in both the x-direction and the y-direction
     */
    @Override
    public final float getCameraScaleFactor() {
        return cameraScaleFactor;
    }

    /**
     * Setter for ICoop's scale factor
     */
    public final void setCameraScaleFactor(float cameraScaleFactor) {
        this.cameraScaleFactor = cameraScaleFactor;
    }

    @Override
    public boolean isViewCentered() {
        return true;
    }
}
