package bodies.projectiles;

import effects.EffectI;
import bodies.characters.PlayerA;

// import actions.

public class Projectile_Knife extends AttackProjectile{
    public Projectile_Knife(int damage, EffectI effect, PlayerA player, Integer[] pos, Double[] vel) {
        super(damage, effect, player, pos, vel);
    }

    public void destroyProjectile() {

    }

    public void updatePosition(Long newTime) {

    }

    public void updateVelocity() {

    }
}
