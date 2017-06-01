package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Need to draw a visible danger zone around the map
 * to avoid player frustration over unexplained explosions
 */

public class DangerZone {
    private int color = Color.WHITE;
    private int w = 40;
    private double angle = 0.0f;
    private double delta = Math.PI/10.0;
    public DangerZone(int color) {
        this.color = color;
    }
    public void draw(Canvas canvas, float cx, float cy) {
        angle = (angle + delta) % (Math.PI * 2.0);
        int alpha = 20 + (int) Math.round(20.0 * Math.sin(angle));
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) w);

        Rect r = new Rect(0, 0, (int) Constants.MAX_MAP, (int) Constants.MAX_MAP);

        canvas.save();
        canvas.translate(cx + Constants.SCREEN_WIDTH / 2, cy + Constants.SCREEN_HEIGHT / 2);
        canvas.drawRect(r, paint);
        canvas.restore();
    }
}
