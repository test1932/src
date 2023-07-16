package menu.settings.config;

import bodies.characters.HumanPlayer;
import bodies.characters.HumanPlayer.Keys;
import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;

public class KeyOption extends AbstractOption {
    private Keys inputAction;
    private int curCode;
    private HumanPlayer player;

    public KeyOption(Game game, AbstractMenu parent, String optionName, 
                     Keys inputAction, HumanPlayer player) {
        super(game, parent, optionName);
        this.inputAction = inputAction;
        this.player = player;
    }

    public void setKeyID(int keyCode) {
        if (((MenuConfig)getParent()).keyCodeUsed(keyCode)) return;
        curCode = keyCode;
        player.updateKey(inputAction, keyCode);
        game.notifyObservers();
    }

    public int getKeyID() {
        return this.curCode;
    } 

    public void statefulHandler() {
        // EMPTY
    }

    public int getCurCode() {
        return curCode;
    }
}
