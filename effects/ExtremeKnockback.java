package effects;

import bodies.characters.AbstractPlayer;

public class ExtremeKnockback implements IEffect {
    private AbstractPlayer player;
    private AbstractPlayer attacker;
    private Double[] diffPosition;

    private static final double MULTIPLIER = 0.05;

    public ExtremeKnockback(AbstractPlayer attacker) {
        this.attacker = attacker;
    }

    @Override
    public boolean effectIsOver() {
        return false;
    }

    @Override
    public void reduceTime(Long timeDiff) {
        player.setVel(new Double[]{diffPosition[0], diffPosition[1]});
    }

    @Override
    public void applyEffect(AbstractPlayer player) {
        this.player = player;
        Integer[] pp = player.getPosition();
        Integer[] ap = attacker.getPosition();
        diffPosition = new Double[]{(double)(pp[0] - ap[0]) * MULTIPLIER, 
                                    (double)(pp[1] - ap[1] - 10) * MULTIPLIER};
    }
    
}
