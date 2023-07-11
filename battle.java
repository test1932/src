import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bodies.characters.HumanPlayer;
import bodies.characters.PlayerA;
import bodies.PhysicalBodyA;
import bodies.other.Wall;
import listeners.Observer;

public class battle {
    public Rectangle bounds;
    public PlayerA[] players = {new HumanPlayer(true), 
                                new HumanPlayer(false)};
    private List<Observer> observers;
    public LinkedList<PhysicalBodyA> bodies = new LinkedList<PhysicalBodyA>();

    public PhysicalBodyA[] walls;

    public final int X = 100;
    public final int Y = 50;
    public final int WIDTH = 800;
    public final int HEIGHT = 500;

    public battle() {
        bounds = new Rectangle(X, Y, WIDTH, HEIGHT);
        setupWalls();
        observers = new ArrayList<Observer>();

        for (PhysicalBodyA physicalBodyA : players) {
            bodies.add(physicalBodyA);
        }
    }

    private void setupWalls() {
        PhysicalBodyA floor = new Wall(X, Y + HEIGHT, WIDTH, 200);
        PhysicalBodyA ceiling = new Wall(X, Y - 200, WIDTH, 200);
        PhysicalBodyA left = new Wall(X - 200, Y - 200, 200, HEIGHT + 400);
        PhysicalBodyA right = new Wall(X + WIDTH, Y -200, 200, HEIGHT + 400);
        walls = new PhysicalBodyA[]{floor, ceiling, left, right};
    }

    public void changePlayerPos(int playerIndex, Integer[] newPos) {
        players[playerIndex].setPos(newPos);
        for (Observer o : observers) {
            o.update();
        }
    }

    public void addObserver(Observer o) {
        this.observers.add(o);
    }

    public Boolean outOfBounds(int isX, Shape s, Double d) {
        if (d == 0) return false;
        if (isX == 0) {
            return (walls[2].collides(s) && d < 0) || (walls[3].collides(s) && d > 0);
        }
        return (walls[0].collides(s) && d > 0) || (walls[1].collides(s) && d < 0);
    }

    public Boolean collidesWall(Shape a) {
        for (PhysicalBodyA wall : walls) {
            if (wall.collides(a)) return true;
        }
        return false;
    }
}
