package bodies.characters.Seija;

import java.awt.image.BufferedImage;

import bodies.characters.Sakuya.Sakuya;
import bodies.characters.Utsuho.Utsuho;
import game.model.Controller;
import game.model.scenario.AbstractScenario;
import game.model.scenario.AbstractScenarioFactory;
import game.model.scenario.Battle;
import game.model.scenario.Dialogue;

public class SeijaScenarioFactory extends AbstractScenarioFactory{

    public class SeijaScenario extends AbstractScenario {

        public SeijaScenario(Battle battle, AbstractScenario nextScenario, Controller cont) {
            super(battle, nextScenario, cont);
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
            
            this.postBattle = dialogue1;
        }

        @Override
        public void setCharacters() {
            cont.players[0].setCharacter(new Seija(cont.players[0]));
            cont.players[1].setCharacter(new Sakuya(cont.players[1]));
        }

    }

    public SeijaScenarioFactory(Controller cont) {
        super(cont);
    }

    @Override
    public AbstractScenario makeScenario() {
        return new SeijaScenario(new Battle(cont), null, cont);
    }
    
}
