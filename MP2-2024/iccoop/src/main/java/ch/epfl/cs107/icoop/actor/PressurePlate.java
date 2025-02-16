package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;


public class PressurePlate extends AreaEntity implements Logic {
    private Logic signal;
    private final RPGSprite sprite;

    /**
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the PressurePlate in the Area. Not null
     */
    public PressurePlate(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);
        this.signal = Logic.FALSE;
        sprite = new RPGSprite("GroundPlateOff", 1, 1, this);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        sprite.draw(canvas);
    }

    /**
     * @return (boolean): true if the signal is considered as on
     */
    @Override
    public boolean isOn() {
        return signal.isOn();
    }

    /**
     * @return (boolean): true if the signal is considered as off
     */
    @Override
    public boolean isOff() {
        return !isOn();
    }

    public void setSignalTrue() {
        this.signal = Logic.TRUE;
    }

    public void setSignalFalse() {
        this.signal = Logic.FALSE;
    }

    /**
     * Get this PressurePlate's current occupying cells coordinates
     * @return (List of DiscreteCoordinates). May be empty but not null
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * A PressurePlate is passable.
     * @return (boolean): true.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * A PressurePlate is able to have cell interactions
     * @return (boolean): true.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * A PressurePlate is not able to have view interactions.
     * @return (boolean): false.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * @param v (AreaInteractionVisitor): the visitor
     * @param isCellInteraction (boolean): true, if this is a cell interaction
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this , isCellInteraction);
    }
}
