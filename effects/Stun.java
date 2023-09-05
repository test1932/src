package effects;

import bodies.characters.AbstractPlayer;

public class Stun implements IEffect {
    private final Long INITIAL_TIME = 500l;

    private Long timeRemaining = INITIAL_TIME;
    private AbstractPlayer player;

    public Stun(Long duration) {
        this.timeRemaining = duration;
    }

    public Stun() {
        //pass
    }

    @Override
    public boolean effectIsOver() {
        return timeRemaining > 0l;
    }

    @Override
    public void reduceTime(Long timeDiff) {
        timeRemaining -= timeDiff;
        if (timeRemaining <= 0l) {
            this.player.setStunned(false);
            player.removeEffect(this);
        }
    }

    @Override
    public void applyEffect(AbstractPlayer player) {
        this.player = player;
        this.player.setStunned(true);
    }
    
}
