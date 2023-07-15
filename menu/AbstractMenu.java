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
        if (selectedIndex == null || options.length == 0) throw new NullPointerException();
        selectedIndex = (selectedIndex + n + options.length) % options.length;
        System.out.println(selectedIndex);
    }

    public void decrementSelection(int n) {
        incrementSelection(-n);
    }
}
