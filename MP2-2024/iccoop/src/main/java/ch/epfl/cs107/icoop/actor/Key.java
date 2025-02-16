package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;


public class Key extends ElementalItem implements Logic {
    private final Sprite sprite;
    Logic signal;

    /**
     * ElementalItem constructor
     * @param area        (Area): Owner area. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param element     (Element): Associated element. Not null
     */
    public Key(Area area, DiscreteCoordinates position, Element element) {
        super(area, Orientation.UP, position, false, element);
        String spriteName;
        if (element == Element.FIRE) {
            spriteName = "icoop/key_red";
        } else {
            spriteName = "icoop/key_blue";
        }
        sprite = new Sprite(spriteName, 0.6f, 0.6f, this);
        signal = Logic.FALSE;
    }

    @Override
    public void draw(Canvas canvas) {
        if (signal.isOff()) {
            super.draw(canvas);
            sprite.draw(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    public void setSignalTrue() {
        signal = Logic.TRUE;
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
