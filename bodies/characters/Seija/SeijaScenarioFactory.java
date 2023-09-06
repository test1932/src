package bodies.characters.Seija;

import java.awt.image.BufferedImage;

import bodies.characters.Utsuho.Utsuho;
import game.model.Controller;
import game.model.scenario.AbstractScenario;
import game.model.scenario.AbstractScenarioFactory;
import game.model.scenario.Battle;
import game.model.scenario.Dialogue;

public class SeijaScenarioFactory extends AbstractScenarioFactory{

    public static class SeijaScenario extends AbstractScenario {

        public SeijaScenario(Battle battle, AbstractScenario nextScenario) {
            super(battle, nextScenario);
            Seija.setupCharacter();
            Utsuho.setupCharacter();
            setPreBattle();
            setPostBattle();
        }

        private void setPreBattle() {
            BufferedImage[] spritesSeija = Seija.getSprites();
            BufferedImage[] spritesUtsuho = Utsuho.getSprites();
            Dialogue dialogue1 = new Dialogue(spritesSeija[0], spritesUtsuho[1], "sprite 0 is happy", false);
            Dialogue dialogue2 = new Dialogue(spritesSeija[1], spritesUtsuho[1], "sprite 1 is angry", true);
            dialogue1.setNext(dialogue2);
            Dialogue dialogue3 = new Dialogue(spritesSeija[1], spritesUtsuho[2], "sprite 2 is sad", false);
            dialogue2.setNext(dialogue3);
            Dialogue dialogue4 = new Dialogue(spritesSeija[3], spritesUtsuho[2], "sprite 3 is overjoyed!", true);
            dialogue3.setNext(dialogue4);
            Dialogue dialogue5 = new Dialogue(spritesSeija[3], spritesUtsuho[3], "sprite 4 is suprised!", false);
            dialogue4.setNext(dialogue5);
            Dialogue dialogue6 = new Dialogue(spritesSeija[4], spritesUtsuho[3], "sprite 5 is distraught!", true);
            dialogue5.setNext(dialogue6);
            
            this.preBattle = dialogue1;
        }

        private void setPostBattle() {
            BufferedImage[] spritesSeija = Seija.getSprites();
            BufferedImage[] spritesUtsuho = Utsuho.getSprites();
            Dialogue dialogue1 = new Dialogue(spritesSeija[0], spritesUtsuho[1], "sprite 0 is happy", false);
            Dialogue dialogue2 = new Dialogue(spritesSeija[1], spritesUtsuho[1], "sprite 1 is angry", true);
            dialogue1.setNext(dialogue2);
            Dialogue dialogue3 = new Dialogue(spritesSeija[1], spritesUtsuho[2], "sprite 2 is sad", false);
            dialogue2.setNext(dialogue3);
            Dialogue dialogue4 = new Dialogue(spritesSeija[3], spritesUtsuho[2], "sprite 3 is overjoyed!", true);
            dialogue3.setNext(dialogue4);
            Dialogue dialogue5 = new Dialogue(spritesSeija[3], spritesUtsuho[3], "sprite 4 is suprised!", false);
            dialogue4.setNext(dialogue5);
            Dialogue dialogue6 = new Dialogue(spritesSeija[4], spritesUtsuho[3], "sprite 5 is distraught!", true);
            dialogue5.setNext(dialogue6);
            
            this.postBattle = dialogue1;
        }

    }

    public SeijaScenarioFactory(AbstractScenario nextScenario, Controller cont) {
        super(nextScenario, cont);
    }

    @Override
    public AbstractScenario makeScenario() {
        return new SeijaScenario(new Battle(cont), null);
    }
    
}
