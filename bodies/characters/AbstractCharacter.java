package bodies.characters;

import java.util.ArrayList;

import actions.AbstractSpellAction;
import game.model.Pair;

public abstract class AbstractCharacter {
    public enum Combo {Forward, Back, Up, Down, Melee, Weak, Strong}

    public ArrayList<Pair<Combo[], AbstractSpellAction>> comboMapping
        = new ArrayList<Pair<Combo[], AbstractSpellAction>>();

    private Pair<Combo[],AbstractSpellAction> makePair(Combo[] keys, AbstractSpellAction combo) {
        return new Pair<Combo[],AbstractSpellAction>(keys, combo);
    }

    public void setup() {
        // appending to end
        Combo[][] combos = new Combo[][] {
            {Combo.Down, Combo.Down, Combo.Weak},
            {Combo.Down, Combo.Down, Combo.Strong},

            {Combo.Down, Combo.Forward, Combo.Melee},
            {Combo.Down, Combo.Forward, Combo.Weak},
            {Combo.Down, Combo.Forward, Combo.Strong},

            {Combo.Down, Combo.Back, Combo.Melee},
            {Combo.Down, Combo.Back, Combo.Weak},
            {Combo.Down, Combo.Back, Combo.Strong},

            {Combo.Up, Combo.Weak},
            {Combo.Down, Combo.Weak},
            {Combo.Back, Combo.Weak},
            {Combo.Forward, Combo.Weak},

            {Combo.Up, Combo.Strong},
            {Combo.Down, Combo.Strong},
            {Combo.Back, Combo.Strong},
            {Combo.Forward, Combo.Strong},

            {Combo.Up, Combo.Melee},
            {Combo.Down, Combo.Melee},
            {Combo.Back, Combo.Melee},
            {Combo.Forward, Combo.Melee},

            {Combo.Melee},
            {Combo.Weak},
            {Combo.Strong}
        };

        for (int i = 0; i < combos.length; i++) {
            comboMapping.add(makePair(combos[i], null));
        }
    }

    private AbstractPlayer player;
    protected long weakTimeout = 300l;
    protected long strongTimeout = 1000l;
    protected long meleeTimeout = 500l;
    protected long timeout;

    protected int meleeCombo = 3;

    public AbstractCharacter(AbstractPlayer player) {
        setup();
        this.player = player;
    }

    public AbstractPlayer getPlayer() {
        return player;
    }

    public void resetStrongTimeout() {
        timeout += strongTimeout;
    }

    public void resetWeakTimeout() {
        timeout += weakTimeout;
    }

    public void resetMeleeTimeout() {
        timeout += meleeTimeout;
    }

    public void decrementTimeout(long timeDiff) {
        this.timeout -= timeDiff;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
