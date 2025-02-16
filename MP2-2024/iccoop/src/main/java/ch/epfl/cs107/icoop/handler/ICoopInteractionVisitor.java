package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.icoop.ICoopBehavior.ICoopCell;
import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.icoop.actor.wall.ElementalWall;
import ch.epfl.cs107.icoop.actor.PressurePlate;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;

/**
 * InteractionVisitor for the ICoop entities
 */

public interface ICoopInteractionVisitor extends AreaInteractionVisitor {
    /// Add Interaction method with all non Abstract Interactable
    default void interactWith(ICoopCell other, boolean isCellInteraction) {}
    default void interactWith(ICoopPlayer other, boolean isCellInteraction) {}
    default void interactWith(Door other, boolean isCellInteraction) {}
    default void interactWith(Rock other, boolean isCellInteraction) {}
    default void interactWith(Explosive other, boolean isCellInteraction) {}
    default void interactWith(Orb other, boolean isCellInteraction) {}
    default void interactWith(ElementalWall other, boolean isCellInteraction) {}
    default void interactWith(Heart other, boolean isCellInteraction) {}
    default void interactWith(PressurePlate other, boolean isCellInteraction) {}
    default void interactWith(BombFoe other, boolean isCellInteraction) {}
    default void interactWith(Ball other, boolean isCellInteraction) {}
    default void interactWith(Staff other, boolean isCellInteraction) {}
    default void interactWith(HellSkull other, boolean isCellInteraction) {}
    default void interactWith(Key other, boolean isCellInteraction) {}
}
