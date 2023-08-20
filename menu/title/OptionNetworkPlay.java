package menu.title;

import game.Game;
import game.Game.GameState;
import menu.AbstractMenu;
import menu.AbstractOption;

public class OptionNetworkPlay extends AbstractOption {

    public OptionNetworkPlay(Game game, AbstractMenu parent) {
        super(game, parent, "Network Play");
    }

    @Override
    public void statefulHandler() {
        game.gameState = GameState.Playing;
        game.setCurMenu(null);
    }
}
