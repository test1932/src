package bodies.characters.Sakuya;

import bodies.characters.AbstractCharacter;
import bodies.characters.AbstractPlayer;
import bodies.characters.Sakuya.spellActions.TestSpellActionFactory;
import game.model.scenario.Battle;

public class Sakuya extends AbstractCharacter {
    public Sakuya(AbstractPlayer player, Battle bat) {
        super(player, bat);
        setupSpellActions();
    }

    private void setupSpellActions() {
        for (int i = 0; i < comboMapping.size(); i++) {
            comboMapping.get(i).snd = new TestSpellActionFactory(getPlayer(), bat);   
        }
    }
}
