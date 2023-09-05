package bodies.characters.Misc.projectiles;

import java.util.LinkedList;

import actions.AbstractSpellAction;
import bodies.characters.AbstractPlayer;
import bodies.projectiles.AbstractAttackProjectile;
import effects.ExtremeKnockback;
import effects.IEffect;
import effects.Invulnerable;
import effects.Knockback;
import effects.Stun;

// import actions.

public class MeleeProjectile extends AbstractAttackProjectile {
    protected boolean isKnockback;

    public MeleeProjectile(boolean isKnockback, int damage, AbstractPlayer player, Integer[] pos, AbstractSpellAction spellAction) {
        super(0, 0, damage, player, pos, new Double[]{0d,0d}, spellAction, true); // effect often null
        this.gravityApplies = false;
        this.isKnockback = isKnockback;
        setupEffects();
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

    @Override
    protected void setupEffects() {
        LinkedList<IEffect> effects = new LinkedList<IEffect>();
        // System.out.println(isKnockback);
        if (isKnockback) {
            effects.add(new ExtremeKnockback(getOwner()));
            effects.add(new Stun(2000l));
            effects.add(new Invulnerable(2000l));
        }
        else {
            effects.add(new Knockback(this, 1d));
            effects.add(new Stun(500l));
        }
        this.setEffects(effects);
    }

    public void collisionEffect(AbstractPlayer p) {
        p.setHealth(p.getHealth() - damage);
        if (this.getEffects() != null) {
            for (IEffect e : this.getEffects()) {
                // swapping these two lines will cause a mess for invulnerable
                p.applyNewEffect(e);
                // e.applyEffect(p);
            }
        }
        this.getSpellAction().removeAllProjectiles();
    }
}
