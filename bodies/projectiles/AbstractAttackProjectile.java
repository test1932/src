package bodies.projectiles;

import effects.IEffect;
import effects.Knockback;

import java.util.LinkedList;

import actions.AbstractSpellAction;
import bodies.characters.AbstractPlayer;

public abstract class AbstractAttackProjectile extends AbstractProjectile {
    protected Integer damage;

    public AbstractAttackProjectile(int maxBounce, double bounce, int damage, AbstractPlayer player, Integer[] pos, 
            Double[] vel, AbstractSpellAction spellAction, boolean isMelee) {
        super(maxBounce, bounce, null, player, pos, vel, spellAction, isMelee);
        setupEffects();
        this.damage = damage;
    }

    protected void setupEffects() {
        LinkedList<IEffect> es = new LinkedList<IEffect>();
        es.add(new Knockback(this));
        this.setEffects(es);
    }

    public void collisionEffect(AbstractPlayer p) {
        p.setHealth(p.getHealth() - damage);
        if (this.getEffects() != null) {
            for (IEffect e : this.getEffects()) {
                p.applyNewEffect(e);
                // e.applyEffect(p);
            }
        }
        this.getSpellAction().removeProjectile(this);
    }
}
