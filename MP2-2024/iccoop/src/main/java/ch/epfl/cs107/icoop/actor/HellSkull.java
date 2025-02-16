package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.random.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;


public class HellSkull extends Foe {
    //Static because all HellSkull make the same damages.
    //Final to preserve the encapsulation. (public for the accessing).
    public final static int DAMAGE = 1;
    private final OrientedAnimation animation;
    private final float MAX;
    private final float MIN;
    private final HellSkullInteractionHandler interactionHandler = new HellSkullInteractionHandler();
    private float randomFloat;

    /**
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public HellSkull(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, 10);
        setVulnerableElement(Element.PHYSICAL);
        setVulnerableElement(Element.WATER);

        final int ANIMATION_DURATION = 12;
        Orientation[] orders = new Orientation []{ Orientation.UP, Orientation.LEFT , Orientation.DOWN , Orientation.RIGHT};
        animation = new OrientedAnimation("icoop/flameskull", ANIMATION_DURATION/3, this, new Vector(-0.5f, -0.5f), orders , 3, 2, 2, 32, 32, true);

        MIN = 0.5f;
        MAX = 2.f;
        randomFloat = RandomGenerator.getInstance().nextFloat(MIN , MAX);
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

        randomFloat -= deltaTime;

        if (randomFloatFinished()) {
            Fire fire = new Fire(getCurrentArea(), Orientation.RIGHT, new DiscreteCoordinates((int) getPosition().x + 1, (int) getPosition().y));
            getCurrentArea().enterAreaCells(fire, getFieldOfViewCells());
            getCurrentArea().registerActor(fire);
            randomFloat = RandomGenerator.getInstance().nextFloat(MIN , MAX);
        }

        leaveArea();
    }

    private boolean randomFloatFinished() {
        return randomFloat <= 0.f;
    }

    /**
     * @param damage (int): How much damage the BombFoe will take if it is vulnerable to the `element`.
     * @param element (Element): Element of the damage.
     */
    public void damage(int damage, Element element) {
        if (element == Element.WATER || element == Element.PHYSICAL) {
            decreaseLife(damage);
        }
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this , isCellInteraction);
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    private class HellSkullInteractionHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(ICoopPlayer other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("ICoopPlayer is null");
            }
            if (isCellInteraction) {
                other.damage(DAMAGE, Element.FIRE);
            }
        }
    }
}
