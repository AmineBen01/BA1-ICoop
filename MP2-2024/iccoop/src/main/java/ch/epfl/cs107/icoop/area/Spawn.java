package ch.epfl.cs107.icoop.area;


import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;


public class Spawn extends ICoopArea {
    private boolean firstUpdateCallPassed;

    public Spawn(DialogHandler game) {
        super(game);
        firstUpdateCallPassed = false;
    }

    /**
     * @return the players' spawn position in the area
     */
    @Override
    public ArrayList<DiscreteCoordinates> getPlayerSpawnPosition() {
        //The first entry of the array is for the red player, the second for the blue
        ArrayList<DiscreteCoordinates> coords = new ArrayList<>();
        coords.add(new DiscreteCoordinates(13, 6));
        coords.add(new DiscreteCoordinates(14, 7));
        return coords;
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        ArrayList<DiscreteCoordinates> destCoordsOrbWay = new ArrayList<>();
        destCoordsOrbWay.add(new DiscreteCoordinates(1, 12));
        destCoordsOrbWay.add(new DiscreteCoordinates(1, 5));

        ArrayList<DiscreteCoordinates> destCoordsMaze = new ArrayList<>();
        destCoordsMaze.add(new DiscreteCoordinates(2, 39));
        destCoordsMaze.add(new DiscreteCoordinates(3, 39));

        Door door1 = new Door("OrbWay", Logic.TRUE, destCoordsOrbWay, this,
                new DiscreteCoordinates(19, 15), new DiscreteCoordinates(19, 16));
        registerActor(door1);
        Door door2 = new Door("Maze", Logic.TRUE, destCoordsMaze, this,
                new DiscreteCoordinates(4, 0), new DiscreteCoordinates(5, 0));
        registerActor(door2);

        Explosive explosive1 = new Explosive(this, new DiscreteCoordinates(12, 10));
        registerActor(explosive1);

        Rock rock = new Rock(this, new DiscreteCoordinates(11, 10));
        registerActor(rock);
        Door mainDoor = new Door("Spawn", Logic.FALSE, getPlayerSpawnPosition(), this, new DiscreteCoordinates(6, 11), game);
        registerActor(mainDoor);
    }

    @Override
    public void update(float deltaTime) {
        if (!firstUpdateCallPassed) {
            super.update(deltaTime);
            game.publish(new Dialog("welcome"));
            firstUpdateCallPassed = true;
        }
        if (isActiveDialogCompleted()) {
            super.update(deltaTime);
        }
    }

    @Override
    public String getTitle() {
        return "Spawn";
    }
}
