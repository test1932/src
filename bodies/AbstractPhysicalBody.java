package bodies;

import java.awt.Shape;
import java.awt.geom.Area;

public class AbstractPhysicalBody {
    public Shape hitbox;
    public boolean gravityApplies = false;
    private Integer[] position;
    private Double[] velocity = {0.0,0.0};

    /**
     * author: user2221343
     * https://stackoverflow.com/questions/15690846/java-collision-detection-between-two-shape-objects
     * 20:10 11/7/23
     */
    public boolean collides(Shape other) {
        Area areaA = new Area(hitbox);
        areaA.intersect(new Area(other));
        return !areaA.isEmpty();
    }

    public Integer[] getPosition() {
        return position;
    }

    public void setPosition(Integer[] position) {
        this.position = position;
    }

    public Double[] getVelocity() {
        return velocity;
    }

    public void setVelocity(Double[] velocity) {
        this.velocity = velocity;
    }
}
