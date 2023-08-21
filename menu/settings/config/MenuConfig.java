package menu.settings.config;

import bodies.characters.HumanPlayer;
import bodies.characters.HumanPlayer.Keys;
import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.OptionBack;

public class MenuConfig extends AbstractMenu {
    public MenuConfig(Game game, AbstractMenu prevMenu, HumanPlayer player) {
        super(game, "Config", prevMenu, "assets/images/backgrounds/config.jpg");
        options = new AbstractOption[9];
        Keys[] keyArr =  Keys.values();

        for (int i = 0; i < 8; i++) {
            options[i] = new KeyOption(game, this, keyArr[i].toString(), keyArr[i], player);
        }
        options[8] = new OptionBack(game, this);
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
    public boolean keyCodeUsed(int keycode) {
        for (AbstractOption option : options) {
            if (!(option instanceof KeyOption)) continue;
            if (((KeyOption)option).getCurCode() == keycode) return true;
        }
        return false;
    }
}
