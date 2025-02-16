package ch.epfl.cs107.icoop.actor.wall;


import ch.epfl.cs107.icoop.actor.Element;
import ch.epfl.cs107.icoop.actor.ElementalEntity;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.Collections;
import java.util.List;


/**
 * Abstract class representing a Wall serving an Element.
 */
public abstract class ElementalWall extends AreaEntity implements ElementalEntity, Interactor {

    //those attributes are shared across the wall package without breaking the encapsulation
    protected Sprite sprite;
    protected Logic signal;
    protected Element element;
    protected boolean alwaysOn;

    private final ElementalWallInteractionHandler interactionHandler = new ElementalWallInteractionHandler();

    /**
     * @param area (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the ElementalWall in the Area. Not null
     * @param position (DiscreteCoordinate): Initial position of the ElementalWall in the Area. Not null
     * @param signal (Logic): Signal of the ElementalWall. Not null
     * @param alwaysOn (boolean): If this ElementalWall can be shut down or not.
     */
    public ElementalWall(Area area, Orientation orientation, DiscreteCoordinates position, Logic signal, boolean alwaysOn) {
        super(area, orientation, position);
        if (signal == null) {
            throw new NullPointerException("signal is null");
        }
        this.signal = signal;
        this.alwaysOn = alwaysOn;
    }

    public void setSignal(Logic signal) {
        if (signal == null) {
            throw new NullPointerException("signal is null");
        }
        if (!alwaysOn && signal != this.signal) {
            this.signal = signal;
        }
    }

    @Override
    public Element getElement() {
        return element;
    }

    /**
     * Tell if an ICoopPlayer can enter the ElementalWall or not.
     * @param otherPlayer (ICoopPlayer): The ICoopPlayer entering in the ElementalWall.
     * @return (boolean): true, if the element of the player entering is the same as this ElementalWall.
     */
    public boolean canEnter(ICoopPlayer otherPlayer) {
        if (otherPlayer == null) {
            throw new NullPointerException("otherPlayer is null");
        }
        return otherPlayer.getElement() == element || signal.isOff();
    }

    /**
     * An ElementalWall wants cell interaction.
     * @return (boolean): true.
     */
    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    /**
     * An ElementalWall doesn't want view interaction.
     * @return (boolean): false.
     */
    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    /**
     * Do this ElementalWall interact with the given Interactable
     * @param other (Interactable). Not null
     * @param isCellInteraction True if this is a cell interaction
     */
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    /**
     * Get this ElementalWall's current occupying cells coordinates
     * @return (List of DiscreteCoordinates). May be empty but not null
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Get this ElementalWall's current field of view cells coordinates
     * @return (List of DiscreteCoordinates). May be empty but not null
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * An ElementalWall is passable in the general case.
     * @return (boolean): false.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * An ElementalWall is cell interactable.
     * @return (boolean): true.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * The view interaction has been implemented through its signal.
     * @return (boolean): false.
     */
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    /**
     * Call directly the interaction on the ElementalWall if accepted
     * @param v (AreaInteractionVisitor) : the visitor
     * @param isCellInteraction (boolean): true, if this is a cell interaction
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * Handles all the interactions of this ElementalWall (Interactor)
     */
    private class ElementalWallInteractionHandler implements ICoopInteractionVisitor {

        /**
         * Default interaction between an ElementalWall and an ICoopPlayer
         * @param other (Interactable): interactable to interact with, not null
         * @param isCellInteraction (boolean): true, if this is a cell interaction.
         */
        @Override
        public void interactWith(ICoopPlayer other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("ICoopPlayer is null");
            }
            if (signal.isOn() && isCellInteraction) {
                other.damage(1, getElement());
            }
        }
    }
}
