package menu.title.networkPlay;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.OptionBack;

public class MenuNetwork extends AbstractMenu {

    public MenuNetwork(Game game, AbstractMenu prevMenu) {
        super(game, "Network", prevMenu, "assets/images/sprites/temp.png");
        options = new AbstractOption[]{
            new menu.OptionBack(game, this)
        };
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
