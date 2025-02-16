package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;


public abstract class Projectile extends Foe {
    private final static int MOVE_DURATION = 5;
    private final int maxDistance;
    private int distanceFromStart;
    private final int speed;

    /**
     * Default Projectile constructor
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param maxDistance (int): Max distance this projectile can reach.
     */
    public Projectile(Area area, Orientation orientation, DiscreteCoordinates position, int maxDistance) {
        super(area, orientation, position, 0);
        this.distanceFromStart = 0;
        this.maxDistance = maxDistance;
        this.speed = 1;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (hasNotArrived()) {
            move(MOVE_DURATION / speed);
            incrementDistanceFromStart();
        } else {
            leaveArea();
        }
    }

    public boolean hasNotArrived() {
        return distanceFromStart != maxDistance;
    }

    public void incrementDistanceFromStart() {
        distanceFromStart += 1;
    }

    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    @Override
    public boolean wantsCellInteraction() {
        return hasNotArrived();
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }
}
