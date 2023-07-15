package menu.title;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;

public class MenuTitle extends AbstractMenu {

    public MenuTitle(Game game, AbstractMenu prevMenu) {
        super(game, "Title", prevMenu);
        options = new AbstractOption[]{
            new OptionQuickGame(game, this),
            new OptionSettings(game, this)
        };
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
