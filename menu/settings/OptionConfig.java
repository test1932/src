package menu.settings;

import bodies.characters.HumanPlayer;
import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.settings.config.MenuConfig;


public class OptionConfig extends AbstractOption {
    private MenuConfig config;

    public OptionConfig(Game game, AbstractMenu parent, HumanPlayer player, int playerNo) {
        super(game, parent, "Config Player " + String.valueOf(playerNo));
        config = new MenuConfig(game, parent, player);
    }

    @Override
    public void statefulHandler() {
        game.setCurMenu(config);
    }
}
