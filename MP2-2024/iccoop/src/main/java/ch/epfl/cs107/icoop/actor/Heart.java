package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Gives the ability to gain some heartPoints by collecting it.
 */
public class Heart extends ICoopCollectable {

    //Static because the HP earned are the same for all hearts.
    //Final preserves the encapsulation. (public for accessing it)
    public final static int HEART_POINTS = 5;
    private final Animation animation;

    /**
     * Default CollectableAreaEntity constructor
     * @param area        (Area): Owner area. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public Heart(Area area, DiscreteCoordinates position) {
        super(area, Orientation.UP, position);

        int ANIMATION_DURATION = 24;
        animation = new Animation("icoop/heart", 4, 1, 1, this,
                16, 16, ANIMATION_DURATION/4, true);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        animation.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
        if (isCollected()) {
            getOwnerArea().unregisterActor(this);
        }
    }

    /**
     * Get this Heart's current occupying cells coordinates
     * @return (List of DiscreteCoordinates). May be empty but not null
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * A Heart is not view interactable.
     * @return (boolean): false.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * @param v (AreaInteractionVisitor): the visitor
     * @param isCellInteraction (boolean): true if this is a cell interaction
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this , isCellInteraction);
    }
}
