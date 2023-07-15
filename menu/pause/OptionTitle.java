package menu.pause;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;

public class OptionTitle extends AbstractOption {

    public OptionTitle(Game game, AbstractMenu parent) {
        super(game, parent, "Back to title");
    }

    @Override
    public void statefulHandler() {
        game.setCurMenu(game.titleMenu);
        game.notifyObservers();
    }
    
}
