package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;


/**
 * Obstacle
 */
public class Obstacle extends AreaEntity implements Actor {
    private final Sprite sprite;

    /**
     * @param area (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     * @param spriteName (String): Name of the sprite. Not null
     */
    public Obstacle(Area area, Orientation orientation, DiscreteCoordinates position, String spriteName) {
        super(area, orientation, position);
        if (spriteName == null) {
            throw new NullPointerException("Sprite name cannot be null");
        }
        sprite = new Sprite(spriteName, 1.f, 1.f, this);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        sprite.draw(canvas);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * An Obstacle is not passable.
     * @return (boolean): true
     */
    @Override
    public boolean takeCellSpace() {
        return true;
    }

    /**
     * An Obstacle is able to have cell interactions
     * @return (boolean): true.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * An Obstacle is able to have view interactions
     * @return (boolean): true.
     */
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
    }
}
