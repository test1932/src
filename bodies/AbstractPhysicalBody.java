package bodies;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;

public class AbstractPhysicalBody {
    public Rectangle hitbox = new Rectangle();
    protected Rectangle image;
    public boolean gravityApplies = false;
    protected boolean gravityReversed = false;

    public boolean frictionApplies = false;
    protected Double frictionMultiplier = null;

    private Integer[] position;
    private Double[] velocity = {0.0,0.0};

    public AbstractPhysicalBody() {
        this.image = this.hitbox;
    }

    /**
     * author: user2221343
     * https://stackoverflow.com/questions/15690846/java-collision-detection-between-two-shape-objects
     * 20:10 11/7/23
     */
    public boolean collides(Shape other) {
        Area areaA = new Area(hitbox.getBounds2D());
        areaA.intersect(new Area(other.getBounds2D()));
        return !areaA.isEmpty();
    }

    public Integer[] getPosition() {
        return position;
    }

    public void setPosition(Integer[] position) {
        this.position = position;
        this.hitbox.x = position[0];
        this.hitbox.y = position[1];
        this.image.x = position[0];
        this.image.y = position[1];
    }

    public void incrementPosition(int index, int increment) {
        position[index] += increment;
    }

    public Double[] getVelocity() {
        return velocity;
    }

    public void setVelocity(Double[] velocity) {
        this.velocity = velocity;
    }

    public void setVelocityComp(int index, Double val) {
        this.velocity[index] = val;
    }

    public Shape getImage() {
        return image;
    }

    public boolean isGravityReversed() {
        return gravityReversed;
    }

    public void setGravityReversed(boolean gravityReversed) {
        this.gravityReversed = gravityReversed;
    }

    public void reverseGravity() {
        this.gravityReversed = !this.gravityReversed;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    public void setImage(Rectangle image) {
        this.image = image;
    }

    public boolean isGravityApplies() {
        return gravityApplies;
    }

    public void setGravityApplies(boolean gravityApplies) {
        this.gravityApplies = gravityApplies;
    }

    public boolean isFrictionApplies() {
        return frictionApplies;
    }

    public void setFrictionApplies(boolean frictionApplies) {
        this.frictionApplies = frictionApplies;
    }

    public Double getFrictionMultiplier() {
        return frictionMultiplier;
    }

    public void setFrictionMultiplier(Double frictionMultiplier) {
        this.frictionMultiplier = frictionMultiplier;
    }

    public void applyFriction() {
        Double[] oldv = getVelocity();
        oldv[0] *= frictionMultiplier;
        setVelocity(oldv);
    }
}
