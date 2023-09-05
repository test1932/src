package menu.settings;

import bodies.characters.HumanPlayer;
import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.settings.deck.MenuDeck;


public class OptionDeck extends AbstractOption {
    private MenuDeck deck;

    public OptionDeck(Game game, AbstractMenu parent, HumanPlayer player, int playerNo) {
        super(game, parent, "Player " + String.valueOf(playerNo) + "'s Deck");
        deck = new MenuDeck(game, parent, player);
    }

    @Override
    public void statefulHandler() {
        game.setCurMenu(deck);
    }
}
