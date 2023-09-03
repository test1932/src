package effects;

import bodies.characters.AbstractPlayer;
import bodies.projectiles.AbstractProjectile;

public class Knockback implements IEffect {
    private final Long INITIAL_TIME = 100l;

    private Long timeRemaining = INITIAL_TIME;
    private AbstractPlayer player;
    private AbstractProjectile projectile;
    private Double[] impactVelocity;
    private boolean isFixed;
    private Double[] magnitude;

    public Knockback(AbstractProjectile projectile) {
        this.isFixed = false;
        this.projectile = projectile;
    }

    public Knockback(AbstractProjectile projectile, Double magnitude) {
        this.isFixed = true;
        this.magnitude = new Double[]{magnitude, 0d};
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
            double scalar = (timeRemaining / (double)INITIAL_TIME);
            player.setVel(new Double[]{impactVelocity[0] * scalar, impactVelocity[1] * scalar});
        }
    }

    @Override
    public void applyEffect(AbstractPlayer player) {
        this.player = player;
        this.impactVelocity = isFixed ? magnitude : projectile.getVelocity();
        if (isFixed && projectile.getPosition()[0] > player.getPosition()[0]) {
            this.magnitude[0] *= -1;
        }
    }
    
}
