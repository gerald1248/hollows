package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Maintains state of one tiny dot in a starfield
 */

public class Star {
    public Star(boolean maj, float side) {
        cx = (float) Math.random() * side;
        cy = (float) Math.random() * side;
        r = 0.5f + (float) Math.random();

        if (maj) {
            r += 2.0f;
        }
    }

    public float getR() {
        return r;
    }

    private float cx;
    private float cy;
    private float r;

    void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle(cx, cy, r, paint);
    }
}
