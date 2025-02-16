package ch.epfl.cs107.icoop.actor;

public interface Unstoppable {

    /**
     * Implemented for Projectiles. Prevents from type checking (if the game-engine was modifiable).
     * @return (boolean): true, if the implemented class is unstoppable.
     */
    boolean unstop();
}
