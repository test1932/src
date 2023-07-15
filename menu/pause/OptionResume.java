package menu.pause;

import game.Game;
import game.Game.GameState;
import menu.AbstractMenu;
import menu.AbstractOption;

public class OptionResume extends AbstractOption {

    public OptionResume(Game game, AbstractMenu parent) {
        super(game, parent, "Resume");
    }

    @Override
    public void statefulHandler() {
        game.gameState = GameState.Playing;
        game.setCurMenu(null);
    }
    
}
