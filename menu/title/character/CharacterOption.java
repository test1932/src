package menu.title.character;

import bodies.characters.AbstractCharacter;
import bodies.characters.AbstractPlayer;
import game.Game;
import game.Game.GameState;
import game.model.scenario.AbstractScenarioFactory;
import menu.AbstractMenu;
import menu.AbstractOption;

public class CharacterOption extends AbstractOption {
    protected AbstractCharacter character;
    protected AbstractPlayer player;
    protected AbstractScenarioFactory scenario;

    public CharacterOption(Game game, AbstractMenu parent, String optionName, AbstractPlayer player, 
            AbstractCharacter character, AbstractScenarioFactory scenarioFactory) {
        super(game, parent, optionName);
        this.player = player;
        this.character = character;
        this.scenario = scenarioFactory;
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
        game.getCont().setScenario(this.scenario.makeScenario());
        game.gameState = GameState.Playing;
        game.setCurMenu(null);

        game.getCont().getBattle().bodiesLock.lock();
        for (AbstractPlayer player : game.getCont().players) {
            player.reset();
        }
        game.getCont().getBattle().bodiesLock.unlock();
    }
}
