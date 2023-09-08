package menu.title;

import game.Game;
import game.Game.GameState;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.title.networkPlay.MenuNetwork;

public class OptionNetworkPlay extends AbstractOption {
    private AbstractMenu networkMenu;

    public OptionNetworkPlay(Game game, AbstractMenu parent) {
        super(game, parent, "Network");
        networkMenu = new MenuNetwork(game, this.getParent());
    }

    @Override
    public void statefulHandler() {
        game.setCurMenu(networkMenu);
    }
}
