package menu.title.networkPlay;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;

public class TumblerOption extends AbstractOption {
    private NumberTumbler tumbler;
    private boolean isSelected = false;

    public TumblerOption(Game game, AbstractMenu parent, String optionName, int noDigits) {
        super(game, parent, optionName);
        this.tumbler = new NumberTumbler(noDigits);
    }

    public NumberTumbler getTumbler() {
        return this.tumbler;
    } 

    @Override
    public void statefulHandler() {
        // EMPTY
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void toggleSelected() {
        this.isSelected = !this.isSelected;
    }

    public void nextDigit() {
        tumbler.nextDigit();
    }

    public void prevDigit() {
        tumbler.prevDigit();
    }

    public void incrementDigit() {
        tumbler.incrementDigit();
    }

    public void decrementDigit() {
        tumbler.decrementDigit();
    }
}
