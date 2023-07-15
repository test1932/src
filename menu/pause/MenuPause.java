package menu.pause;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;

public class MenuPause extends AbstractMenu {

    public MenuPause(Game game, AbstractMenu prevMenu) {
        super(game, "Pause", prevMenu);
        options = new AbstractOption[]{};
        selectedIndex = 0;
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
