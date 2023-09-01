package bodies.projectiles;

import java.awt.Rectangle;
import java.awt.Shape;

import actions.AbstractSpellAction;
import effects.IEffect;
import bodies.characters.AbstractPlayer;
import bodies.AbstractPhysicalBody;

public abstract class AbstractProjectile extends AbstractPhysicalBody {
    private Long timeRemaining;
    private IEffect effect = null;
    private AbstractPlayer owner;
    private AbstractSpellAction spellAction;
    protected boolean isMelee = false;
    public Long lastTime;
    private double bounce;
    private int collisions;

    public AbstractProjectile(int maxBounce, double bounce, IEffect effect, AbstractPlayer player, Integer[] pos, 
            Double[] vel, AbstractSpellAction spellAction) {
        this.effect = effect;
        this.owner = player;
        this.image = new Rectangle(pos[0], pos[1], 10, 20);
        this.spellAction = spellAction;
        this.hitbox = new Rectangle(pos[0], pos[1], 10, 20);
        this.bounce = bounce;
        this.collisions = maxBounce;
        setVelocity(vel);
        setPosition(pos);
    }

    public abstract void destroyProjectile();
    public abstract void updateVelocity(Long newTime);
    public abstract void collisionEffect(AbstractPlayer p);

    public void reduceTime(Long timeDiff) {
        timeRemaining -= timeDiff;
    }

    public boolean projectileIsOver() {
        return timeRemaining <= 0;
    }

    public AbstractPlayer getOwner() {
        return owner;
    }

    public IEffect getEffect() {
        return effect;
    }

    public void setEffect(IEffect e) {
        this.effect = e;
    }

    public Shape getImage() {
        return image;
    }

    public AbstractSpellAction getSpellAction() {
        return spellAction;
    }

    public boolean isMelee() {
        return isMelee;
    }

    public void setBounce(double bounce) {
        this.bounce = bounce;
    }

    public double getBounce() {
        return bounce;
    }

    public boolean needsToCollide() {
        return collisions > 0;
    }

    public void collided() {
        collisions ++;
    }

    @Override
    public String toString() {
        return "projectile at (" + String.valueOf(getPosition()[0] + "," + String.valueOf(getPosition()[1]) + ")");
    }
}
