package menu.title;

import game.Game;
import game.Game.GameState;
import menu.AbstractMenu;
import menu.AbstractOption;

public class OptionQuickGame extends AbstractOption {

    public OptionQuickGame(Game game, AbstractMenu parent) {
        super(game, parent, "Quick Game");
    }

    @Override
    public void statefulHandler() {
        game.setCurMenu(null);
        game.gameState = GameState.Playing;
    }
}
