package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;


/**
 * A collectable item that serves an element.
 */
public abstract class ElementalItem extends ICoopCollectable implements ElementalEntity, Logic {
    private final Element element;

    /**
     * ElementalItem constructor
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param isCollected (boolean): Initial collected status of this. Not null
     * @param element     (Element): Associated element. Not null
     */
    public ElementalItem(Area area, Orientation orientation, DiscreteCoordinates position, boolean isCollected, Element element) {
        super(area, orientation, position, isCollected);
        if (element == Element.FIRE || element == Element.WATER) {
            this.element = element;
        } else {
            throw new IllegalArgumentException("The element given is not valid !");
        }
    }

    /**
     * @return (boolean): true, if the ElementalItem is collected.
     */
    @Override
    public boolean isOn() {
        return isCollected();
    }

    /**
     * @return (boolean): true, if the ElementalItem is not collected.
     */
    @Override
    public boolean isOff() {
        return !isOn();
    }

    @Override
    public Element getElement() {
        return element;
    }
}
