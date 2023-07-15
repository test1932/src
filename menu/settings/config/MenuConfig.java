package menu.settings.config;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.OptionBack;

public class MenuConfig extends AbstractMenu {
    public MenuConfig(Game game, AbstractMenu prevMenu) {
        super(game, "Config", prevMenu);
        options = new AbstractOption[]{
            new OptionBack(game, this)
        };
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
