package menu.settings.config;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;

public class MenuConfig extends AbstractMenu {

    public MenuConfig(Game game) {
        super(game);
        options = new AbstractOption[]{};
        selectedIndex = 0;
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
