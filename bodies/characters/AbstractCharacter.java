package bodies.characters;

import java.util.ArrayList;

import actions.ISpellActionFactory;
import bodies.characters.HumanPlayer.Keys;
import actions.AbstractSpellAction;
import actions.AbstractSpellcard;
import game.model.Pair;

public abstract class AbstractCharacter {
    public enum Combo {Forward, Back, Up, Down, Melee, Weak, Strong, Dash}

    public ArrayList<Pair<Combo[], ISpellActionFactory>> comboMapping
        = new ArrayList<Pair<Combo[], ISpellActionFactory>>();

    private Pair<Combo[],ISpellActionFactory> makePair(Combo[] keys, ISpellActionFactory combo) {
        return new Pair<Combo[],ISpellActionFactory>(keys, combo);
    }

    protected ISpellActionFactory dashFactory;

    protected AbstractSpellcard[] deck = new AbstractSpellcard[20];
    protected ArrayList<AbstractSpellcard> library = new ArrayList<AbstractSpellcard>();

    public void setup() {
        // appending to end
        Combo[][] combos = new Combo[][] {
            {Combo.Down, Combo.Forward, Combo.Melee},   //0 v > m
            {Combo.Down, Combo.Forward, Combo.Weak},    //1 v > w
            {Combo.Down, Combo.Forward, Combo.Strong},  //2 v > s

            {Combo.Down, Combo.Back, Combo.Melee},      //3 v < m
            {Combo.Down, Combo.Back, Combo.Weak},       //4 v < w
            {Combo.Down, Combo.Back, Combo.Strong},     //5 v < s

            {Combo.Up, Combo.Weak},                     //6 ^ w
            {Combo.Down, Combo.Weak},                   //7 v w
            {Combo.Back, Combo.Weak},                   //8 < w
            {Combo.Forward, Combo.Weak},                //9 > w

            {Combo.Up, Combo.Strong},                   //10 ^ s
            {Combo.Down, Combo.Strong},                 //11 v s
            {Combo.Back, Combo.Strong},                 //12 < s
            {Combo.Forward, Combo.Strong},              //13 > s

            {Combo.Up, Combo.Melee},                    //14 ^ m
            {Combo.Down, Combo.Melee},                  //15 v m
            {Combo.Back, Combo.Melee},                  //16 < m    (block)
            {Combo.Forward, Combo.Melee},               //17 > m

            {Combo.Melee},                              //18 m
            {Combo.Weak},                               //19 w
            {Combo.Strong},                             //20 s

            {Combo.Forward, Combo.Dash},                //21 > d
            {Combo.Back, Combo.Dash},                   //22 < d
            {Combo.Up, Combo.Dash},                     //23 ^ d
            {Combo.Down, Combo.Dash},                   //24 v d
            {Combo.Forward, Combo.Down, Combo.Dash},    //25 > v d
            {Combo.Forward, Combo.Up, Combo.Dash},      //26 > ^ d
            {Combo.Back,  Combo.Down, Combo.Dash},      //27 < v d
            {Combo.Back,  Combo.Up, Combo.Dash},        //28 < ^ d
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

    public static Keys keyOfCombo(Combo c, boolean facingLeft) {
        switch (c) {
            case Back:
                return facingLeft ? Keys.Right : Keys.Left;
            case Down:
                return Keys.Down;
            case Forward:
                return facingLeft ? Keys.Left : Keys.Right;
            case Melee:
                return Keys.Melee;
            case Strong:
                return Keys.Strong;
            case Up:
                return Keys.Up;
            case Weak:
                return Keys.Weak;
            case Dash:
                return Keys.Dash;
        }
        throw new RuntimeException("No! being here is bad!");
    }

    public static Combo keyToCombo(Keys key, boolean facingLeft) {
        if (key == null) return null;
        switch(key) {
            case Up:
                return Combo.Up;
            case Down:
                return Combo.Down;
            case Left:
                return facingLeft ? Combo.Forward : Combo.Back;
            case Right:
                return facingLeft ? Combo.Back : Combo.Forward;
            case Weak:
                return Combo.Weak;
            case Strong:
                return Combo.Strong;
            case Melee:
                return Combo.Melee;
            case Dash:
                return Combo.Dash;
            default:
                return null;
        }
    }

    public AbstractCharacter(AbstractPlayer player, String name) {
        setup();
        this.player = player;
        this.name = name;
    }

    protected abstract void setupSpellActions();
    protected abstract void setupSpellCards();
    public AbstractSpellAction getDash() {
        return this.dashFactory.newSpell();
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
