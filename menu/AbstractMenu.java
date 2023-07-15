package menu;

import game.Game;

public abstract class AbstractMenu {
    protected Integer selectedIndex = null;
    protected AbstractOption[] options = {};
    protected Game game;

    public AbstractMenu(Game game) {
        this.game = game;
    }

    public abstract void animateSelectionChange();

    public void incrementSelection(int n) {
        if (selectedIndex == null) throw new NullPointerException();
        selectedIndex = (selectedIndex + n) % options.length;
    }

    public void decrementSelection(int n) {
        incrementSelection(-n);
    }
}
