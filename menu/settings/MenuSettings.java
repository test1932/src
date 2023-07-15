package menu.settings;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.OptionBack;

public class MenuSettings extends AbstractMenu{
    public MenuSettings(Game game, AbstractMenu prevMenu) {
        super(game, "Settings", prevMenu);
        options = new AbstractOption[]{
            new OptionConfig(game, this),
            new OptionBack(game, this)
        };
    }

    @Override
    public void animateSelectionChange() {
        // TODO Anumation
    }
    
}
