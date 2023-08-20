package game.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.LinkedList;

public class SegmentedProgressBar extends ProgressBar{
    protected int segments;
    protected int fullSegments;

    protected Color fullColor = Color.GREEN;
    protected LinkedList<Rectangle> progressFill = new LinkedList<Rectangle>();
    protected LinkedList<Rectangle> outlines = new LinkedList<Rectangle>();;

    private final int SEPARATOR = 10;

    public SegmentedProgressBar(boolean isReversed, int x, int y, int width, int height, double initial, int segments) {
        super(isReversed, x, y, width, height, initial);
        if (segments <= 0) return;
        this.segments = segments;
        setupSegments(x, y, width, height, segments);
        fillProgress();
    }

    public SegmentedProgressBar(boolean isReversed, int x, int y, int width, int height, int segments) {
        super(isReversed, x, y, width, height);
        if (segments <= 0) return;
        this.segments = segments;
        setupSegments(x, y, width, height, segments);
    }

    private void setupSegments(int x, int y, int width, int height, int segments) {
        int segmentWidth = (width / segments) - SEPARATOR;
        for (int i = 0; i < segments; i++) {
            int segmentX = x + i * ((width / segments));
            outlines.add(new Rectangle(segmentX, y, segmentWidth, height));
            progressFill.add(new Rectangle(segmentX, y, 0, height));
        }
    }

    private void fillProgress() {
        double progressTemp = progress;
        int width = outlines.getFirst().width;
        int n = 0;
        for (Rectangle fill : progressFill) {
            if (progressTemp >= 1.0 / segments) n++;
            if (progressTemp > 1.0 / segments) {
                fill.width = width;
            }
            else {
                fill.width = (int)(segments * progressTemp * width);
            }
            progressTemp -= 1.0 / segments;
            if (progressTemp < 0) progressTemp = 0;
        }
        fullSegments = n;
    }

    @Override
    public void paintProgress(Graphics2D g2D) {
        fillProgress();
        Color tempColor = g2D.getColor();
        g2D.setColor(fillColour);
        for (Rectangle segment : progressFill) {
            g2D.fill(segment);
        }
        paintOutlines(g2D);
        g2D.setColor(tempColor); // restore colour
    }

    private void paintOutlines(Graphics2D g2D) {
        int n = 0;
        g2D.setColor(fullColor);
        for (Rectangle outline : outlines) {
            if (n++ == fullSegments) g2D.setColor(outlineColour);
            g2D.draw(outline);
        }
    }
}
