package menu.title;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;

public class MenuTitle extends AbstractMenu {

    public MenuTitle(Game game) {
        super(game);
        options = new AbstractOption[]{
            new OptionQuickGame(game),
            new OptionSettings(game)
        };
        selectedIndex = 0;
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
