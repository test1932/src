package effects;

import bodies.characters.AbstractPlayer;
import bodies.projectiles.AbstractProjectile;

public class Knockback implements IEffect {
    private final Long INITIAL_TIME = 100l;

    private Long timeRemaining = INITIAL_TIME;
    private AbstractPlayer player;
    private AbstractProjectile projectile;
    private Double[] impactVelocity;

    public Knockback(AbstractProjectile projectile) {
        this.projectile = projectile;
    }

    @Override
    public boolean effectIsOver() {
        return timeRemaining > 0l;
    }

    @Override
    public void reduceTime(Long timeDiff) {
        timeRemaining -= timeDiff;
        if (timeRemaining <= 0l) {
            player.removeEffect(this);
        }
        else {
            double scalar = (timeRemaining / (double)INITIAL_TIME) ;
            System.out.println(scalar);
            player.setVel(new Double[]{impactVelocity[0] * scalar, impactVelocity[1] * scalar});
        }
    }

    @Override
    public void applyEffect(AbstractPlayer player) {
        this.player = player;
        this.impactVelocity = projectile.getVelocity();
    }
    
}
