package menu;

import game.Game;

public class OptionBack extends AbstractOption {

    public OptionBack(Game game, AbstractMenu parent) {
        super(game, parent, "Back");
    }

    @Override
    public void statefulHandler() {
        game.setCurMenu(game.getCurMenu().getPrevMenu());
    }
    
}
