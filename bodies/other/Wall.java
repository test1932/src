package bodies.other;

import bodies.AbstractPhysicalBody;

import java.awt.Rectangle;

public class Wall extends AbstractPhysicalBody{
    public Wall(int x, int y, int width, int height) {
        hitbox = new Rectangle(x, y, width, height);
        gravityApplies = false;
    }
}
