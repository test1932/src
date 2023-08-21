package bodies.characters.Sakuya;

import game.model.scenario.AbstractDialogue;
import game.model.scenario.AbstractScenario;
import game.model.scenario.Battle;

public class SakuyaScenario extends AbstractScenario {

    public SakuyaScenario(Battle battle, AbstractDialogue preBattle, AbstractDialogue postBattle,
            AbstractScenario nextScenario) {
        super(battle, preBattle, postBattle, nextScenario);
    }
    
}
