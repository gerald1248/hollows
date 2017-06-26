package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.magnos.impulse.Body;

/**
 * Laser bolts are similar to waves, but they interact with obstacles whereas waves don't
 * The panel maintains a linked list of these, just as it maintains a list of Wave objects
 */

public class Laser implements Projectile {
    public float cx, cy;
    public float deltaX, deltaY;
    public float r = 8.0f;
    public float d = 0.0f;
    public float x, y, prevX, prevY, midX, midY;
    public int stepsRemaining;

    // all angles in radians until passed to API
    public float orient;

    private Body observer;
    private Paint paint;

    public Laser() {
        set(0.0f, 0.0f, 0.0f, 10, null);
    }

    public Laser(float cx, float cy, float orient, int steps, Body observer) {
        set(cx, cy, orient, steps, observer);
    }

    public void set(float cx, float cy, float orient, int steps, Body observer) {
        this.cx = cx;
        this.cy = cy;
        this.x = this.cx;
        this.y = this.cy;
        this.prevX = this.x;
        this.prevY = this.y;
        this.orient = orient;
        this.stepsRemaining = steps;
        this.observer = observer;

        deltaX = Constants.PLAYER_RADIUS * (float) Math.cos((double) orient);
        deltaY = Constants.PLAYER_RADIUS * (float) Math.sin((double) orient);

        if (observer != null) {
            //TODO: add observer.velocity
        }

        paint = new Paint();
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.FILL);
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

        paint.setColor(color);

        midX = x + deltaX/2;
        midY = y + deltaY/2;
        x += deltaX;
        y += deltaY;

        canvas.drawCircle(x, y, r, paint);
        canvas.restore();
    }

    @Override
    public boolean isDone() {
        return (stepsRemaining <= 0);
    }

    public void setVelocityFactor(float f) {
        deltaX *= f;
        deltaY *= f;
    }
}
