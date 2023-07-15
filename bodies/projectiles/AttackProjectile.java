package bodies.projectiles;

import effects.IEffect;
import bodies.characters.AbstractPlayer;

public abstract class AttackProjectile extends AbstractProjectile {
    private Integer damage;

    public AttackProjectile(int damage, IEffect effect, AbstractPlayer player, Integer[] pos, Double[] vel) {
        super(effect, player, pos, vel);
        this.damage = damage;
    }

    public void collisionEffect() {
        AbstractPlayer p = this.getPlayer();
        p.setHealth(p.getHealth() - damage);
        if (this.getEffect() != null) p.applyNewEffect(this.getEffect());
    }
}
