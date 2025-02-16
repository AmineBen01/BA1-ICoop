package ch.epfl.cs107.icoop.area;


import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.Element;
import ch.epfl.cs107.icoop.actor.Key;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;


public class Arena extends ICoopArea implements Logic {
    private Logic signal;

    private Key redKey;
    private Key blueKey;

    private Door door;

    public Arena(DialogHandler game) {
        super(game);
    }

    @Override
    public ArrayList<DiscreteCoordinates> getPlayerSpawnPosition() {
        ArrayList<DiscreteCoordinates> coords = new ArrayList<>();
        coords.add(new DiscreteCoordinates(4, 5));
        coords.add(new DiscreteCoordinates(14, 15));
        return coords;
    }

    @Override
    protected void createArea() {
        signal = Logic.FALSE;

        registerActor(new Background(this));
        registerActor(new Foreground(this));

        redKey = new Key(this, new DiscreteCoordinates(9, 16), Element.FIRE);
        registerActor(redKey);

        blueKey = new Key(this, new DiscreteCoordinates(9, 4), Element.WATER);
        registerActor(blueKey);

        ArrayList<DiscreteCoordinates> destCoords = new ArrayList<>();
        destCoords.add(new DiscreteCoordinates(13, 6));
        destCoords.add(new DiscreteCoordinates(14, 7));

        door = new Door("Spawn", Logic.FALSE, destCoords, this, new DiscreteCoordinates(10, 10), "shadow");
        registerActor(door);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        door.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (redKey.isOn() && blueKey.isOn()) {
            setSignalTrue();
            door.setSignalTrue();
            door.update(deltaTime);
        }
    }

    @Override
    public String getTitle() {
        return "Arena";
    }

    @Override
    public boolean isOn() {
        return signal.isOn();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }

    public void setSignalTrue() {
        signal = Logic.TRUE;
    }
}
