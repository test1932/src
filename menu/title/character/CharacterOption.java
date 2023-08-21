package menu.title.character;

import bodies.characters.AbstractCharacter;
import bodies.characters.AbstractPlayer;
import game.Game;
import game.Game.GameState;
import game.model.scenario.AbstractScenario;
import menu.AbstractMenu;
import menu.AbstractOption;

public class CharacterOption extends AbstractOption {
    protected AbstractCharacter character;
    protected AbstractPlayer player;
    protected AbstractScenario scenario;

    public CharacterOption(Game game, AbstractMenu parent, String optionName, AbstractPlayer player, 
            AbstractCharacter character, AbstractScenario scenario) {
        super(game, parent, optionName);
        this.player = player;
        this.character = character;
        this.scenario = scenario;
    }
    
    public AbstractCharacter getCharacter() {
        return character;
    }

    public void setCharacter(AbstractCharacter character) {
        this.character = character;
    }

    public AbstractPlayer getPlayer() {
        return player;
    }

    public void setPlayer(AbstractPlayer player) {
        this.player = player;
    }

    @Override
    public void statefulHandler() {
        player.setCharacter(character);
        game.getCont().setScenario(this.scenario);
        game.gameState = GameState.Playing;
        game.setCurMenu(null);
    }
}
