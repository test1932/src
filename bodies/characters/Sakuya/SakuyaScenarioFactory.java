package bodies.characters.Sakuya;

import game.model.scenario.Dialogue;

import java.awt.image.BufferedImage;

import bodies.characters.Utsuho.Utsuho;
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
            Sakuya.setupCharacter();
            Utsuho.setupCharacter();
            setPreBattle();
            setPostBattle();
        }

        private void setPreBattle() {
            BufferedImage[] spritesSakuya = Sakuya.getSprites();
            BufferedImage[] spritesUtsuho = Utsuho.getSprites();
            Dialogue dialogue1 = new Dialogue(spritesSakuya[0], spritesUtsuho[1], "sprite 0 is happy", false);
            Dialogue dialogue2 = new Dialogue(spritesSakuya[1], spritesUtsuho[1], "sprite 1 is angry", true);
            dialogue1.setNext(dialogue2);
            Dialogue dialogue3 = new Dialogue(spritesSakuya[1], spritesUtsuho[2], "sprite 2 is sad", false);
            dialogue2.setNext(dialogue3);
            Dialogue dialogue4 = new Dialogue(spritesSakuya[3], spritesUtsuho[2], "sprite 3 is overjoyed!", true);
            dialogue3.setNext(dialogue4);
            Dialogue dialogue5 = new Dialogue(spritesSakuya[3], spritesUtsuho[3], "sprite 4 is suprised!", false);
            dialogue4.setNext(dialogue5);
            Dialogue dialogue6 = new Dialogue(spritesSakuya[5], spritesUtsuho[3], "sprite 5 is distraught!", true);
            dialogue5.setNext(dialogue6);
            
            this.preBattle = dialogue1;
        }

        private void setPostBattle() {
            BufferedImage[] spritesSakuya = Sakuya.getSprites();
            BufferedImage[] spritesUtsuho = Utsuho.getSprites();
            Dialogue dialogue1 = new Dialogue(spritesSakuya[0], spritesUtsuho[1], "sprite 0 is happy", false);
            Dialogue dialogue2 = new Dialogue(spritesSakuya[1], spritesUtsuho[1], "sprite 1 is angry", true);
            dialogue1.setNext(dialogue2);
            Dialogue dialogue3 = new Dialogue(spritesSakuya[1], spritesUtsuho[2], "sprite 2 is sad", false);
            dialogue2.setNext(dialogue3);
            Dialogue dialogue4 = new Dialogue(spritesSakuya[3], spritesUtsuho[2], "sprite 3 is overjoyed!", true);
            dialogue3.setNext(dialogue4);
            Dialogue dialogue5 = new Dialogue(spritesSakuya[3], spritesUtsuho[3], "sprite 4 is suprised!", false);
            dialogue4.setNext(dialogue5);
            Dialogue dialogue6 = new Dialogue(spritesSakuya[5], spritesUtsuho[3], "sprite 5 is distraught!", true);
            dialogue5.setNext(dialogue6);
            
            this.postBattle = dialogue1;
        }
    }

    @Override
    public AbstractScenario makeScenario() {
        return new SakuyaScenario(new Battle(cont), next);
    }

    
}
