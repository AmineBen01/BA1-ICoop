package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;


public class Staff extends ElementalItem {
    private final Animation animation;

    /**
     * Staff constructor
     * @param area        (Area): Owner area. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param element     (Element): Associated element. Not null
     */
    public Staff(Area area, DiscreteCoordinates position, Element element) {
        super(area, Orientation.UP, position, false, element);
        int ANIMATION_DURATION = 32;
        String staff_name;
        if (element == Element.FIRE) {
            staff_name = "icoop/staff_fire";
        } else {
            staff_name = "icoop/staff_water";
        }
        animation = new Animation(staff_name, 8, 2, 2, this , 32, 32,
                new Vector(-0.5f, 0),
                ANIMATION_DURATION/8, true);
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

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this , isCellInteraction);
    }
}
