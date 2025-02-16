package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.List;


public abstract class ICoopCharacter extends MovableAreaEntity implements Interactor {
    private final int life;
    private boolean immune;
    private int immunityPeriod;
    private ICoopArea currentArea;
    private Keyboard keyboard;
    private final ICoopCharacterInteractionHandler interactionHandler = new ICoopCharacterInteractionHandler();

    /**
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param life        (int): Initial life of the ICoopCharacter.
     */
    public ICoopCharacter(Area area, Orientation orientation, DiscreteCoordinates position, int life) {
        super(area, orientation, position);

        this.life = life;
        immune = false;
        immunityPeriod = 24;
    }

    @Override
    public void update(float deltaTime) {
        currentArea = (ICoopArea) getOwnerArea();
        keyboard = currentArea.getKeyboard();
        super.update(deltaTime);
    }

    public abstract boolean isDead();

    public boolean isImmune() {
        return immune;
    }

    public void setImmune(boolean immune) {
        this.immune = immune;
    }

    public boolean immunityPeriodIsOn() {
        return immunityPeriod > 0;
    }

    public void decrementImmunityPeriod() {
        if (immunityPeriod > 0) {
            immunityPeriod -= 1;
        }
    }

    public boolean immunityPeriodIsPair() {
        return immunityPeriod % 2 == 0;
    }

    public void setImmunityPeriod(int immunityPeriod) {
        if (immunityPeriod >= 0) {
            this.immunityPeriod = immunityPeriod;
        } else {
            throw new IllegalArgumentException("immunityPeriod must be >= 0");
        }
    }

    public ICoopArea getCurrentArea() {
        return currentArea;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public abstract void leaveArea();

    /**@return (boolean): true because an ICoopPlayer is able to have cell interactions*/
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**@return (boolean): true because an ICoopPlayer is able to have view interactions*/
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    /**
     * Get this ICoopPlayer's current field of view cells coordinates
     * @return (List of DiscreteCoordinates). May be empty but not null
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    /**
     * @return (boolean): true, an ICoopPlayer always wants cell interaction
     */
    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    public int getLife() {
        return life;
    }

    private class ICoopCharacterInteractionHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(Ball other, boolean isCellInteractable) {
            if (isCellInteractable) {
                other.leaveArea();
            }
        }
    }
}
