package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * To keep instructions to a minimum, the homing device guides the player at all times
 * First to find the settlers' orb, then to indicate the flight path to the top
 */

public class HomingDevice implements GameObject {
    private float cx, cy, r;
    private float orient = 0.0f; // 3 o'clock
    private Paint paint;
    private Path path;

    public HomingDevice(float cx, float cy, float r) {
        this.cx = cx;
        this.cy = cy;
        this.r = r;

        paint = new Paint();
        path = new Path();
    }

    @Override
    public void draw(Canvas canvas, int color) {
        canvas.save();
        canvas.rotate((float) Math.toDegrees(orient), cx, cy);

        paint.reset();

        paint.setColor(Color.GRAY);
        canvas.drawCircle(cx, cy, r, paint);

        paint.setColor(color);

        path.reset();
        path.moveTo(cx + r, cy);
        path.lineTo(cx - r, cy + r / 3);
        path.lineTo(cx - r + r / 3, cy);
        path.lineTo(cx - r, cy - r / 3);
        path.close();
        canvas.drawPath(path, paint);

        canvas.restore();
    }

    @Override
    public void update() {

    }

    public void update(float orient) {
        this.orient = orient;
    }
}
