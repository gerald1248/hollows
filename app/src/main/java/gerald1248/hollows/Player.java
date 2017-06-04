package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * Player
 */

public class Player implements GameObject {

    public float orient;

    private Rect r;
    private float scale;
    private boolean explode, escape;

    public Player(Rect rectangle, float orient) {
        this.r = rectangle;
        this.orient = orient;
        this.orient = -0.5f * (float)Math.PI;
        this.scale = 1.0f;
        this.explode = false;
    }

    public void move(MotionEvent event, float delta) {

        if (delta < 0) {
            this.orient -= Math.PI/40; // was 50
        } else if (delta > 0) {
            this.orient += Math.PI/40; // was 50
        }
    }

    @Override
    public void draw(Canvas canvas, int color) {
        Paint paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setColor(color);

        float paddingX = ((float) r.width()) / 10.0f;
        float paddingY = ((float) r.height()) / 10.0f;
        float[] points = {
                (float) r.right, r.exactCenterY(),
                (float) r.left, (float) r.bottom - paddingY,
                (float) r.left + paddingX, r.exactCenterY(),
                (float) r.left, (float) r.top + paddingY,
                (float) r.right, r.exactCenterY()
        };

        Path path = new Path();
        path.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i += 2) {
            path.lineTo(points[i], points[i + 1]);
        }

        canvas.save();
        canvas.rotate(this.orient * 180 / (float) Math.PI, r.centerX(), r.centerY()); // rad2deg

        if (explode) {
            this.scale *= 1.5f;
            canvas.scale(this.scale, this.scale, r.centerX(), r.centerY());
        } else if (escape) {
            this.scale /= 1.5f;
            canvas.scale(this.scale, this.scale, r.centerX(), r.centerY());
        }
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    @Override
    public void update() {
    }

    public void update(Point point) {
        r.set(point.x - r.width() / 2, point.y - r.height() / 2, point.x + r.width() / 2, point.y + r.height() / 2);
    }

    public void explode(boolean b) {
        explode = b;

        if (!b) {
            scale = 1.0f;
        }
    }

    public void escape(boolean b) {
        escape = b;

        if (!escape) {
            scale = 1.0f;
        }
    }

    public boolean exploded() {
        return (scale > 20.0f);
    }

    public boolean escaped() { return (scale < 0.05f); }
}
