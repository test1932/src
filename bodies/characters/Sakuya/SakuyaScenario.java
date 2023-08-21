package bodies.characters.Sakuya;

import game.model.scenario.Dialogue;
import game.model.scenario.AbstractScenario;
import game.model.scenario.Battle;

public class SakuyaScenario extends AbstractScenario {

    public SakuyaScenario(Battle battle, AbstractScenario nextScenario) {
        super(battle, nextScenario);
        setPreBattle();
        setPostBattle();
    }

    private void setPreBattle() {
        this.preBattle = new Dialogue(null, "hello", true);
    }

    private void setPostBattle() {
        this.postBattle = new Dialogue(null, "hello", false);
    }
}
