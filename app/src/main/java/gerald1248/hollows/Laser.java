package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import org.magnos.impulse.Body;
import org.magnos.impulse.Vec2;

/**
 * Laser bolts are similar to waves, but they interact with obstacles whereas waves don't
 * The panel maintains a linked list of these, just as it maintains a list of Wave objects
 */

public class Laser implements Projectile {
    public float r = 8.0f;
    public float d = 0.0f;
    public float x, y, prevX, prevY;

    // all angles in radians until passed to API
    public float orient;

    // fixed params
    private float cx;
    private float cy;
    private int stepsRemaining;
    private float dd = Constants.PLAYER_RADIUS;
    private Body observer = null;

    public Laser(float cx, float cy, float orient, int steps) {
        this.cx = cx;
        this.cy = cy;
        this.x = this.cx;
        this.y = this.cy;
        this.prevX = this.x;
        this.prevY = this.y;
        this.orient = orient;
        this.stepsRemaining = steps;
    }

    @Override
    public void draw(Canvas canvas, int color) {
        stepsRemaining--;
        if (stepsRemaining <= 0) {
            return;
        }

        canvas.save();
        float translateX = (observer == null) ? cx : observer.position.x;
        float translateY = (observer == null) ? cy : observer.position.y;

        canvas.translate(-translateX + Constants.SCREEN_WIDTH / 2, -translateY + Constants.SCREEN_HEIGHT / 2);

        //store previous values
        prevX = x;
        prevY = y;

        d += dd;
        Paint paint = new Paint();
        paint.setStrokeWidth(4.0f);
        paint.setColor(color);

        x = cx + d * (float) Math.cos((double) orient);
        y = cy + d * (float) Math.sin((double) orient);

        paint.setStyle(Paint.Style.FILL);

        //paint.setColor(Color.argb(64, 255, 255, 255));
        paint.setAlpha(64);
        canvas.drawCircle(x, y, r * 2, paint);

        //paint.setColor(Color.WHITE);
        paint.setAlpha(255);
        canvas.drawCircle(x, y, r, paint);
        canvas.restore();
    }

    @Override
    public boolean isDone() {
        return (stepsRemaining <= 0);
    }

    @Override
    public void setObserver(Body observer) {
        this.observer = observer;
    }

    public void setVelocityFactor(float f) {
        dd *= f;
    }
}
