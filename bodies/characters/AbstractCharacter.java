package bodies.characters;

import java.util.ArrayList;

import actions.AbstractSpellActionFactory;
import actions.spellcard.AbstractSpellcard;
import game.model.Pair;

public abstract class AbstractCharacter {
    public enum Combo {Forward, Back, Up, Down, Melee, Weak, Strong}

    public ArrayList<Pair<Combo[], AbstractSpellActionFactory>> comboMapping
        = new ArrayList<Pair<Combo[], AbstractSpellActionFactory>>();

    private Pair<Combo[],AbstractSpellActionFactory> makePair(Combo[] keys, AbstractSpellActionFactory combo) {
        return new Pair<Combo[],AbstractSpellActionFactory>(keys, combo);
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
