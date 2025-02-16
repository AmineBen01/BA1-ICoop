package ch.epfl.cs107.icoop.area;


import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.icoop.actor.wall.FireWall;
import ch.epfl.cs107.icoop.actor.PressurePlate;
import ch.epfl.cs107.icoop.actor.wall.WaterWall;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;


public class OrbWay extends ICoopArea {

    private final ArrayList<FireWall> fireWalls = new ArrayList<>();
    private final ArrayList<WaterWall> waterWalls = new ArrayList<>();

    private PressurePlate pressurePlate1;
    private PressurePlate pressurePlate2;

    public OrbWay(DialogHandler game) {
        super(game);
    }

    /**
     * @return the players' spawn position in the area
     */
    @Override
    public ArrayList<DiscreteCoordinates> getPlayerSpawnPosition() {
        //The first entry of the array is for the red player, the second for the blue
        ArrayList<DiscreteCoordinates> coords = new ArrayList<>();
        coords.add(new DiscreteCoordinates(1, 12));
        coords.add(new DiscreteCoordinates(1, 5));
        return coords;
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        ArrayList<DiscreteCoordinates> destCoordsSpawn = new ArrayList<>();
        destCoordsSpawn.add(new DiscreteCoordinates(18, 16));
        destCoordsSpawn.add(new DiscreteCoordinates(18, 15));
        Door door014 = new Door("Spawn", Logic.TRUE, destCoordsSpawn, this, new DiscreteCoordinates(0, 14),
                new DiscreteCoordinates(0, 13),
                new DiscreteCoordinates(0, 12),
                new DiscreteCoordinates(0, 11),
                new DiscreteCoordinates(0, 10));
        registerActor(door014);
        Door door08 = new Door("Spawn", Logic.TRUE, destCoordsSpawn, this, new DiscreteCoordinates(0, 8),
                new DiscreteCoordinates(0, 7),
                new DiscreteCoordinates(0, 6),
                new DiscreteCoordinates(0, 5),
                new DiscreteCoordinates(0, 4));
        registerActor(door08);

        Orb waterOrb = new Orb(this, new DiscreteCoordinates(17, 6), false, Element.WATER, game);
        registerActor(waterOrb);
        Orb fireOrb = new Orb(this, new DiscreteCoordinates(17, 12), false, Element.FIRE, game);
        registerActor(fireOrb);

        for (int i = 0; i < 5; ++i) {
            FireWall fireWall = new FireWall(this, Orientation.LEFT,
                    new DiscreteCoordinates(12, 10+i), Logic.TRUE, false);
            fireWalls.add(fireWall);
            registerActor(fireWall);
        }

        for (int i = 0; i < 5; ++i) {
            WaterWall waterWall = new WaterWall(this, Orientation.LEFT,
                    new DiscreteCoordinates(12, 4+i), Logic.TRUE, false);
            waterWalls.add(waterWall);
            registerActor(waterWall);
        }

        WaterWall waterWall1 = new WaterWall(this, Orientation.LEFT, new DiscreteCoordinates(7, 12),
                Logic.TRUE, false);
        registerActor(waterWall1);
        FireWall fireWall1 = new FireWall(this, Orientation.LEFT, new DiscreteCoordinates(7, 6),
                Logic.TRUE, false);
        registerActor(fireWall1);

        Heart heart1 = new Heart(this, new DiscreteCoordinates(8, 4));
        registerActor(heart1);
        Heart heart2 = new Heart(this, new DiscreteCoordinates(10, 6));
        registerActor(heart2);
        Heart heart3 = new Heart(this, new DiscreteCoordinates(5, 13));
        registerActor(heart3);
        Heart heart4 = new Heart(this, new DiscreteCoordinates(10, 11));
        registerActor(heart4);

        pressurePlate1 = new PressurePlate(this, new DiscreteCoordinates(5, 7));
        registerActor(pressurePlate1);

        pressurePlate2 = new PressurePlate(this, new DiscreteCoordinates(5, 10));
        registerActor(pressurePlate2);
    }

    public void update(float deltaTime) {
        Logic opposite1 = (pressurePlate1.isOn()) ? Logic.FALSE : Logic.TRUE;
        Logic opposite2 = (pressurePlate2.isOn()) ? Logic.FALSE : Logic.TRUE;
        super.update(deltaTime);
        if (pressurePlate1.isOn()) {
            for (FireWall wall : fireWalls) {
                wall.setSignal(opposite1);
            }
        }
        if (pressurePlate2.isOn()) {
            for (WaterWall wall : waterWalls) {
                wall.setSignal(opposite2);
            }
        }
        if (pressurePlate1.isOff()) {
            for (FireWall wall : fireWalls) {
                wall.setSignal(opposite1);
            }
        }
        if (pressurePlate2.isOff()) {
            for (WaterWall wall : waterWalls) {
                wall.setSignal(opposite2);
            }
        }
    }

    @Override
    public String getTitle() {
        return "OrbWay";
    }
}
