package effects;

import bodies.characters.AbstractPlayer;

public interface IEffect {
    public boolean effectIsOver();
    public void reduceTime(Long timeDiff);
    public void applyEffect(AbstractPlayer player);
}
