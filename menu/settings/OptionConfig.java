package menu.settings;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.settings.config.MenuConfig;


public class OptionConfig extends AbstractOption {
    private MenuConfig config;

    public OptionConfig(Game game, AbstractMenu parent) {
        super(game, parent, "Config");
        config = new MenuConfig(game, parent);
    }

    @Override
    public void statefulHandler() {
        game.setCurMenu(config);
    }
}
