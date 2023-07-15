package bodies.projectiles;

import effects.IEffect;
import bodies.characters.AbstractPlayer;

// import actions.

public class Projectile_Knife extends AttackProjectile{
    public Projectile_Knife(int damage, IEffect effect, AbstractPlayer player, Integer[] pos, Double[] vel) {
        super(damage, effect, player, pos, vel); // effect often null
    }

    @Override
    public void destroyProjectile() {
        //Do nothing, basic knife doesn't have death effect
    }

    @Override
    public void updateVelocity(Long newTime) {
        reduceTime(newTime);
        // Do nothing, basic knife continues on path
    }
}
