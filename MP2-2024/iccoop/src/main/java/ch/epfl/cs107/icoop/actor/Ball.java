package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class Ball extends Projectile implements Unstoppable {
    private final Element element;
    private final Animation animation;
    private final BallInteractionHandler interactionHandler = new BallInteractionHandler();

    /**
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param element     (Element): Element of this Ball. WATER or FIRE.
     */
    public Ball(Area area, Orientation orientation, DiscreteCoordinates position, Element element) {
        super(area, orientation, position, 40);
        if (element == Element.FIRE || element == Element.WATER) {
            this.element = element;
        } else {
            throw new IllegalArgumentException("The element given is not a valid parameter");
        }

        int ANIMATION_DURATION = 12;
        String spriteName;
        if (element == Element.WATER) {
            spriteName = "icoop/magicWaterProjectile";
        } else {
            spriteName = "icoop/magicFireProjectile";
        }
        animation = new Animation(spriteName , 4, 1, 1, this , 32, 32,
                ANIMATION_DURATION/4, true);
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
    }

    /**
     * @return (boolean): true, a Projectile is Unstoppable.
     */
    @Override
    public boolean unstop() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this , isCellInteraction);
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    private class BallInteractionHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(BombFoe other, boolean isCellInteraction) {
            if (isCellInteraction) {
                other.damage(10, element);
                leaveArea();
            }
        }

        @Override
        public void interactWith(Explosive other, boolean isCellInteraction) {
            if (isCellInteraction) {
                other.explode();
                leaveArea();
            }
        }

        @Override
        public void interactWith(Rock other, boolean isCellInteraction) {
            if (isCellInteraction) {
                other.setBroken();
                leaveArea();
            }
        }
    }
}
