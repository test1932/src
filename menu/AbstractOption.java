package menu;

import game.Game;

public abstract class AbstractOption {
    public String displayText;
    protected Game game;
    private AbstractMenu parent;

    public AbstractOption(Game game, AbstractMenu parent, String optionName) {
        this.game = game;
        this.parent = parent;
        this.displayText = optionName;
    }

    public abstract void statefulHandler(); 

    public AbstractMenu getParent() {
        return parent;
    }

    public void setParent(AbstractMenu parent) {
        this.parent = parent;
    }
}
