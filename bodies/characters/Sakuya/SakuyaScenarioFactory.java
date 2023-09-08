package bodies.characters.Sakuya;

import game.model.scenario.Dialogue;

import java.awt.image.BufferedImage;

import bodies.characters.Seija.Seija;
import bodies.characters.Utsuho.Utsuho;
import game.model.Controller;
import game.model.scenario.AbstractScenario;
import game.model.scenario.AbstractScenarioFactory;
import game.model.scenario.Battle;

public class SakuyaScenarioFactory extends AbstractScenarioFactory {
    public SakuyaScenarioFactory(Controller cont) {
        super(cont);
    }

    public class SakuyaScenarioFirst extends AbstractScenario {

        public SakuyaScenarioFirst(Battle battle, AbstractScenario nextScenario, Controller cont) {
            super(battle, nextScenario, cont);
            Sakuya.setupCharacter();
            Utsuho.setupCharacter();
            setPreBattle();
            setPostBattle();
        }

        private void setPreBattle() {
            BufferedImage[] spritesSakuya = Sakuya.getSprites();
            BufferedImage[] spritesUtsuho = Utsuho.getSprites();
            Dialogue dialogue1 = new Dialogue(spritesSakuya[0], spritesUtsuho[1], "<dialogue in progress>", false);
            Dialogue dialogue2 = new Dialogue(spritesSakuya[1], spritesUtsuho[1], "<dialogue in progress>", true);
            dialogue1.setNext(dialogue2);
            Dialogue dialogue3 = new Dialogue(spritesSakuya[1], spritesUtsuho[2], "<dialogue in progress>", false);
            dialogue2.setNext(dialogue3);
            
            this.preBattle = dialogue1;
        }

        private void setPostBattle() {
            BufferedImage[] spritesSakuya = Sakuya.getSprites();
            BufferedImage[] spritesUtsuho = Utsuho.getSprites();
            Dialogue dialogue1 = new Dialogue(spritesSakuya[0], spritesUtsuho[1], "<dialogue in progress>", false);
            Dialogue dialogue2 = new Dialogue(spritesSakuya[1], spritesUtsuho[1], "<dialogue in progress>", true);
            dialogue1.setNext(dialogue2);
            Dialogue dialogue3 = new Dialogue(spritesSakuya[1], spritesUtsuho[2], "<dialogue in progress>", false);
            dialogue2.setNext(dialogue3);
            
            this.postBattle = dialogue1;
        }

        @Override
        public void setCharacters() {
            cont.players[0].setCharacter(new Sakuya(cont.players[0]));
            cont.players[1].setCharacter(new Sakuya(cont.players[1]));
        }
    }

    public class SakuyaScenarioSecond extends AbstractScenario {

        public SakuyaScenarioSecond(Battle battle, AbstractScenario nextScenario, Controller cont) {
            super(battle, nextScenario, cont);
            Sakuya.setupCharacter();
            Seija.setupCharacter();
            setPreBattle();
            setPostBattle();
        }

        private void setPreBattle() {
            BufferedImage[] spritesSakuya = Sakuya.getSprites();
            BufferedImage[] spritesSeija = Seija.getSprites();
            Dialogue dialogue1 = new Dialogue(spritesSakuya[0], spritesSeija[1], "<dialogue in progress>", false);
            Dialogue dialogue2 = new Dialogue(spritesSakuya[1], spritesSeija[1], "<dialogue in progress>", true);
            dialogue1.setNext(dialogue2);
            Dialogue dialogue3 = new Dialogue(spritesSakuya[1], spritesSeija[2], "<dialogue in progress>", false);
            dialogue2.setNext(dialogue3);
            
            this.preBattle = dialogue1;
        }

        private void setPostBattle() {
            BufferedImage[] spritesSakuya = Sakuya.getSprites();
            BufferedImage[] spritesSeija = Seija.getSprites();
            Dialogue dialogue1 = new Dialogue(spritesSakuya[0], spritesSeija[1], "<dialogue in progress>", false);
            Dialogue dialogue2 = new Dialogue(spritesSakuya[1], spritesSeija[1], "<dialogue in progress>", true);
            dialogue1.setNext(dialogue2);
            Dialogue dialogue3 = new Dialogue(spritesSakuya[1], spritesSeija[2], "<dialogue in progress>", false);
            dialogue2.setNext(dialogue3);
            
            this.postBattle = dialogue1;
        }

        @Override
        public void setCharacters() {
            cont.players[0].setCharacter(new Sakuya(cont.players[0]));
            cont.players[1].setCharacter(new Seija(cont.players[1]));
        }
    }

    @Override
    public AbstractScenario makeScenario() {
        AbstractScenario second = new SakuyaScenarioSecond(new Battle(cont), null, cont);
        return new SakuyaScenarioFirst(new Battle(cont), second, cont);
    }
}
