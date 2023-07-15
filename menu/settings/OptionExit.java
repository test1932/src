package menu.settings;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;

public class OptionExit extends AbstractOption {

    public OptionExit(Game game, AbstractMenu parent) {
        super(game, parent, "Exit");
    }

    @Override
    public void statefulHandler() {
        System.exit(0);
    }
    
}
