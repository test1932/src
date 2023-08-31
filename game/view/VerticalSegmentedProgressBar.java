package game.view;

import java.awt.Rectangle;

public class VerticalSegmentedProgressBar extends SegmentedProgressBar {
    public VerticalSegmentedProgressBar(boolean isReversed, int x, int y, int width, int height, double initial,
            int segments) {
        super(isReversed, x, y, width, height, initial, segments);
        //TODO Auto-generated constructor stub
    }

    public VerticalSegmentedProgressBar(boolean isReversed, int x, int y, int width, int height, int segments) {
        super(isReversed, x, y, width, height, segments);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected void setupSegments(int x, int y, int width, int height, int segments) {
        int segmentWidth = (width / segments) - SEPARATOR;
        for (int i = 0; i < segments; i++) {
            int segmentX = x + i * ((width / segments));
            outlines.add(new Rectangle(segmentX, y, segmentWidth, height));
            progressFill.add(new Rectangle(segmentX, y, segmentWidth, 0));
        }
    }

    @Override
    protected void fillProgress() {
        double progressTemp = progress;
        int f_height = outlines.getFirst().height;
        int n = 0;
        for (Rectangle fill : progressFill) {
            if (progressTemp >= 1.0 / segments) n++;
            if (progressTemp > 1.0 / segments) {
                fill.height = f_height;
            }
            else {
                fill.height = (int)(segments * progressTemp * f_height);
            }
            progressTemp -= 1.0 / segments;
            if (progressTemp < 0) progressTemp = 0;
        }
        fullSegments = n;
    }
}
