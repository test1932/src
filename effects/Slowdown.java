package effects;

import bodies.characters.AbstractPlayer;

public class Slowdown implements IEffect {
    private Long timeRemaining;
    private AbstractPlayer player;
    private double multiplier;

    public Slowdown(Long duration, double multiplier) {
        this.timeRemaining = duration;
        this.multiplier = multiplier;
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
            Double[] v = player.getVelocity();
            player.setVel(new Double[]{v[0] * multiplier, v[1] * multiplier});
        }
    }

    @Override
    public void applyEffect(AbstractPlayer player) {
        this.player = player;
    }
    
}
