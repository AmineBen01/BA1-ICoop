package ch.epfl.cs107.icoop.area;


import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.icoop.actor.wall.FireWall;
import ch.epfl.cs107.icoop.actor.wall.WaterWall;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;


public class Maze extends ICoopArea implements Logic {
    private Logic signal;

    private FireWall fireWall1a;
    private FireWall fireWall1b;
    private FireWall fireWall3;

    private PressurePlate pressurePlate1;
    private PressurePlate pressurePlate2;

    public Maze(DialogHandler game) {
        super(game);
    }

    /**
     * @return the player's spawn position in the area
     */
    @Override
    public ArrayList<DiscreteCoordinates> getPlayerSpawnPosition() {
        ArrayList<DiscreteCoordinates> coords = new ArrayList<>();
        coords.add(new DiscreteCoordinates(2, 39));
        coords.add(new DiscreteCoordinates(3, 39));
        return coords;
    }

    /**
     * Area specific callback to initialise the instance
     */
    @Override
    protected void createArea() {
        signal = Logic.FALSE;

        registerActor(new Background(this));
        registerActor(new Foreground(this));

        WaterWall waterWall1a = new WaterWall(this, Orientation.LEFT,
                new DiscreteCoordinates(4, 35), Logic.TRUE, true);
        registerActor(waterWall1a);
        WaterWall waterWall1b = new WaterWall(this, Orientation.LEFT,
                new DiscreteCoordinates(4, 36), Logic.TRUE, true);
        registerActor(waterWall1b);

        //pressurePlate1
        fireWall1a = new FireWall(this, Orientation.LEFT,
                new DiscreteCoordinates(6, 35), Logic.TRUE, false);
        registerActor(fireWall1a);
        fireWall1b = new FireWall(this, Orientation.LEFT,
                new DiscreteCoordinates(6, 36), Logic.TRUE, false);
        registerActor(fireWall1b);

        FireWall fireWall2a = new FireWall(this, Orientation.DOWN,
                new DiscreteCoordinates(2, 34), Logic.TRUE, true);
        registerActor(fireWall2a);
        FireWall fireWall2b = new FireWall(this, Orientation.DOWN,
                new DiscreteCoordinates(3, 34), Logic.TRUE, true);
        registerActor(fireWall2b);

        pressurePlate1 = new PressurePlate(this, new DiscreteCoordinates(6, 33));
        registerActor(pressurePlate1);

        Explosive explosive = new Explosive(this, new DiscreteCoordinates(6, 25));
        registerActor(explosive);

        WaterWall waterWall2a = new WaterWall(this, Orientation.DOWN,
                new DiscreteCoordinates(5, 24), Logic.TRUE, true);
        registerActor(waterWall2a);
        WaterWall waterWall2b = new WaterWall(this, Orientation.DOWN,
                new DiscreteCoordinates(6, 24), Logic.TRUE, true);
        registerActor(waterWall2b);

        pressurePlate2 = new PressurePlate(this, new DiscreteCoordinates(9, 25));
        registerActor(pressurePlate2);

        //pressurePlate2
        fireWall3 = new FireWall(this, Orientation.DOWN,
                new DiscreteCoordinates(8, 21), Logic.TRUE, false);
        registerActor(fireWall3);

        Heart heart1 = new Heart(this, new DiscreteCoordinates(15, 18));
        registerActor(heart1);
        Heart heart2 = new Heart(this, new DiscreteCoordinates(16, 19));
        registerActor(heart2);
        Heart heart3 = new Heart(this, new DiscreteCoordinates(14, 19));
        registerActor(heart3);
        Heart heart4 = new Heart(this, new DiscreteCoordinates(14, 17));
        registerActor(heart4);

        WaterWall waterWall3 = new WaterWall(this, Orientation.DOWN,
                new DiscreteCoordinates(8, 4), Logic.TRUE, true);
        registerActor(waterWall3);

        FireWall fireWall3 = new FireWall(this, Orientation.DOWN,
                new DiscreteCoordinates(13, 4), Logic.TRUE, true);
        registerActor(fireWall3);

        FireWall fireWall4 = new FireWall(this, Orientation.DOWN,
                new DiscreteCoordinates(13, 14), Logic.TRUE, true);
        registerActor(fireWall4);

        for (int i = 0; i < 10; i += 2) {
            HellSkull hellSkull = new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(12, 33-i));
            registerActor(hellSkull);
        }

        for (int i = 0; i < 8; i += 2) {
            HellSkull hellSkull = new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(10, 32-i));
            registerActor(hellSkull);
        }

        BombFoe bombFoe1 = new BombFoe(this, new DiscreteCoordinates(5, 15));
        registerActor(bombFoe1);

        BombFoe bombFoe2 = new BombFoe(this, new DiscreteCoordinates(6, 17));
        registerActor(bombFoe2);

        BombFoe bombFoe3 = new BombFoe(this, new DiscreteCoordinates(10, 17));
        registerActor(bombFoe3);

        BombFoe bombFoe4 = new BombFoe(this, new DiscreteCoordinates(5, 14));
        registerActor(bombFoe4);

        Staff fireStaff = new Staff(this, new DiscreteCoordinates(13, 2), Element.FIRE);
        registerActor(fireStaff);

        Staff waterStaff = new Staff(this, new DiscreteCoordinates(8, 2), Element.WATER);
        registerActor(waterStaff);

        ArrayList<DiscreteCoordinates> destCoords = new ArrayList<>();
        destCoords.add(new DiscreteCoordinates(4, 5));
        destCoords.add(new DiscreteCoordinates(14, 15));
        Door door = new Door("Arena", Logic.TRUE, destCoords, this,
                new DiscreteCoordinates(19, 6), new DiscreteCoordinates(19, 7));
        registerActor(door);
    }

    @Override
    public void update(float deltaTime) {
        Logic opposite1 = (pressurePlate1.isOn()) ? Logic.FALSE : Logic.TRUE;
        Logic opposite2 = (pressurePlate2.isOn()) ? Logic.FALSE : Logic.TRUE;
        super.update(deltaTime);
        if (pressurePlate1.isOn()) {
            fireWall1a.setSignal(opposite1);
            fireWall1b.setSignal(opposite1);
        }
        if (pressurePlate2.isOn()) {
            fireWall3.setSignal(opposite2);
        }
        if (pressurePlate1.isOff()) {
            fireWall1a.setSignal(opposite1);
            fireWall1b.setSignal(opposite1);
        }
        if (pressurePlate2.isOff()) {
            fireWall3.setSignal(opposite2);
        }
    }

    /**
     * Getter for game title
     * Note: Need to be Override
     * @return (String) the game title
     */
    @Override
    public String getTitle() {
        return "Maze";
    }

    @Override
    public boolean isOn() {
        return signal.isOn();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }
}
