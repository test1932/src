package game.model.scenario;

import java.awt.image.BufferedImage;

public class Dialogue {
    private BufferedImage sprite;
    private String textLine1;
    private String textLine2;
    //TODO audio
    private boolean isLeft;
    private Dialogue next;

    public static final int LINE_1_TEXT_Y = 400;
    public static final int LINE_2_TEXT_Y = 430;
    public static final int TEXT_X = 50;
    public static final int LEFT_X = 0;
    public static final int WIDTH = 300;
    public static final int HEIGHT = 400;

    public Dialogue(BufferedImage sprite, String text, boolean isLeft) {
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
    }

    public Dialogue getNext() {
        return next;
    }

    public void setNext(Dialogue next) {
        this.next = next;
    }

    public BufferedImage getSprite() {
        return sprite;
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
