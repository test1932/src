package menu.title;

import game.Game;
import game.Game.GameState;
import menu.AbstractMenu;
import menu.AbstractOption;

public class OptionStory extends AbstractOption {

    public OptionStory(Game game, AbstractMenu parent) {
        super(game, parent, "Story");
    }

    //TODO
    @Override
    public void statefulHandler() {
        game.gameState = GameState.Playing;
        game.setCurMenu(null);
    }
}
