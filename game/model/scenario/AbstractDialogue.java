package game.model.scenario;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.view.Display;

public abstract class AbstractDialogue {
    private BufferedImage sprite;
    private String textLine1;
    private String textLine2;
    //TODO audio
    private boolean isLeft;
    private Display d;
    private AbstractDialogue next;

    public static final int LINE_1_TEXT_Y = 400;
    public static final int LINE_2_TEXT_Y = 430;
    public static final int TEXT_X = 50;
    public static final int LEFT_X = 0;
    public static final int WIDTH = 300;
    public static final int HEIGHT = 400;

    public AbstractDialogue(BufferedImage sprite, String text, boolean isLeft, Display d) {
        this.sprite = sprite;
        if (text.length() > 60) {
            this.textLine1 = text.substring(0,60);
            this.textLine2 = text.substring(60);
        }
        else {
            this.textLine1 = text;
            this.textLine2 = "";
        }
        this.isLeft = isLeft;
        this.d = d;
    }

    public void paintDialogue(Graphics2D g2D) {
        Color tempColor = g2D.getColor();
        g2D.setColor(Color.BLACK);
        g2D.drawString(textLine1, TEXT_X, LINE_1_TEXT_Y);
        g2D.drawString(textLine1, TEXT_X, LINE_2_TEXT_Y);
        g2D.drawImage(sprite, isLeft ? LEFT_X : Display.WIDTH - WIDTH, 50, WIDTH, HEIGHT, d);
        g2D.setColor(tempColor);
    }

    public AbstractDialogue getNext() {
        return next;
    }

    public void setNext(AbstractDialogue next) {
        this.next = next;
    }
}
