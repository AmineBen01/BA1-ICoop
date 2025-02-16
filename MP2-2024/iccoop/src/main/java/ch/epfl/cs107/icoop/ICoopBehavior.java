package ch.epfl.cs107.icoop;


import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.icoop.actor.wall.ElementalWall;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;


public class ICoopBehavior extends AreaBehavior {
    /**
     * Default ICoopBehavior Constructor
     *
     * @param window (Window), not null
     * @param name   (String): Name of the Behavior, not null
     */
    public ICoopBehavior(Window window, String name, ICoopArea area) {
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ICoopCellType color = ICoopCellType.toType(getRGB(height - 1 - y, x));
                setCell(x, y, new ICoopCell(x, y, color));

                if (name.equals("Arena")) {
                    if (color == ICoopCellType.ROCK) {
                        area.registerActor(new Rock(area, new DiscreteCoordinates(x, y)));
                    } else if (color == ICoopCellType.OBSTACLE) {
                        area.registerActor(new Obstacle(area, Orientation.UP, new DiscreteCoordinates(x, y), "rock.2"));
                    }
                }
            }
        }
    }

    public enum ICoopCellType {
        //https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
        NULL(0, false , false),
        WALL(-16777216, false , false),
        IMPASSABLE (-8750470, false , true),
        INTERACT(-256, true , true),
        DOOR(-195580, true , true),
        WALKABLE(-1, true , true),
        ROCK(-16777204, true , true),
        OBSTACLE (-16723187, true , true);

        final int type;
        final boolean canWalk;
        final boolean canFly;

        ICoopCellType(int type, boolean canWalk, boolean canFly) {
            this.type = type;
            this.canWalk = canWalk;
            this.canFly = canFly;
        }

        public static ICoopCellType toType(int type) {
            for (ICoopCellType ict : ICoopCellType.values()) {
                if (ict.type == type)
                    return ict;
            }
            return NULL;
        }
    }

    /**
     * Cell adapted to the ICoop game
     */
    public class ICoopCell extends Cell {
        /// Type of the cell following the enum
        private final ICoopCellType type;

        /**
         * Default ICoopCell Constructor
         *
         * @param x    (int): x coordinate of the cell
         * @param y    (int): y coordinate of the cell
         * @param type (ICoopCellType), not null
         */
        public ICoopCell(int x, int y, ICoopCellType type) {
            super(x, y);
            if (type == null) {
                throw new NullPointerException("type is null");
            }
            this.type = type;
        }

        @Override
        protected boolean canLeave(Interactable entity) {
            if (entity == null) {
                throw new NullPointerException("entity is null");
            }
            return true;
        }

        @Override
        protected boolean canEnter(Interactable entity) {
            // si l'entité occupe l'espace
            // si la cellule n'est pas traversable
            // on retourne faux
            if (entity.takeCellSpace())
                if (!type.canWalk) {
                    return false;
                }

            //we can't make Interactable implement Unstoppable because it will change the game-engine.
            if (entity instanceof Unstoppable) {
                return ((Unstoppable) entity).unstop();
            }

            for (Interactable e : entities) {
                //s'il existe une autre entité qui occupe l'espace
                //retourner faux
                if (e.takeCellSpace()) {
                    return false;
                }

                if (e instanceof ElementalWall && entity instanceof ICoopPlayer) {
                    return ((ElementalWall) e).canEnter((ICoopPlayer) entity);
                }
            }
            return type.canWalk;
        }

        @Override
        public boolean isCellInteractable() {
            return false;
        }

        @Override
        public boolean isViewInteractable() {
            return false;
        }

        @Override
        public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
            ((ICoopInteractionVisitor) v).interactWith(this , isCellInteraction);
        }
    }
}
