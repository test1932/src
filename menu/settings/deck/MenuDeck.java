package menu.settings.deck;

import bodies.characters.HumanPlayer;
import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;

public class MenuDeck extends AbstractMenu {
    public MenuDeck(Game game, AbstractMenu prevMenu, HumanPlayer player) {
        super(game, "Deck", prevMenu, "assets/images/backgrounds/config.jpg");
        options = new AbstractOption[9];
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
}
