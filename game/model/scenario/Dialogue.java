package game.model.scenario;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import game.view.Display;

public class Dialogue {
    private BufferedImage spriteLeft;
    private BufferedImage spriteRight;
    private String textLine1;
    private String textLine2;
    //TODO audio
    private boolean isLeft;
    private Dialogue next;

    public static final int LINE_1_TEXT_Y = Display.HEIGHT - 100;
    public static final int LINE_2_TEXT_Y = Display.HEIGHT - 70;
    public static final int TEXT_X = 50;
    public static final int LEFT_X = 0;
    public static final int WIDTH = 400;
    public static final int HEIGHT = 400;

    public Dialogue(BufferedImage spriteLeft, BufferedImage spriteRight, String text, boolean isLeft) {
        this.spriteRight = spriteRight;
        this.spriteLeft = spriteLeft;
        if (text.length() > 60) {
            this.textLine1 = text.substring(0,60);
            this.textLine2 = text.substring(60);
        }
        else {
            this.textLine1 = text;
            this.textLine2 = "";
        }
        this.isLeft = isLeft;
        mirrorRight();
        applyTransparency();
    }

    private void mirrorRight() {
        AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
        transform.translate(-spriteRight.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        spriteRight = op.filter(spriteRight, null);
    }

    private void applyTransparency() {
        BufferedImage img;
        if (isLeft) {
            img = spriteRight;
        }
        else {
            img = spriteLeft;
        }

        BufferedImage temp = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = temp.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        if (isLeft) {
            spriteRight = temp;
        }
        else {
            spriteLeft = temp;
        }
    }

    public Dialogue getNext() {
        return next;
    }

    public void setNext(Dialogue next) {
        this.next = next;
    }

    public BufferedImage getSpriteLeft() {
        return spriteLeft;
    }

    public BufferedImage getSpriteRight() {
        return spriteRight;
    }

    public String getTextLine1() {
        return textLine1;
    }

    public String getTextLine2() {
        return textLine2;
    }

    public boolean isLeft() {
        return isLeft;
    }
}
