package bodies.characters.Utsuho;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bodies.characters.AbstractCharacter;
import bodies.characters.AbstractPlayer;
import bodies.characters.Sakuya.spellActions.TestSpellActionFactory;

public class Utsuho extends AbstractCharacter {
    private static String[] imagePaths = new String[]{
        "assets/images/sprites/utsuho.png",
        "assets/images/sprites/cut ins.png"
    };
    private static BufferedImage[] sprites = new BufferedImage[4];
    private static boolean isSetup = false;
    private static BufferedImage spriteSheet;
    private static BufferedImage cutIn;

    public Utsuho(AbstractPlayer player) {
        super(player, "Sakuya");
        setupSpellActions();
        setupCharacter();
    }

    private void setupSpellActions() {
        for (int i = 0; i < comboMapping.size(); i++) {
            comboMapping.get(i).snd = new TestSpellActionFactory(getPlayer());   
        }
    }

    public static void setupCharacter() {
        if (!Utsuho.isSetup) {
            try {
                Utsuho.setUpSprites();
                Utsuho.isSetup = true;
            } catch (IOException e) {
                System.out.println("failed to get sprites");
            }
        }
    }

    public static void setUpSprites() throws IOException {
        spriteSheet = ImageIO.read(new File(imagePaths[0]));
        int width = spriteSheet.getWidth() / 2;
        int height = spriteSheet.getHeight() / 2;

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                sprites[x + y * 2] = spriteSheet.getSubimage(x * width, y * height, width, height);
            }
        }
        cutIn = ImageIO.read(new File(imagePaths[1]));
    }

    public static BufferedImage[] getSprites() {
        return sprites;
    }

    public static BufferedImage getCutIn() {
        return cutIn;
    }
}
