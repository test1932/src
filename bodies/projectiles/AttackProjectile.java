package bodies.projectiles;

import effects.EffectI;
import bodies.characters.PlayerA;

public abstract class AttackProjectile extends ProjectileA {
    private Integer damage;

    public AttackProjectile(int damage, EffectI effect, PlayerA player, Integer[] pos, Double[] vel) {
        super(effect, player, pos, vel);
        this.damage = damage;
    }

    public void collisionEffect() {
        PlayerA p = this.getPlayer();
        p.setHealth(p.getHealth() - damage);
        if (this.getEffect() != null) p.applyNewEffect(this.getEffect());
    }
}
