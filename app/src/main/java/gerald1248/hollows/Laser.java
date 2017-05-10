package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Laser bolts are similar to waves, but they interact with obstacles whereas waves don't
 * The panel maintains a linked list of these, just as it maintains a list of Wave objects
 */

public class Laser implements Projectile {
    public float r = 8.0f;
    public float x, y;

    // all angles in radians until passed to API
    public float orient; //rad

    // fixed params
    private float cx;
    private float cy;
    private int steps, stepsRemaining;
    private float d = 0.0f;
    private float dd = Constants.PLAYER_RADIUS;

    public Laser(float cx, float cy, float orient, int steps) {
        this.cx = cx;
        this.cy = cy;
        this.x = this.cx;
        this.y = this.cy;
        this.orient = orient - (float)Math.PI/2; //12 o'clock
        this.steps = steps;
        this.stepsRemaining = steps;
    }
    public void draw(Canvas canvas) {
        stepsRemaining--;
        if (stepsRemaining <= 0) {
            return;
        }

        canvas.save();
        canvas.translate(-cx + Constants.SCREEN_WIDTH/2, -cy + Constants.SCREEN_HEIGHT/2);

        d += dd;
        Paint paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setColor(Color.WHITE);

        x = cx + d * (float)Math.cos((double)orient);
        y = cy + d * (float)Math.sin((double)orient);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(x, y, r, paint);
        canvas.restore();
    }
    public boolean done() {
        return (stepsRemaining <= 0);
    }
}
