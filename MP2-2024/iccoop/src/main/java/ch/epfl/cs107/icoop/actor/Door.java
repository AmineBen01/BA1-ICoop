package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Class that allows transitioning to a destination area.
 */
public class Door extends AreaEntity {
    private final String destinationName;
    private Logic signal;
    private RPGSprite sprite;
    private DialogHandler game;

    //DiscreteCoordinates of the red and the blue player in the destination Area (in this order)
    private final ArrayList<DiscreteCoordinates> destinationCoordinates;

    //The other cells the door occupies in addition to currentMainCellCoordinates
    private final ArrayList<DiscreteCoordinates> additionalCoordinates;

    /**
     * @param destinationName (String): Name of the destination Area
     * @param signal (Logic): Signal that indicates under which conditions the door can be opened or not.
     * @param destinationCoordinates (ArrayList<DiscreteCoordinates>): Coordinates of the red and blue player in the destination Area (in this order)
     * @param area (Area): Owner area. Not null
     * @param position (DiscreteCoordinate): Position of the main cell of the door in the Area. Not null
     */
    public Door(String destinationName, Logic signal, ArrayList<DiscreteCoordinates> destinationCoordinates,
                Area area, DiscreteCoordinates position) {

        //we do not draw the door, so the orientation is arbitrary
        super(area, Orientation.DOWN, position);

        if (destinationName == null || signal == null || destinationCoordinates == null) {
            throw new NullPointerException("Null parameter in Door constructor");
        }
        this.destinationName = destinationName;
        this.signal = signal;
        this.destinationCoordinates = destinationCoordinates;
        this.additionalCoordinates = new ArrayList<>();
    }

    /**
     * Door constructor (redefined with additionalCoordinates)
     * Initialize all the coordinates that the door covers (not only the main cell)
     * Same parameters as the first constructor
     * @param additionalCoordinates (DiscreteCoordinates): The other cells the door occupies in addition to currentMainCellCoordinates
     */
    public Door(String destinationName, Logic signal, ArrayList<DiscreteCoordinates> destinationCoordinates,
                Area area, DiscreteCoordinates position, DiscreteCoordinates... additionalCoordinates) {

        //calls the first constructor
        this(destinationName, signal, destinationCoordinates, area, position);

        if (additionalCoordinates == null) {
            throw new NullPointerException("additionalCoordinates is null");
        }
        this.additionalCoordinates.addAll(Arrays.asList(additionalCoordinates));
    }



    /**
     * Special constructor for the "hidden" door of the Arena area.
     * @param spriteName (String): Supplementary parameter for the name of the RGBSprite.
     */
    public Door(String destinationName, Logic signal, ArrayList<DiscreteCoordinates> destinationCoordinates,
                Area area, DiscreteCoordinates position, String spriteName, DiscreteCoordinates... additionalCoordinates) {
        this(destinationName, signal, destinationCoordinates, area, position, additionalCoordinates);
        sprite = new RPGSprite(spriteName, 1, 1, this,
                new RegionOfInterest(0, 0, 32, 32));
    }

    /**
     * Special constructor for the "main" door of the Spawn area.
     * @param game (DialogHandler): Supplementary parameter for publishing the dialogs.
     */
    public Door(String spawn, Logic signal, ArrayList<DiscreteCoordinates> destCoords, Area area, DiscreteCoordinates position, DialogHandler game) {
        this(spawn, signal, destCoords, area, position);
        this.game = game;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        if (signal.isOn() && sprite != null) {
            super.draw(canvas);
            sprite.draw(canvas);
        }
    }

    public Logic getSignal() {
        return signal;
    }

    public void setSignalTrue() {
        signal = Logic.TRUE;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public ArrayList<DiscreteCoordinates> getDestinationCoordinates() {
        return destinationCoordinates;
    }

    /**
     * @return (ArrayList<DiscreteCoordinates>): All the cells that the Door occupies.
     * The currentMainCell is in the first index of the ArrayList returned.
     */
    @Override
    public ArrayList<DiscreteCoordinates> getCurrentCells() {
        ArrayList<DiscreteCoordinates> currentCells = new ArrayList<>(additionalCoordinates);
        currentCells.addFirst(getCurrentMainCellCoordinates());
        return currentCells;
    }

    /**
     * A Door is passable.
     * @return (boolean): false.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * A Door is able to have cell interactions
     * @return (boolean): true.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * A Door is able to have view interactions
     * @return (boolean): false.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * @param v (AreaInteractionVisitor): the visitor
     * @param isCellInteraction (boolean): true, if this is a cell interaction.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this , isCellInteraction);
    }

    public DialogHandler getGame() {
        return game;
    }
}
