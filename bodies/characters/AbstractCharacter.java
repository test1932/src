package bodies.characters;

import java.util.ArrayList;

import actions.AbstractSpellcard;
import actions.ISpellActionFactory;
import game.model.Pair;

public abstract class AbstractCharacter {
    public enum Combo {Forward, Back, Up, Down, Melee, Weak, Strong}

    public ArrayList<Pair<Combo[], ISpellActionFactory>> comboMapping
        = new ArrayList<Pair<Combo[], ISpellActionFactory>>();

    private Pair<Combo[],ISpellActionFactory> makePair(Combo[] keys, ISpellActionFactory combo) {
        return new Pair<Combo[],ISpellActionFactory>(keys, combo);
    }

    protected AbstractSpellcard[] deck = new AbstractSpellcard[20];
    protected ArrayList<AbstractSpellcard> library = new ArrayList<AbstractSpellcard>();

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
    private String name;

    protected long timeout;
    protected long spellTimeout;
    protected int meleeCombo = 3;

    public AbstractCharacter(AbstractPlayer player, String name) {
        setup();
        this.player = player;
        this.name = name;
    }

    public AbstractPlayer getPlayer() {
        return player;
    }

    public void setPlayer(AbstractPlayer player) {
        this.player = player;
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


    public void resetSpellTimeout(long timeDiff) {
        spellTimeout = timeDiff;
    }

    public void decrementSpellTimeout(long timeDiff) {
        this.spellTimeout = Math.max(0, this.spellTimeout - timeDiff);
    }

    public long getSpellTimeout() {
        return spellTimeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public AbstractSpellcard[] getDeck() {
        return deck;
    }

    public void setDeck(AbstractSpellcard[] deck) {
        this.deck = deck;
    }

    public void replaceCard(AbstractSpellcard card, int index) {
        this.deck[index] = card;
    }

    public String getName() {
        return name;
    }
}
