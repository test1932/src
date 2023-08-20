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

    public Long lastTime;

    public AbstractProjectile(IEffect effect, AbstractPlayer player, Integer[] pos, 
            Double[] vel, AbstractSpellAction spellAction) {
        this.effect = effect;
        this.owner = player;
        this.image = new Rectangle(pos[0], pos[1], 10, 20);
        this.spellAction = spellAction;
        this.hitbox = new Rectangle(pos[0], pos[1], 10, 20);
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

    public Shape getImage() {
        return image;
    }

    public AbstractSpellAction getSpellAction() {
        return spellAction;
    }

    @Override
    public String toString() {
        return "projectile at (" + String.valueOf(getPosition()[0] + "," + String.valueOf(getPosition()[1]) + ")");
    }
}
