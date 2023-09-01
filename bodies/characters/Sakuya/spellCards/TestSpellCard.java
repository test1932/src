package bodies.characters.Sakuya.spellCards;

import actions.AbstractSpellAction;
import actions.AbstractSpellcard;
import bodies.characters.AbstractPlayer;
import bodies.characters.Sakuya.spellActions.TestSpellActionFactory;

public class TestSpellCard extends AbstractSpellcard {

    public TestSpellCard(AbstractPlayer player) {
        super(player);
    }

    @Override
    public AbstractSpellAction newSpellAction() {
        return new TestSpellActionFactory.TestSpellAction(getOwner());
    }

    @Override
    public void animateSpellCard() {
        // TODO animation
    }

}