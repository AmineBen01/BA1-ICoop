package ch.epfl.cs107.icoop.actor.wall;


import ch.epfl.cs107.icoop.actor.Element;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;


public class FireWall extends ElementalWall {

    Sprite[] wallSpritesFire = RPGSprite.extractSprites("fire_wall", 4, 1F, 1F,
            this, Vector.ZERO , 256, 256);

    /**
     * @param area (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the ElementalWall in the Area. Not null
     * @param position (DiscreteCoordinate): Initial position of the ElementalWall in the Area. Not null
     * @param signal (Logic): Signal of the ElementalWall. Not null
     * @param alwaysOn (boolean): If this ElementalWall can be shut down or not.
     */
    public FireWall(Area area, Orientation orientation, DiscreteCoordinates position, Logic signal, boolean alwaysOn) {
        super(area, orientation, position, signal, alwaysOn);
        this.sprite = wallSpritesFire[orientation.ordinal()];
        this.element = Element.FIRE;
    }

    @Override
    public void draw(Canvas canvas) {
        if (signal.isOn()) {
            super.draw(canvas);
            sprite.draw(canvas);
        }
    }
}
