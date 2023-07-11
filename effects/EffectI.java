package effects;

import bodies.characters.PlayerA;

public interface EffectI {
    public boolean effectIsOver();
    public void reduceTime(Long timeDiff);
    public void applyEffect(PlayerA player);
}
