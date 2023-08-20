package actions.spellcard;

import actions.AbstractSpellAction;
import bodies.characters.AbstractPlayer;

public abstract class AbstractSpellcard {
    protected AbstractSpellAction spellAction;
    protected int cost = 1;
    private AbstractPlayer owner;

    public AbstractSpellcard(AbstractPlayer player) {
        this.owner = player;
    }

    public AbstractSpellAction getSpellAction() {
        return spellAction;
    }

    public void setSpellAction(AbstractSpellAction spellAction) {
        this.spellAction = spellAction;
    }

    public void activateSpellCard() {
        AbstractSpellcard[] hand = owner.getHand();
        for (int i = 0; i < cost; i++) {
            hand[i] = null;
        }
        spellAction.start();
    }

    public AbstractPlayer getOwner() {
        return owner;
    }
}
