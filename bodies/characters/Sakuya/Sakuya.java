package bodies.characters.Sakuya;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bodies.characters.AbstractCharacter;
import bodies.characters.AbstractPlayer;
import bodies.characters.Sakuya.spellActions.TestSpellActionFactory;

public class Sakuya extends AbstractCharacter {
    private static String[] imagePaths = new String[]{
        "assets/images/sprites/sakuya1.png"
    };
    private static BufferedImage[] sprites = new BufferedImage[imagePaths.length];
    private static boolean isSetup = false;

    public Sakuya(AbstractPlayer player) {
        super(player, "Sakuya");
        setupSpellActions();
        if (!Sakuya.isSetup) {
            try {
                Sakuya.setUpSprites();
            } catch (IOException e) {
                System.out.println("failed to get sprites");
            }
        }
    }

    private void setupSpellActions() {
        for (int i = 0; i < comboMapping.size(); i++) {
            comboMapping.get(i).snd = new TestSpellActionFactory(getPlayer());   
        }
    }

    public static void setUpSprites() throws IOException {
        for (int i = 0; i < imagePaths.length; i++) {
            sprites[i] = ImageIO.read(new File(imagePaths[i]));
        }
    }

    public static BufferedImage[] getSprites() {
        return sprites;
    }
}
