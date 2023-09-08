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
        double x = (double)(pp[0] - ap[0]) * MULTIPLIER ;
        double y = (double)(pp[1] - ap[1] - 20) * MULTIPLIER;
        double scalar = Math.abs(3 / Math.max(Math.abs(x), Math.abs(y)));
        diffPosition = new Double[]{x * scalar, 
                                    y * scalar};
    }
    
}
