package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import org.magnos.impulse.Vec2;

/**
 * Laser bolts are similar to waves, but they interact with obstacles whereas waves don't
 * The panel maintains a linked list of these, just as it maintains a list of Wave objects
 */

public class Laser implements Projectile {
    public float r = 8.0f;
    public float x, y;
    private float xOffset = 0.0f;
    private float yOffset = 0.0f;

    // all angles in radians until passed to API
    public float orient; //rad

    // fixed params
    private float cx;
    private float cy;
    private int steps, stepsRemaining;
    private float d = 0.0f;
    private float dd = Constants.PLAYER_RADIUS;

    //private Vec2 translationVec2 = null;

    public Laser(float cx, float cy, float orient, int steps) {
        this.cx = cx;
        this.cy = cy;
        this.x = this.cx;
        this.y = this.cy;
        this.orient = orient;
        this.steps = steps;
        this.stepsRemaining = steps;
    }

    @Override
    public void draw(Canvas canvas) {
        stepsRemaining--;
        if (stepsRemaining <= 0) {
            return;
        }

        canvas.save();
        canvas.translate(-cx + -xOffset + Constants.SCREEN_WIDTH/2, -cy - yOffset + Constants.SCREEN_HEIGHT/2);

        d += dd;
        Paint paint = new Paint();
        paint.setStrokeWidth(4.0f);
        paint.setColor(Color.WHITE);

        x = cx + d * (float)Math.cos((double)orient);
        y = cy + d * (float)Math.sin((double)orient);

        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.argb(64, 255, 255, 255));
        canvas.drawCircle(x, y, r * 2, paint);

        paint.setColor(Color.WHITE);
        canvas.drawCircle(x, y, r, paint);
        canvas.restore();
    }

    @Override
    public boolean isDone() {
        return (stepsRemaining <= 0);
    }

    @Override
    public void setOffset(float dx, float dy) {
        xOffset = dx;
        yOffset = dy;
    }

    public void divideVelocityBy(float divisor) {
        dd /= divisor;
    }
}
