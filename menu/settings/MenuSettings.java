package menu.settings;

import java.util.LinkedList;

import bodies.characters.AbstractPlayer;
import bodies.characters.HumanPlayer;
import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.OptionBack;
import game.model.scenario.Battle;

public class MenuSettings extends AbstractMenu{
    public MenuSettings(Game game, AbstractMenu prevMenu) {
        super(game, "Settings", prevMenu, "assets/settings.png");
        LinkedList<HumanPlayer> humans = getHumans();
        options = new AbstractOption[1 + humans.size()];

        int i = 0;
        for (HumanPlayer human: humans) {
            options[i] = new OptionConfig(game, this, human, i + 1);
            i++;
        }
        options[options.length - 1] = new OptionBack(game, this);
    }

    @Override
    public void animateSelectionChange() {
        // TODO Anumation
    }
    
    private LinkedList<HumanPlayer> getHumans() {
        LinkedList<HumanPlayer> humans = new LinkedList<HumanPlayer>();
        for (AbstractPlayer player : game.b.players) {
            if (player instanceof HumanPlayer) humans.add((HumanPlayer)player);
        }
        return humans;
    }
}
