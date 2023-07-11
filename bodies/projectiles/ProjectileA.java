package bodies.projectiles;

import java.awt.Shape;

import effects.EffectI;
import bodies.characters.PlayerA;
import bodies.PhysicalBodyA;

public abstract class ProjectileA extends PhysicalBodyA {
    private Long timeRemaining;
    private EffectI effect = null;
    private PlayerA player;
    private Shape image;

    public Long lastTime;

    public ProjectileA(EffectI effect, PlayerA player, Integer[] pos, Double[] vel) {
        this.effect = effect;
        this.player = player;
        this.image = null;
        setVelocity(vel);
        setPosition(pos);
    }

    public abstract void destroyProjectile();
    public abstract void updatePosition(Long newTime);
    public abstract void updateVelocity();
    public abstract void collisionEffect();

    public void reduceTime(Long timeDiff) {
        timeRemaining -= timeDiff;
    }

    public boolean effectIsOver() {
        return timeRemaining <= 0;
    }

    public PlayerA getPlayer() {
        return player;
    }

    public EffectI getEffect() {
        return effect;
    }

    public Shape getImage() {
        return image;
    }
}
