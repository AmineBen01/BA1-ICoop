package ch.epfl.cs107.icoop.actor;
/*
 *	Author:      Amine Benlaroui
 *	Date:        10 Dec 2024
 */


import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;


public class Orb extends ElementalItem {
    private final DialogHandler game;
    private final Animation animation;

    /**
     * @param area        (Area): Owner area. Not null
     * @param position    (Coordinate): Initial position of the Orb. Not null
     * @param isCollected (boolean): Initial collected status of the Orb. Not null
     * @param element     (Element): Associated element. Not null
     * @param game        (DialogHandler): Associated game for publishing dialogs. Not null.
     */
    public Orb(Area area, DiscreteCoordinates position, boolean isCollected, Element element, DialogHandler game) {
        super(area, Orientation.UP, position, isCollected, element);

        if (game == null) {
            throw new NullPointerException("game is null");
        }
        this.game = game;

        final int ANIMATION_FRAMES = 6;
        int spriteYDelta = -1;
        if (getElement() == Element.FIRE) {
            spriteYDelta = 64;
        }
        else if (getElement() == Element.WATER) {
            spriteYDelta = 0;
        }
        Sprite[] sprites = new Sprite[ANIMATION_FRAMES];
        for (int i = 0; i < ANIMATION_FRAMES; i++) {
            sprites[i] = new RPGSprite("icoop/orb", 1, 1, this ,
                    new RegionOfInterest(i * 32, spriteYDelta , 32, 32));
        }

        final int ANIMATION_DURATION = 24;
        animation = new Animation(ANIMATION_DURATION / ANIMATION_FRAMES , sprites);
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

    public DialogHandler getGame() {
        return game;
    }

    /**
     * Get this Orb's current occupying cells coordinates
     * @return (List of DiscreteCoordinates). May be empty but not null
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * An Orb is not able to have view interactions.
     * @return (boolean): false.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * @param v (AreaInteractionVisitor): the visitor
     * @param isCellInteraction (boolean): true, if this is a cell interaction
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this , isCellInteraction);
    }
}
