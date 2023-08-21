package bodies.characters.Sakuya;

import bodies.characters.AbstractCharacter;
import bodies.characters.AbstractPlayer;
import bodies.characters.Sakuya.spellActions.TestSpellActionFactory;

public class Sakuya extends AbstractCharacter {
    public Sakuya(AbstractPlayer player) {
        super(player, "Sakuya");
        setupSpellActions();
    }

    private void setupSpellActions() {
        for (int i = 0; i < comboMapping.size(); i++) {
            comboMapping.get(i).snd = new TestSpellActionFactory(getPlayer());   
        }
    }
}
