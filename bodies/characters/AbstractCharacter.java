package bodies.characters;

import java.util.ArrayList;

import actions.AbstractSpellActionFactory;
import game.model.scenario.Battle;
import game.model.Pair;

public abstract class AbstractCharacter {
    public enum Combo {Forward, Back, Up, Down, Melee, Weak, Strong}

    public ArrayList<Pair<Combo[], AbstractSpellActionFactory>> comboMapping
        = new ArrayList<Pair<Combo[], AbstractSpellActionFactory>>();

    private Pair<Combo[],AbstractSpellActionFactory> makePair(Combo[] keys, AbstractSpellActionFactory combo) {
        return new Pair<Combo[],AbstractSpellActionFactory>(keys, combo);
    }

    public void setup() {
        // appending to end
        Combo[][] combos = new Combo[][] {
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
    protected long timeout;
    protected Battle bat;

    protected int meleeCombo = 3;

    public AbstractCharacter(AbstractPlayer player, Battle bat) {
        setup();
        this.player = player;
        this.bat = bat;
    }

    public AbstractPlayer getPlayer() {
        return player;
    }

    public void resetTimeout(long timeDiff) {
        timeout = timeDiff;
    }

    public void decrementTimeout(long timeDiff) {
        this.timeout = Math.max(0, this.timeout - timeDiff);
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
