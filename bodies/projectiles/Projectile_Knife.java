package bodies.projectiles;

import effects.IEffect;
import actions.AbstractSpellAction;
import bodies.characters.AbstractPlayer;

// import actions.

public class Projectile_Knife extends AbstractAttackProjectile{
    public Projectile_Knife(int damage, IEffect effect, AbstractPlayer player, Integer[] pos, 
            Double[] vel, AbstractSpellAction spellAction) {
        super(damage, effect, player, pos, vel, spellAction); // effect often null
        gravityApplies = true;
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
