package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Need to draw the danger zone around the map (preferably in red)
 * to avoid player frustration over unexplained explosion
 */

public class DangerZone {
    public static void draw(Canvas canvas, float cx, float cy, int color) {
        int w = 50;

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(25);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) w);

        Rect r = new Rect(0, 0, (int) Constants.MAX_MAP, (int) Constants.MAX_MAP);

        canvas.save();
        canvas.translate(cx + Constants.SCREEN_WIDTH / 2, cy + Constants.SCREEN_HEIGHT / 2);
        canvas.drawRect(r, paint);
        canvas.restore();
    }
}
