package bodies.projectiles;

import effects.IEffect;
import actions.AbstractSpellAction;
import bodies.characters.AbstractPlayer;

public abstract class AbstractAttackProjectile extends AbstractProjectile {
    private Integer damage;

    public AbstractAttackProjectile(int damage, IEffect effect, AbstractPlayer player, Integer[] pos, 
            Double[] vel, AbstractSpellAction spellAction) {
        super(effect, player, pos, vel, spellAction);
        this.damage = damage;
    }

    public void collisionEffect() {
        AbstractPlayer p = this.getOwner();
        p.setHealth(p.getHealth() - damage);
        if (this.getEffect() != null) p.applyNewEffect(this.getEffect());
    }
}
