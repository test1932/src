package game.model.scenario;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;

import bodies.characters.AbstractPlayer;
import bodies.characters.Sakuya.Sakuya;
import bodies.AbstractPhysicalBody;
import bodies.other.Wall;
import bodies.projectiles.AbstractProjectile;
import game.model.Controller;

public class Battle {
    public Rectangle bounds;
    //TODO move these
    protected String pathBack = "assets/images/backgrounds/battle1.png";
    protected String pathBackBack = "assets/images/backgrounds/backbackground.jpg";
    public BufferedImage background;
    public BufferedImage backbackground;

    public AbstractPhysicalBody[] walls;
    public ReentrantLock bodiesLock = new ReentrantLock();

    public final int X = 0;
    public final int Y = 50;
    public final int WIDTH = 960;
    public final int HEIGHT = 550;

    private Controller cont;
    private boolean isOver = false;
    private Battle nextBattle = null;

    public Battle(Controller cont) {
        this.cont = cont;
        this.cont.players[1].setCharacter(new Sakuya(this.cont.players[1]));
        bounds = new Rectangle(X, Y, WIDTH, HEIGHT);
        setupWalls();
        setBackground();
    }

    public void setBackground() {
        try {
            this.background = ImageIO.read(new File(pathBack));
            this.backbackground = ImageIO.read(new File(pathBackBack));
        } catch (IOException e) {
            System.err.println("failed to get image");
        }
    }

    public BufferedImage getBackground() {
        return background;
    }

    public BufferedImage getBackBackground() {
        return backbackground;
    }

    private void setupWalls() {
        AbstractPhysicalBody floor = new Wall(X, Y + HEIGHT, WIDTH, 200);
        AbstractPhysicalBody ceiling = new Wall(X, Y - 200, WIDTH, 200);
        AbstractPhysicalBody left = new Wall(X - 200, Y - 200, 200, HEIGHT + 400);
        AbstractPhysicalBody right = new Wall(X + WIDTH, Y -200, 200, HEIGHT + 400);
        walls = new AbstractPhysicalBody[]{floor, ceiling, left, right};
    }

    public void changePlayerPos(int playerIndex, Integer[] newPos) {
        this.cont.players[playerIndex].setPosition(newPos);
    }

    public Boolean outOfBounds(int isX, Shape s, Double d) {
        if (d == 0) return false;
        if (isX == 0) {
            return (walls[2].collides(s) && d < 0) || (walls[3].collides(s) && d > 0);
        }
        return (walls[0].collides(s) && d > 0) || (walls[1].collides(s) && d < 0);
    }

    //TODO tidy this mess as well
    public void playerOutOfBounds(AbstractPlayer p) {
        Shape s = p.hitbox;
        Double[] v = p.getVelocity();

        boolean wallLeftCollision = walls[2].collides(s) && v[0] < 0;
        boolean wallRightCollision = walls[3].collides(s) && v[0] > 0;
        
        boolean wallFloorCollision = walls[0].collides(s) && v[1] > 0;
        boolean wallCeilingCollision = walls[1].collides(s) && v[1] < 0;

        if (wallLeftCollision || wallRightCollision) {
            if (v[0] > 0.5)  v[1] += 0.5;
            v[0] *= -0.3;
            p.removeKnockback();

        }

        if (wallCeilingCollision || wallFloorCollision) {
            v[1] *= 0;
            p.removeKnockback();
        }
    }

    //TODO for the love of god, clean this up
    public Double[] projectileWallCollision(AbstractProjectile p) {
        Double[] v = p.getVelocity();
        Shape s = p.hitbox;
        if ((v[0] > 0 && walls[3].collides(s) || v[0] < 0 && walls[2].collides(s)) && p.needsToCollide()) {
            v[0] *= -p.getBounce();
            p.collided();
        }
        if ((v[1] > 0 && walls[0].collides(s) || v[1] < 0 && walls[1].collides(s)) && p.needsToCollide()) {
            v[1] *= -p.getBounce();
            p.collided();
        }
        return v;
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

    public boolean isOnGround(Shape s, boolean isReversed) {
        return isReversed ? walls[1].collides(s) : walls[0].collides(s);
    }

    public Battle getNextBattle() {
        return nextBattle;
    }

    public void setNextBattle(Battle nextBattle) {
        this.nextBattle = nextBattle;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean isOver) {
        this.isOver = isOver;
    }

    public void checkIfOver() {
        this.isOver = cont.players[0].getHealth() <= 0 || cont.players[1].getHealth() <= 0;
    }
}
