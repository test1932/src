package menu.title;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.title.character.MenuCharacter;

public class OptionStory extends AbstractOption {
    private MenuCharacter characterSelect;

    public OptionStory(Game game, AbstractMenu parent) {
        super(game, parent, "Story");
        characterSelect = new MenuCharacter(game, this.getParent(), game.getCont().players[0]);
    }

    //TODO
    @Override
    public void statefulHandler() {
        game.setCurMenu(characterSelect);
    }
}
