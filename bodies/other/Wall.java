package bodies.other;

import bodies.PhysicalBodyA;

import java.awt.Rectangle;

public class Wall extends PhysicalBodyA{
    public Wall(int x, int y, int width, int height) {
        hitbox = new Rectangle(x, y, width, height);
        gravityApplies = false;
    }
}
