package game.model;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.LinkedList;

import actions.AbstractSpellAction;
import bodies.characters.HumanPlayer;
import bodies.characters.Sakuya.Sakuya;
import bodies.characters.AbstractPlayer;
import bodies.AbstractPhysicalBody;
import bodies.other.Wall;
import game.Game;

public class Battle {
    public Rectangle bounds;
    public AbstractPlayer[] players = {new HumanPlayer(true, null), 
                                       new HumanPlayer(false, null)};
    public LinkedList<AbstractPhysicalBody> bodies = new LinkedList<AbstractPhysicalBody>();
    public LinkedList<AbstractSpellAction> spellActions = new LinkedList<AbstractSpellAction>();

    public AbstractPhysicalBody[] walls;

    public final int X = 100;
    public final int Y = 50;
    public final int WIDTH = 800;
    public final int HEIGHT = 500;

    private Game game;


    public Battle() {
        players[0].character = new Sakuya(players[0]);
        players[1].character = new Sakuya(players[1]);
        bounds = new Rectangle(X, Y, WIDTH, HEIGHT);
        setupWalls();

        for (AbstractPhysicalBody physicalBodyA : players) {
            bodies.add(physicalBodyA);
        }
    }

    private void setupWalls() {
        AbstractPhysicalBody floor = new Wall(X, Y + HEIGHT, WIDTH, 200);
        AbstractPhysicalBody ceiling = new Wall(X, Y - 200, WIDTH, 200);
        AbstractPhysicalBody left = new Wall(X - 200, Y - 200, 200, HEIGHT + 400);
        AbstractPhysicalBody right = new Wall(X + WIDTH, Y -200, 200, HEIGHT + 400);
        walls = new AbstractPhysicalBody[]{floor, ceiling, left, right};
    }

    public void changePlayerPos(int playerIndex, Integer[] newPos) {
        players[playerIndex].setPos(newPos);
        game.notifyObservers();
    }

    public Boolean outOfBounds(int isX, Shape s, Double d) {
        if (d == 0) return false;
        if (isX == 0) {
            return (walls[2].collides(s) && d < 0) || (walls[3].collides(s) && d > 0);
        }
        return (walls[0].collides(s) && d > 0) || (walls[1].collides(s) && d < 0);
    }

    public Boolean collidesWall(Shape a) {
        for (AbstractPhysicalBody wall : walls) {
            if (wall.collides(a)) return true;
        }
        return false;
    }

    public boolean shapeInBounds(Shape s) {
        Area areaA = new Area(s);
        areaA.intersect(new Area(bounds));
        return areaA.equals(new Area(s));
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
