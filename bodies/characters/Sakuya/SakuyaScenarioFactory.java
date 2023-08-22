package bodies.characters.Sakuya;

import game.model.scenario.Dialogue;

import java.awt.image.BufferedImage;

import game.model.Controller;
import game.model.scenario.AbstractScenario;
import game.model.scenario.AbstractScenarioFactory;
import game.model.scenario.Battle;

public class SakuyaScenarioFactory extends AbstractScenarioFactory {
    public SakuyaScenarioFactory(AbstractScenario nextScenario, Controller cont) {
        super(nextScenario, cont);
    }

    public class SakuyaScenario extends AbstractScenario {

        public SakuyaScenario(Battle battle, AbstractScenario nextScenario) {
            super(battle, nextScenario);
            setPreBattle();
            setPostBattle();
        }

        private void setPreBattle() {
            BufferedImage[] sprites = Sakuya.getSprites();
            this.preBattle = new Dialogue(sprites[0], "hello", true);
        }

        private void setPostBattle() {
            BufferedImage[] sprites = Sakuya.getSprites();
            this.postBattle = new Dialogue(sprites[0], "hello", false);
        }
    }

    @Override
    public AbstractScenario makeScenario() {
        return new SakuyaScenario(new Battle(cont), next);
    }

    
}
