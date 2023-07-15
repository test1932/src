package menu.title;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.settings.MenuSettings;

public class OptionSettings extends AbstractOption {
    private MenuSettings settings;

    public OptionSettings(Game game, AbstractMenu parent) {
        super(game, parent, "Settings");
        settings = new MenuSettings(game, parent);
    }

    @Override
    public void statefulHandler() {
        game.setCurMenu(settings);
    }
}