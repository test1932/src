package bodies.projectiles;

import actions.AbstractSpellAction;
import bodies.characters.AbstractPlayer;

// import actions.

public class Projectile_Knife extends AbstractAttackProjectile{
    public Projectile_Knife(int maxBounce, double bounce, boolean gravityApplies, int damage,
            AbstractPlayer player, Integer[] pos,  Double[] vel, AbstractSpellAction spellAction) {
        super(maxBounce, bounce, damage, player, pos, vel, spellAction); // effect often null
        this.gravityApplies = gravityApplies;
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
