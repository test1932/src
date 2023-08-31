package actions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bodies.characters.AbstractPlayer;

public abstract class AbstractSpellcard {
    protected AbstractSpellAction spellAction;
    protected int cost = 1;
    private AbstractPlayer owner;
    private BufferedImage cardImage;
    public long timeout = 500l;

    protected String path;

    public AbstractSpellcard(AbstractPlayer player) {
        this.owner = player;
    }

    public AbstractSpellAction getSpellAction() {
        return spellAction;
    }

    public void setSpellAction(AbstractSpellAction spellAction) {
        this.spellAction = spellAction;
    }

    public void setImage() {
        try {
            this.cardImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("failed to get image");
        }
    }

    public void activateSpellCard() {
        AbstractSpellcard[] hand = owner.getHand();
        for (int i = 0; i < cost; i++) {
            hand[i] = null;
        }
        spellAction.start();
        animateSpellCard();
    }

    public AbstractPlayer getOwner() {
        return owner;
    }

    private void animateSpellCard() {
        //TODO
    }

    public BufferedImage getCardImage() {
        return cardImage;
    }
}
