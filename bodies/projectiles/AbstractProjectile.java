package bodies.projectiles;

import java.awt.Shape;

import effects.IEffect;
import bodies.characters.AbstractPlayer;
import bodies.AbstractPhysicalBody;

public abstract class AbstractProjectile extends AbstractPhysicalBody {
    private Long timeRemaining;
    private IEffect effect = null;
    private AbstractPlayer player;
    private Shape image;

    public Long lastTime;

    public AbstractProjectile(IEffect effect, AbstractPlayer player, Integer[] pos, Double[] vel) {
        this.effect = effect;
        this.player = player;
        this.image = null;
        setVelocity(vel);
        setPosition(pos);
    }

    public abstract void destroyProjectile();
    public abstract void updateVelocity(Long newTime);
    public abstract void collisionEffect();

    public void reduceTime(Long timeDiff) {
        timeRemaining -= timeDiff;
    }

    public boolean projectileIsOver() {
        return timeRemaining <= 0;
    }

    public AbstractPlayer getPlayer() {
        return player;
    }

    public IEffect getEffect() {
        return effect;
    }

    public Shape getImage() {
        return image;
    }
}
