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

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.cs107.play.math.Orientation.*;


public class BombFoe extends Foe {
    private final BombFoeInteractionHandler interactionHandler = new BombFoeInteractionHandler();

    //They are static because they are the same for all BombFoes.
    //To preserve encapsulation, they are also final. (public to access them)
    public final static int MAX_VIEW_RANGE = 9;
    public final static int MAX_INACTIVE_TIMER = 50;

    private int inactiveTimer;
    private State currentState;
    private OrientedAnimation animation;
    private final int ANIMATION_DURATION = 24;
    private final Vector anchor = new Vector(-0.5f, 0);
    private final Orientation[] orders = {DOWN , RIGHT , UP, LEFT};
    private ICoopPlayer target;
    private int speedFactor;

    /**
     * @param area        (Area): Owner area. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public BombFoe(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position, 10);
        setVulnerableElement(Element.PHYSICAL);
        setVulnerableElement(Element.FIRE);
        currentState = State.IDLE;

        animation = new OrientedAnimation("icoop/bombFoe", ANIMATION_DURATION/3, this,
                anchor , orders , 4, 2, 2, 32, 32, true);
        speedFactor = 2;
        inactiveTimer = 0;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        animation.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        int randomInt = RandomGenerator.getInstance().nextInt(4);
        double randDouble = RandomGenerator.getInstance().nextDouble();

        if (inactiveTimer > 0) {
            inactiveTimer -= 1;
        } else {
            currentState = State.IDLE;
        }

        if (target != null) {
            currentState = State.ATTACKING;
        }

        switch (currentState) {
            case IDLE:
                speedFactor = 2;
                if (!isDisplacementOccurs()) {
                    orientateRandom(randDouble, randomInt);
                    move(ANIMATION_DURATION / speedFactor);
                    animation.reset();
                } else {
                    animation.update(deltaTime);
                }
                if (randDouble < 0.4) {
                    inactiveTimer = RandomGenerator.getInstance().nextInt(MAX_INACTIVE_TIMER);
                    currentState = State.NOTHING;
                }
                break;

            case ATTACKING:
                Vector vectorToTarget = target.getPosition().sub(getPosition());
                float deltaX = vectorToTarget.x;
                float deltaY = vectorToTarget.y;
                boolean check;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    check = orientate(Orientation.fromVector(new Vector(deltaX, 0)));
                } else {
                    check = orientate(Orientation.fromVector(new Vector(0, deltaY)));
                }
                if (!check && speedFactor > 0) {
                    speedFactor = 5;
                    move(ANIMATION_DURATION / speedFactor);
                }
                if (targetIsInReach()) {
                    speedFactor = 0;
                    Explosive explosive = new Explosive(getOwnerArea(), getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
                    if (getOwnerArea().canEnterAreaCells(explosive, explosive.getCurrentCells())) {
                        speedFactor = 0;
                        getOwnerArea().registerActor(explosive);
                        explosive.setActivated();
                        target = null;
                        currentState = State.PROTECTING;
                    }
                }
                break;

            case PROTECTING:
                animation = new OrientedAnimation("icoop/bombFoe.protecting",
                        ANIMATION_DURATION/3, this , anchor , orders , 4, 2,
                        2, 32, 32, false);
                currentState = State.IDLE;
                break;
        }
        leaveArea();
        super.update(deltaTime);
        animation.update(deltaTime);
    }

    /**
     * Rotates the BombFoe in a random orientation.
     * @param randDouble (double): a random double number.
     * @param randomInt  (int) a random int number.
     */
    private void orientateRandom(double randDouble, int randomInt) {
        if (randDouble < 0.4) {
            switch (randomInt) {
                case 0:
                    orientate(Orientation.UP);
                    break;
                case 1:
                    orientate(Orientation.DOWN);
                    break;
                case 2:
                    orientate(Orientation.LEFT);
                    break;
                case 3:
                    orientate(Orientation.RIGHT);
                    break;
            }
        }
    }

    /**
     * @return (boolean): true, if the target (ICoopPlayer) is less than 3 discrete coordinates away.
     */
    private boolean targetIsInReach() {
        if (target == null) {
            return false;
        } else {
            double distanceBetween = DiscreteCoordinates.distanceBetween(getCurrentMainCellCoordinates(), target.getCurrentMainCellCoordinates());
            return distanceBetween < 3;
        }
    }

    /**
     * @param damage (int): How much damage the BombFoe will take if it is vulnerable to the `element`.
     * @param element (Element): Element of the damage.
     */
    public void damage(int damage, Element element) {
        if (element == Element.FIRE || element == Element.PHYSICAL) {
            decreaseLife(damage);
        }
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return (currentState == State.IDLE || currentState == State.ATTACKING);
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> coordinates = new ArrayList<>();
        DiscreteCoordinates frontCoordinate = getCurrentMainCellCoordinates().jump(getOrientation().toVector());
        coordinates.add(frontCoordinate);
        switch (getOrientation()) {
            case UP:
                for (int i = 0; i < MAX_VIEW_RANGE; ++i) {
                    coordinates.add(new DiscreteCoordinates(frontCoordinate.x, frontCoordinate.y + i));
                }
                break;

            case DOWN:
                for (int i = 0; i < MAX_VIEW_RANGE; ++i) {
                    coordinates.add(new DiscreteCoordinates(frontCoordinate.x, frontCoordinate.y - i));
                }
                break;

            case LEFT:
                for (int i = 0; i < MAX_VIEW_RANGE; ++i) {
                    coordinates.add(new DiscreteCoordinates(frontCoordinate.x - i, frontCoordinate.y));
                }
                break;

            case RIGHT:
                for (int i = 0; i < MAX_VIEW_RANGE; ++i) {
                    coordinates.add(new DiscreteCoordinates(frontCoordinate.x + i, frontCoordinate.y));
                }
                break;
        }
        return coordinates;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this , isCellInteraction);
    }

    private class BombFoeInteractionHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(ICoopPlayer other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("ICoopPlayer is null");
            }
            if (target == null && otherIsInFieldOfView(getFieldOfViewCells(), other)) {
                target = other;
                currentState = State.ATTACKING;
            }
        }

        /**
         * @param fieldOfViewCells (List<DiscreteCoordinates>): Field of view of the BombFoe.
         * @param other (ICoopPlayer): target.
         * @return (boolean): true, if the target is in the field of view of the BombFoe.
         */
        private boolean otherIsInFieldOfView(List<DiscreteCoordinates> fieldOfViewCells, ICoopPlayer other) {
            for (DiscreteCoordinates f : fieldOfViewCells) {
                if (other.getCurrentCells().getFirst().equals(f)) {
                    return true;
                }
            }
            return false;
        }
    }
}
