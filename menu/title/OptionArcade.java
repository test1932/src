package menu.title;

import game.Game;
import game.Game.GameState;
import menu.AbstractMenu;
import menu.AbstractOption;

public class OptionArcade extends AbstractOption {

    public OptionArcade(Game game, AbstractMenu parent) {
        super(game, parent, "Arcade");
    }

    @Override
    public void statefulHandler() {
        game.gameState = GameState.Playing;
        game.setCurMenu(null);
    }
}
