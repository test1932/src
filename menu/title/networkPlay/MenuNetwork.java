package menu.title.networkPlay;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.OptionBack;

public class MenuNetwork extends AbstractMenu {

    public MenuNetwork(Game game, AbstractMenu prevMenu) {
        super(game, "Network", prevMenu, "assets/images/sprites/temp.png");
        options = new AbstractOption[]{
            new TumblerOption(game, this, "Host IP", 12),
            new TumblerOption(game, this, "Host Port", 5),
            new menu.OptionBack(game, this)
        };
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
