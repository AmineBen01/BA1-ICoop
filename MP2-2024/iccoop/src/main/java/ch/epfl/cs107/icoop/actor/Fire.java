package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;


public class Fire extends Projectile implements Unstoppable {
    private final Animation animation;
    private final FireInteractionHandler interactionHandler = new FireInteractionHandler();

    /**
     * Default Fire constructor
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public Fire(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, 31);
        animation = new Animation("icoop/fire", 7, 1, 1, this , 16, 16, 4, true);
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

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
    }

    /**
     * @return (boolean): true, a Fire is unstoppable.
     */
    @Override
    public boolean unstop() {
        return true;
    }

    private static class FireInteractionHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(ICoopPlayer other, boolean isCellInteraction) {
            if (isCellInteraction) {
                other.damage(5, Element.FIRE);
            }
        }

        @Override
        public void interactWith(Explosive other, boolean isCellInteraction) {
            if (isCellInteraction) {
                other.explode();
            }
        }
    }
}
