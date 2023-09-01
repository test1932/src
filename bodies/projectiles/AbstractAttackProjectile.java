package bodies.projectiles;

import effects.Knockback;
import actions.AbstractSpellAction;
import bodies.characters.AbstractPlayer;

public abstract class AbstractAttackProjectile extends AbstractProjectile {
    private Integer damage;

    public AbstractAttackProjectile(int maxBounce, double bounce, int damage, AbstractPlayer player, Integer[] pos, 
            Double[] vel, AbstractSpellAction spellAction) {
        super(maxBounce, bounce, null, player, pos, vel, spellAction);
        this.setEffect(new Knockback(this));
        this.damage = damage;
    }

    public void collisionEffect(AbstractPlayer p) {
        p.setHealth(p.getHealth() - damage);
        if (this.getEffect() != null) p.applyNewEffect(this.getEffect());
        this.getEffect().applyEffect(p);
        this.getSpellAction().removeProjectile(this);
    }
}
