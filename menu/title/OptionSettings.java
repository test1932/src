package menu.title;

import game.Game;
import menu.AbstractOption;
import menu.settings.MenuSettings;

public class OptionSettings extends AbstractOption {
    private MenuSettings settings;

    public OptionSettings(Game game) {
        super(game);
        displayText = "Settings";
        settings = new MenuSettings(game);
    }

    @Override
    public void statefulHandler() {
        game.setCurMenu(settings);
    }
}