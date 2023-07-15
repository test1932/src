package menu.settings;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;

public class MenuSettings extends AbstractMenu{
    public MenuSettings(Game game) {
        super(game);
        options = new AbstractOption[]{
            new OptionConfig(null)
        };
        selectedIndex = 0;
    }

    @Override
    public void animateSelectionChange() {
        // TODO Anumation
    }
    
}
