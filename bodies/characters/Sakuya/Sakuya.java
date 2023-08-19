package bodies.characters.Sakuya;

import bodies.characters.AbstractCharacter;
import bodies.characters.AbstractPlayer;
import bodies.characters.Sakuya.spellActions.TestSpellAction;

public class Sakuya extends AbstractCharacter {
    public Sakuya(AbstractPlayer player) {
        super(player);
        setupSpellActions();
    }

    private void setupSpellActions() {
        comboMapping.get(21).snd = new TestSpellAction(getPlayer());
    }
}
