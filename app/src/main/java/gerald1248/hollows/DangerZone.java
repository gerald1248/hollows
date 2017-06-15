package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Need to draw a visible danger zone around the map
 * to avoid player frustration over unexplained explosions
 */

public class DangerZone {
    private int w = 40;
    private double angle = 0.0f;
    private double delta = Math.PI / 10.0;

    public DangerZone() {

    }

    public void draw(Canvas canvas, float cx, float cy, int color) {
        angle = (angle + delta) % (Math.PI * 2.0);
        int alpha = 20 + (int) Math.round(20.0 * Math.sin(angle));
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) w);

        Rect rNorth = new Rect(0, 0, (int) Constants.MAX_MAP, w);
        Rect rEast = new Rect((int) Constants.MAX_MAP - w, 0, (int) Constants.MAX_MAP, (int) Constants.MAX_MAP);
        Rect rSouth = new Rect(0, (int) Constants.MAX_MAP - w, (int) Constants.MAX_MAP, (int) Constants.MAX_MAP);
        Rect rWest = new Rect(0, 0, w, (int) Constants.MAX_MAP);
        canvas.save();
        //zzz
        //canvas.drawRect(r, paint);
        canvas.restore();
    }
}
