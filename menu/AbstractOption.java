package menu;

import game.Game;

public abstract class AbstractOption {
    public String displayText;
    protected Game game;

    public AbstractOption(Game game) {
        this.game = game;
    }

    public abstract void statefulHandler(); 
}
