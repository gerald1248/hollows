package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import org.magnos.impulse.Body;

/**
 * Projectiles are used whenever waves emanating from objects are shown
 * The panel maintains a linked list of these
 */

public class Wave implements Projectile {
    public float cx;
    public float cy;
    public float r = 0.0f;

    // all angles in radians until passed to API
    public float orient; //rad
    public float sweep = 2.0f * (float) Math.PI; //rad

    // fixed params
    private int steps, stepsRemaining;
    private float dr = Constants.PLAYER_RADIUS;
    private Body observer = null;
    private Paint paint;

    public Wave(float cx, float cy, float orient, float sweep, int steps) {
        this.cx = cx;
        this.cy = cy;
        this.orient = orient;
        this.sweep = sweep;
        this.steps = steps;
        this.stepsRemaining = steps;
        paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void setObserver(Body observer) {
        this.observer = observer;
    }

    @Override
    public void setVelocityFactor(float f) {
        dr *= f;
    }

    @Override
    public void draw(Canvas canvas, int color, boolean show) {
        stepsRemaining--;
        if (stepsRemaining <= 0) {
            return;
        }

        r += dr;

        if (show == false) {
            return;
        }

        float translateX = (observer == null) ? cx : observer.position.x;
        float translateY = (observer == null) ? cy : observer.position.y;
        canvas.save();
        canvas.translate(-translateX + Constants.SCREEN_WIDTH / 2, -translateY + Constants.SCREEN_HEIGHT / 2);
        paint.setColor(color);
        RectF rect = new RectF(cx - r, cy - r, cx + r, cy + r);
        canvas.drawArc(rect, (float) Math.toDegrees(orient - sweep / 2), (float) Math.toDegrees(sweep), false, paint);
        canvas.restore();
    }

    @Override
    public boolean isDone() {
        return (stepsRemaining <= 0);
    }
}
