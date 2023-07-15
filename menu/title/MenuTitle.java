package menu.title;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.settings.OptionExit;

public class MenuTitle extends AbstractMenu {

    public MenuTitle(Game game, AbstractMenu prevMenu) {
        super(game, "Title", prevMenu);
        options = new AbstractOption[]{
            new OptionQuickGame(game, this),
            new OptionSettings(game, this),
            new OptionExit(game, this)
        };
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
