package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Projectiles are used whenever waves emanating from objects are shown
 * The panel maintains a linked list of these
 */

public class Wave extends Object {
    public float cx;
    public float cy;

    // all angles in radians until passed to API
    public float orient; //rad
    public float sweep = 2.0F * (float)Math.PI; //rad

    private float r;

    // fixed params
    private int steps = 100;
    private float dr = Constants.PLAYER_RADIUS;

    public Wave(float cx, float cy, float orient, float sweep) {
        this.cx = cx;
        this.cy = cy;
        this.orient = orient - (float)Math.PI/2; //12 o'clock
        this.sweep = sweep;
    }
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(-cx + Constants.SCREEN_WIDTH/2, -cy + Constants.SCREEN_HEIGHT/2);

        steps--;
        if (steps <= 0) {
            return;
        }
        r += dr;
        Paint paint = new Paint();
        paint.setStrokeWidth(2.0F);
        paint.setColor(Color.WHITE);
        int alpha = (steps * 5) % 255;
        paint.setAlpha(alpha); // opaque then tail off quickly
        paint.setStyle(Paint.Style.STROKE);
        RectF rect = new RectF(cx - r, cy - r, cx + r, cy + r);
        canvas.drawArc(rect, (float)Math.toDegrees(orient - sweep/2), (float)Math.toDegrees(sweep), false, paint);
        canvas.restore();
    }
    public boolean done() {
        return (steps <= 0);
    }
}
