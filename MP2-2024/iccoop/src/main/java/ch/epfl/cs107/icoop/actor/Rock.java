package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;


public class Rock extends Obstacle {
    private boolean broken;

    /**
     * Rock constructor
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Rock(Area area, DiscreteCoordinates position) {
        super(area, Orientation.UP, position, "rock.1");
        broken = false;
    }

    public void setBroken() {
        broken = true;
        getOwnerArea().unregisterActor(this);
    }

    /**
     * Can be drawn only if the rock is not broken.
     * @param canvas target, not null
     */
    @Override
    public void draw(Canvas canvas) {
        if (!broken) {
            super.draw(canvas);
        }
    }

    /**
     * A Rock is passable if it's broken
     * @return (boolean): true if not broken.
     */
    @Override
    public boolean takeCellSpace() {
        return !broken;
    }

    /**
     * @param v (AreaInteractionVisitor) : the visitor
     * @param isCellInteraction (boolean): true, if this is a cell interaction.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this , isCellInteraction);
    }

    /**
     * Get this Rock's current occupying cells coordinates
     * @return (List of DiscreteCoordinates). May be empty but not null
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
}
