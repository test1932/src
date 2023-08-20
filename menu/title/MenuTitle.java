package menu.title;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.Game;
import menu.AbstractMenu;
import menu.AbstractOption;
import menu.settings.OptionExit;

public class MenuTitle extends AbstractMenu {

    public MenuTitle(Game game, AbstractMenu prevMenu) {
        super(game, "Title", prevMenu);
        options = new AbstractOption[]{
            new OptionStory(game, this),
            new OptionArcade(game, this),
            new OptionNetworkPlay(game, this),
            new OptionQuickGame(game, this),
            new OptionSettings(game, this),
            new OptionExit(game, this)
        };

        try {
            setBackground(ImageIO.read(new File("assets/tempBackground.jpg")));
        } catch (IOException e) {
            System.err.println("failed to get image");
        }
    }

    @Override
    public void animateSelectionChange() {
        // TODO Animation
    }
    
}
