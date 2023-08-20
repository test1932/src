package menu.pause;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;

public class MenuPause extends AbstractMenu {

    public MenuPause(Game game, AbstractMenu prevMenu) {
        super(game, "Pause", prevMenu, "assets/tempPause.png");
        options = new AbstractOption[]{
            new OptionResume(game, this),
            new OptionTitle(game, this)
        };
        selectedIndex = 0;
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
