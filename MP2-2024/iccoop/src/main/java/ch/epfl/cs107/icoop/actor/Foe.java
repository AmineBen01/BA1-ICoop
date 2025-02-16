package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;

public abstract class Foe extends ICoopCharacter implements Interactor {
    private int life;
    private final Animation deadAnimation;
    private final ArrayList<Element> vulnerableElements;

    /**
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param life        (int): Initial life of the Foe.
     */
    public Foe(Area area, Orientation orientation, DiscreteCoordinates position, int life) {
        super(area, orientation, position, 0);

        this.life = life;
        final int ANIMATION_DURATION = 24;
        deadAnimation = new Animation("icoop/vanish", 7, 2, 2, this , 32, 32, new
                Vector(-0.5f, 0f), ANIMATION_DURATION/7, false);
        vulnerableElements = new ArrayList<>();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public boolean isDead() {
        return life <= 0;
    }

    @Override
    public void leaveArea() {
        if (isDead()) {
            getOwnerArea().unregisterActor(this);
        }
    }

    public void decreaseLife(int amount) {
        life -= amount;
    }

    @Override
    public boolean takeCellSpace() {
        return !isDead();
    }

    public void setVulnerableElement(Element vulnerableElement) {
        this.vulnerableElements.add(vulnerableElement);
    }
}
