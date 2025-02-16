package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.KeyBindings;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopInventory;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;


import static ch.epfl.cs107.play.math.Orientation.*;


/**
 * An ICoopPlayer is a player of the ICoop game.
 */
public class ICoopPlayer extends ICoopCharacter implements ElementalEntity, Interactor {

    //Static because the duration of a movement is the same for all ICoopPlayers
    //Final to preserve the encapsulation. (public for accessing it)
    public final static int MOVE_DURATION = 4;
    private final Element element;
    private final KeyBindings.PlayerKeyBindings keys;
    private final ICoopPlayerInteractionHandler interactionHandler = new ICoopPlayerInteractionHandler();
    private boolean isDoorPassing;
    private Door doorPassed;
    private final Health health;
    private Element invulnerableElement;
    private PressurePlate pressurePlate;
    private final ICoopInventory inventory;
    private ICoopItem currentItem;
    private State currentState;
    private OrientedAnimation currentAnimation;
    private final OrientedAnimation idleAnimation;
    private final OrientedAnimation swordAnimation;
    private final OrientedAnimation staffAnimation;
    private boolean idleAffected;
    private boolean swordAffected;
    private boolean staffAffected;

    /**
     * @param owner (Area) area to which the player belong
     * @param orientation (Orientation) the initial orientation of the player
     * @param coordinates (DiscreteCoordinates) the initial position in the grid
     * @param element (Element) natural element type of the player. Not null
     * @param playerIndex (int) name of the player (1 for icoop/player and 2 for icoop/player2)
     */
    public ICoopPlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates, Element element, int playerIndex) {
        super(owner, orientation, coordinates, 80);
        resetMotion();

        if (element == null) {
            throw new NullPointerException("Element is null");
        }
        this.element = element;

        final int ANIMATION_DURATION = 4;
        final Vector anchor = new Vector(0, 0);
        final Orientation[] orders = {DOWN , RIGHT , UP, LEFT};
        String[] playerNames = {"icoop/player", "icoop/player2"};

        this.idleAnimation = new OrientedAnimation(playerNames[playerIndex - 1], ANIMATION_DURATION , this ,
                anchor, orders, 4, 1, 2, 16, 32, true);

        currentAnimation = idleAnimation;

        idleAffected = false;

        this.keys = (playerIndex == 1) ? KeyBindings.RED_PLAYER_KEY_BINDINGS : KeyBindings.BLUE_PLAYER_KEY_BINDINGS;
        health = new Health(this , Transform.I.translated(0, 1.75f), getLife(), true);
        isDoorPassing = false;
        invulnerableElement = null;

        inventory = new ICoopInventory();
        inventory.addPocketItem(ICoopItem.Sword, 1);
        inventory.addPocketItem(ICoopItem.Explosive, 1);
        currentItem = ICoopItem.Sword;
        currentState = State.IDLE;

        final int SWORD_ANIMATION_DURATION = 2;
        final Orientation[] swordOrders = {DOWN , UP , RIGHT, LEFT};
        final Vector anchorSword = new Vector(-.5f, 0);
        swordAnimation = new OrientedAnimation(playerNames[playerIndex-1]+".sword", SWORD_ANIMATION_DURATION, this,
                anchorSword, swordOrders, 4, 2, 2, 32, 32);

        swordAffected = false;

        final int STAFF_ANIMATION_DURATION = 2;
        final Vector anchorStaff = new Vector(-.5f, -.20f);
        String name;

        name = (playerIndex == 1) ? "icoop/player.staff_fire" : "icoop/player2.staff_water";

        staffAnimation = new OrientedAnimation(name, STAFF_ANIMATION_DURATION , this ,
                anchorStaff , swordOrders , 4, 2, 2, 32, 32);

        staffAffected = false;
    }

    /**
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        switch (currentState) {
            case IDLE:
                if (!idleAffected) {
                    currentAnimation.reset();
                    currentAnimation = idleAnimation;
                    idleAffected = true;
                }
                moveIfPressed(Orientation.LEFT, getKeyboard().get(keys.left()), deltaTime);
                moveIfPressed(UP, getKeyboard().get(keys.up()), deltaTime);
                moveIfPressed(RIGHT, getKeyboard().get(keys.right()), deltaTime);
                moveIfPressed(DOWN, getKeyboard().get(keys.down()), deltaTime);
                if (isImmune() && immunityPeriodIsOn()) {
                    decrementImmunityPeriod();
                }
                if (!immunityPeriodIsOn()) {
                    setImmune(false);
                }
                if (pressurePlate != null && !(getPosition() == pressurePlate.getPosition())) {
                    pressurePlate.setSignalFalse();
                    pressurePlate = null;
                }
                break;
            case SWORD_ATTACKING:
                if (!swordAffected) {
                    currentAnimation.reset();
                    currentAnimation = swordAnimation;
                    swordAffected = true;
                    idleAffected = false;
                }
                currentAnimation.update(deltaTime);
                if (currentAnimation.isCompleted()) {
                    currentState = State.IDLE;
                    swordAffected = false;
                    idleAffected = false;
                }
                break;
            case STAFF_ATTACKING:
                if (!staffAffected) {
                    currentAnimation.reset();
                    currentAnimation = staffAnimation;
                    staffAffected = true;
                    idleAffected = false;
                }
                currentAnimation.update(deltaTime);
                if (currentAnimation.isCompleted()) {
                    Ball ball = new Ball(getCurrentArea(), getOrientation(), getFieldOfViewCells().getFirst(), element);
                    getCurrentArea().registerActor(ball);
                    currentState = State.IDLE;
                    staffAffected = false;
                    idleAffected = false;
                }
                break;
        }
        switchItem();
        useItem();
    }

    @Override
    public void draw(Canvas canvas) {
        if (immunityPeriodIsPair()) {
            currentAnimation.draw(canvas);
            health.draw(canvas);
        }
    }

    /**
     * Orientate and Move this player with the idleAnimation in the given orientation if the given button is down
     *
     * @param orientation (Orientation): given orientation, not null
     * @param b           (Button): button corresponding to the given orientation, not null
     */
    private void moveIfPressed(Orientation orientation, Button b, float deltaTime) {
        if (orientation == null || b == null) {
            throw new NullPointerException("Null paramters in moveIfPressed");
        }
        if (b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(MOVE_DURATION);
                currentAnimation.reset();
            } else {
                currentAnimation.update(deltaTime);
            }
        }
    }

    /**
     * Leave an area by unregister this player
     */
    @Override
    public void leaveArea() {
        getCurrentArea().unregisterActor(this);
    }

    /**
     * makes the player entering a given area
     * @param area     (Area):  the area to be entered, not null
     * @param position (DiscreteCoordinates): initial position in the entered area, not null
     */
    public void enterArea(Area area, DiscreteCoordinates position) {
        if (area == null || position == null) {
            throw new NullPointerException("Null paramters in enterArea");
        }
        area.registerActor(this);
        area.setViewCandidate(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
    }

    public Element getElement(){
        return element;
    }

    public boolean getIsDoorPassing() {
        return isDoorPassing;
    }

    public void setIsDoorPassing(boolean isDoorPassing) {
        this.isDoorPassing = isDoorPassing;
    }

    public Door getDoorPassed() {
        return doorPassed;
    }

    private void setDoorPassed(Door doorPassed) {
        if (doorPassed == null) {
            throw new NullPointerException("doorPassed is null");
        }
        this.doorPassed = doorPassed;
    }

    @Override
    public boolean isDead() {
        return health.isOff();
    }

    /**
     * Sets the ICoopPlayer invulnerable to a certain damage type.
     * @param type (Element): Type of invulnerability (FIRE, WATER or PHYSICAL)
     * @throws IllegalArgumentException if none of the 3 types are entered.
     */
    public void setInvulnerable(Element type) {
        if (type == Element.FIRE || type == Element.WATER || type == Element.PHYSICAL) {
            invulnerableElement = type;
        } else {
            throw new IllegalArgumentException("Unsupported element type: " + type);
        }
    }

    /**
     * Takes damage only if the type of the Element is different than his
     * @param damage (int): Number of damages.
     * @param type (type): Type of the element that causes damage. Not null.
     */
    public void damage(int damage, Element type) {
        if (type == null) {
            throw new NullPointerException("type is null");
        }

        //if his invulnerableElement != type, then he is vulnerable to type
        if (invulnerableElement != type) {
            health.decrease(damage);
            setImmune(true);
            setImmunityPeriod(24);
        }
    }

    /**
     * Helper method for switching the current item among those in the inventory.
     */
    private void switchItem() {
        if (getKeyboard().get(keys.switchItem()).isPressed()) {
            currentItem = inventory.getNextItem();
        }
    }

    /**
     * Helper method for using items based on the current one in the inventory.
     */
    private void useItem() {
        if (getKeyboard().get(keys.useItem()).isPressed()) {
            switch (currentItem.getName()) {
                case "sword":
                    if (currentState == State.IDLE) {
                        currentState = State.SWORD_ATTACKING;
                    }
                    break;

                case "explosive":
                    Explosive explosive = new Explosive(getCurrentArea(), getFieldOfViewCells().getFirst());
                    if (getCurrentArea().enterAreaCells(explosive, getFieldOfViewCells())
                            && inventory.removePocketItem(currentItem, 1)) {
                        getCurrentArea().registerActor(explosive);
                        explosive.setActivated();
                        currentItem = inventory.getPreviousItem();
                    }
                    break;

                case "waterKey", "fireKey":
                    // do nothing
                    break;

                case "waterStaff":
                case "fireStaff":
                    if (currentState == State.IDLE) {
                        Ball ball = new Ball(getCurrentArea(), getOrientation(), getFieldOfViewCells().getFirst(), element);
                        if (getCurrentArea().enterAreaCells(ball, getFieldOfViewCells())) {
                            getCurrentArea().registerActor(ball);
                            currentState = State.STAFF_ATTACKING;
                        }
                    }
                    break;
            }
        }
    }

    public ICoopItem getCurrentItem() {
        return currentItem;
    }

    /**
     * @return (boolean): true if the ICoopPlayer wants view interaction (item key is down)
     */
    @Override
    public boolean wantsViewInteraction() {
        return (getKeyboard().get(keys.useItem()).isPressed()) || currentState == State.SWORD_ATTACKING;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this , isCellInteraction);
    }

    private class ICoopPlayerInteractionHandler implements ICoopInteractionVisitor {
        private boolean firstTimeKeyRequired = false;
        private boolean firstTimeVictory = false;
        /**
         * Default interaction between an ICoopPlayer and a Door
         * @param other (Door): Door to interact with, not null
         */
        @Override
        public void interactWith(Door other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("door is null");
            }
            if (other.getSignal().isOn()) {
                isDoorPassing = true;
                setDoorPassed(other);
            }
            if (other.getCurrentMainCellCoordinates().equals(new DiscreteCoordinates(6, 11)) && !firstTimeKeyRequired) {
                if (other.getSignal().isOff()) {
                    other.getGame().publish(new Dialog("key_required"));
                    firstTimeKeyRequired = true;
                }
            }
            if (other.getCurrentMainCellCoordinates().equals(new DiscreteCoordinates(6, 11)) && !firstTimeVictory) {
                if (inventory.contains(ICoopItem.FireKey) || inventory.contains(ICoopItem.WaterKey)) {
                    other.getGame().publish(new Dialog("victory"));
                    firstTimeVictory = true;
                }
            }
        }

        /**
         * Default interaction between an ICoopPlayer and an Explosive
         * @param other (Door): Door to interact with, not null
         */
        @Override
        public void interactWith(Explosive other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("explosive is null");
            }
            if (!isCellInteraction) {
                other.setActivated();
            } else {
                other.collect();
                inventory.addPocketItem(ICoopItem.Explosive, 1);
            }
        }

        /**
         * Default interaction between an ICoopPlayer and an ElementalItem
         * @param other (ElementalItem): ElementalItem to interact with, not null
         */
        @Override
        public void interactWith(Orb other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("elemental item is null");
            }
            if (isCellInteraction) {
                if (other.getElement() == Element.FIRE) {
                    other.getGame().publish(new Dialog("orb_fire_msg"));
                } else if (other.getElement() == Element.WATER) {
                    other.getGame().publish(new Dialog("orb_water_msg"));
                }
                other.collect();
                setInvulnerable(other.getElement());
            }
        }

        @Override
        public void interactWith(Heart other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("heart is null");
            }
            if (isCellInteraction) {
                health.increase(Heart.HEART_POINTS);
                other.collect();
            }
        }

        @Override
        public void interactWith(PressurePlate other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("PressurePlate is null");
            }
            if (isCellInteraction) {
                other.setSignalTrue();
                pressurePlate = other;
            }
        }

        @Override
        public void interactWith(Staff other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("staff is null");
            }
            if (isCellInteraction) {
                if (other.getElement() == element) {
                    other.collect();
                    if (other.getElement() == Element.FIRE) {
                        inventory.addPocketItem(ICoopItem.FireStaff, 1);
                    } else {
                        inventory.addPocketItem(ICoopItem.WaterStaff, 1);
                    }
                }
            }
        }

        @Override
        public void interactWith(BombFoe other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("BombFoe is null");
            }
            if (!isCellInteraction && currentState == State.SWORD_ATTACKING) {
                other.damage(10, Element.PHYSICAL);
            } else if (!isCellInteraction && currentState == State.STAFF_ATTACKING) {
                other.damage(10, element);
            }
        }

        @Override
        public void interactWith(HellSkull other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("hellskull is null");
            }
            if (!isCellInteraction && currentState == State.SWORD_ATTACKING) {
                other.damage(10, Element.PHYSICAL);
            }
        }

        @Override
        public void interactWith(Key other, boolean isCellInteraction) {
            if (other == null) {
                throw new NullPointerException("key is null");
            }
            if (isCellInteraction && other.getElement() == element) {
                other.collect();
                other.setSignalTrue();
                if (element == Element.FIRE) {
                    inventory.addPocketItem(ICoopItem.FireKey, 1);
                } else {
                    inventory.addPocketItem(ICoopItem.WaterKey, 1);
                }
            }
        }
    }
}


