package menu.settings;

import game.Game;
import menu.AbstractOption;

public class OptionConfig extends AbstractOption{

    public OptionConfig(Game game) {
        super(game);
        displayText = "Config";
    }

    @Override
    public void statefulHandler() {
        // TODO
    }   
}
