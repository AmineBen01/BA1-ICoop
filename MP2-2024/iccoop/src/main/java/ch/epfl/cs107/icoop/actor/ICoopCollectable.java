package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

/**
 * A collectable item.
 */
public abstract class ICoopCollectable extends CollectableAreaEntity {

    /**
     * @param area (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the ICoopCollectable. Not null
     * @param position (Coordinate): Initial position of the ICoopCollectable. Not null
     */
    public ICoopCollectable(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
    }

    /**
     * Alternative ICoopCollectable constructor
     * @param area (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the ICoopCollectable. Not null
     * @param position (Coordinate): Initial position of the ICoopCollectable. Not null
     * @param isCollected (boolean): initial collected status of the ICoopCollectable
     */
    public ICoopCollectable(Area area, Orientation orientation, DiscreteCoordinates position, boolean isCollected) {
        super(area, orientation, position, isCollected);
    }

    /**
     * An ICoopCollectable is passable.
     * @return (boolean): false.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * An ICoopCollectable is able to have cell interactions
     * @return (boolean): true.
     */
    @Override
    public boolean isCellInteractable() {
        return !isCollected();
    }


}
