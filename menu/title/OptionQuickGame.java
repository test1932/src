package menu.title;

import game.Game;
import game.Game.GameState;
import menu.AbstractOption;

public class OptionQuickGame extends AbstractOption {

    public OptionQuickGame(Game game) {
        super(game);
        displayText = "Quick Game";
    }

    @Override
    public void statefulHandler() {
        game.setCurMenu(null);
        game.gameState = GameState.Playing;
    }
}
