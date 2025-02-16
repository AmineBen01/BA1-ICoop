package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.icoop.actor.wall.ElementalWall;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;


public class Explosive extends ICoopCollectable implements Interactor {
    private boolean activated;
    private int countdown;
    private final ExplosiveInteractionHandler interactionHandler = new ExplosiveInteractionHandler();
    private Animation animation;
    private final int END_EXPLOSION = -10;

    /**
     * @param area (Area): Owner area. Not null
     * @param position (Coordinate): Initial position of the Explosive. Not null
     */
    public Explosive(Area area, DiscreteCoordinates position) {
        super(area, Orientation.UP, position);
        countdown = 30;
        activated = false;
    }

    @Override
    public void update(float deltaTime) {
        if (activated && !isExploding() && animation != null) {
            animation.update(deltaTime);
            countdown -= 1;
        }
        if (countdown == END_EXPLOSION) {
            getOwnerArea().unregisterActor(this);
        }
        if (isCollected()) {
            getOwnerArea().unregisterActor(this);
        }
        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        int ANIMATION_DURATION = 24;
        if (countdown > 0) {
            drawAnimation(canvas, "icoop/explosive", 2, 16, 16,
                    ANIMATION_DURATION / 2, true);
        } else if (isExploding()) {
            drawAnimation(canvas, "icoop/explosion", 7, 32, 32,
                    ANIMATION_DURATION / 7, false);
            countdown--;
        }
    }

    /**
     * Helper method for the drawing of the correct animation.
     */
    private void drawAnimation(Canvas canvas, String spritePath, int frames, int width, int height, int duration, boolean looping) {
        animation = new Animation(spritePath, frames, 1, 1, this, width, height, duration, looping);
        animation.draw(canvas);
    }

    public void setActivated() {
        activated = true;
    }

    /**
     * The explosive explodes directly.
     */
    public void explode() {
        setActivated();
        countdown = 0;
    }

    public boolean isExploding() {
        return countdown <= 0 && countdown >= END_EXPLOSION;
    }

    /**
     * An Explosive is passable.
     * @return (boolean): false.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * @return (boolean): true if the Explosive it's not activated and has not exploded.
     */
    @Override
    public boolean isCellInteractable() {
        return (!activated && !isExploding());
    }

    /**
     * @return (boolean): true if the Explosive hasn't exploded
     */
    @Override
    public boolean isViewInteractable() {
        return countdown > 0;
    }

    /**
     * @param v (AreaInteractionVisitor) : the visitor
     * @param isCellInteraction (boolean): true, if this is a cell interaction
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * @return (boolean): true if the Explosive is exploding.
     */
    @Override
    public boolean wantsCellInteraction() {
        return isExploding();
    }

    /**
     * @return (boolean): true if the Explosive is exploding.
     */
    @Override
    public boolean wantsViewInteraction() {
        return isExploding();
    }

    /**
     * Do this Explosive interact with the given Interactable
     * @param other (Interactable). Not null
     * @param isCellInteraction (boolean): true, if this is a cell interaction
     */
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    /**
     * Get this Explosive's current occupying cells coordinates
     * @return (List of DiscreteCoordinates). May be empty but not null
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Get the Explosive's current field of view cells coordinates.
     * @return List of DiscreteCoordinates (may be empty but not null).
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates mainCellCoords = getCurrentCells().getFirst();
        if (mainCellCoords == null) {
            return Collections.emptyList();
        }
        return List.of(
                mainCellCoords,
                new DiscreteCoordinates(mainCellCoords.x + 1, mainCellCoords.y), // Right
                new DiscreteCoordinates(mainCellCoords.x - 1, mainCellCoords.y), // Left
                new DiscreteCoordinates(mainCellCoords.x, mainCellCoords.y + 1), // Up
                new DiscreteCoordinates(mainCellCoords.x, mainCellCoords.y - 1)  // Down
        );
    }

    private class ExplosiveInteractionHandler implements ICoopInteractionVisitor {

        /**
         * Default interaction between an Explosive and a Rock
         * @param other (Rock): Rock to interact with, not null
         */
        @Override
        public void interactWith(Rock other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("door is null");
            }
            if (otherIsInFieldOfView(getFieldOfViewCells(), other)) {
                other.setBroken();
            }
        }

        /**
         * @param fieldOfViewCells (List<DiscreteCoordinates>)
         * @param other (AreaEntity)
         * @return (boolean): true, if the AreaEntity other, is in the given fieldOfViewCells
         */
        private boolean otherIsInFieldOfView(List<DiscreteCoordinates> fieldOfViewCells, AreaEntity other) {
            for (DiscreteCoordinates fieldOfViewCell : fieldOfViewCells) {
                if (other.getCurrentCells().getFirst().equals(fieldOfViewCell)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Default interaction between an Explosive and an ICoopPlayer
         * @param other (ICoopPlayer): ICoopPlayer to interact with, not null
         */
        @Override
        public void interactWith(ICoopPlayer other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("ICoopPlayer is null");
            }
            if (otherIsInFieldOfView(getFieldOfViewCells(), other)) {
                other.damage(2, Element.PHYSICAL);
            }
        }

        /**
         * Default interaction between an Explosive and an ElementalWall
         * @param other (ElementalWall): ElementalWall to interact with, not null
         */
        @Override
        public void interactWith(ElementalWall other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("ICoopPlayer is null");
            }
            if (!isCellInteraction && otherIsInFieldOfView(getFieldOfViewCells(), other) && isExploding()) {
                getOwnerArea().unregisterActor(other);
            }
        }

        /**
         * Default interaction between an Explosive and an Explosive
         * @param other (Explosive): Explosive to interact with, not null
         */
        @Override
        public void interactWith(Explosive other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("ICoopPlayer is null");
            }
            if (!isCellInteraction && otherIsInFieldOfView(getFieldOfViewCells(), other) && isExploding()) {
                getOwnerArea().unregisterActor(other);
            }
        }
    }
}

