package game.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ProgressBar extends Rectangle {
    private double progress;
    private Rectangle progressFill;
    private boolean isReversed;
    private Color fillColour;
    private Color outlineColour;

    public ProgressBar(boolean isReversed, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.isReversed = isReversed;
        progressFill = new Rectangle(isReversed ? x + width : x, y, 0, height);
    }

    public ProgressBar(boolean isReversed, int x, int y, int width, int height, double initial) {
        super(x, y, width, height);
        this.isReversed = isReversed;
        this.progress = initial;
        int offset = (int)(initial * width);
        progressFill = new Rectangle(isReversed ? x + width - offset : x, y, offset, height);
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        if (progress > 1 || progress < 0) return;
        this.progress = progress;
        progressFill.width = (int)(progress * width);
        if (isReversed) {
            progressFill.x = x + width - progressFill.width;
        }
    }

    public void incrementProgress(double progressIncrement) {
        if (progress + progressIncrement > 1 || progress + progressIncrement < 0) return;
        setProgress(progress + progressIncrement);
    }

    public void decrementProgress(double progressDecrement) {
        incrementProgress(-progressDecrement);
    }

    public void paintProgress(Graphics2D g2D) {
        Color tempColor = g2D.getColor();
        g2D.setColor(fillColour);
        g2D.fill(progressFill);
        g2D.setColor(outlineColour);
        g2D.draw(this);
        g2D.setColor(tempColor); // restore colour
    }

    public Color getFillColour() {
        return fillColour;
    }

    public Color getOutlineColour() {
        return outlineColour;
    }

    public void setOutlineColour(Color outlineColour) {
        this.outlineColour = outlineColour;
    }

    public void setFillColour(Color fillColour) {
        this.fillColour = fillColour;
    }
}
