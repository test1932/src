package menu.pause;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;

public class MenuPause extends AbstractMenu {

    public MenuPause(Game game) {
        super(game);
        options = new AbstractOption[]{};
        selectedIndex = 0;
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
