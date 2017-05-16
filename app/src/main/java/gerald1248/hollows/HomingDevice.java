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
    float cx, cy, r;
    float orient = 0.0f; // 3 o'clock

    public HomingDevice(float cx, float cy, float r) {
        this.cx = cx;
        this.cy = cy;
        this.r = r;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate((float)Math.toDegrees(orient), cx, cy);
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        canvas.drawCircle(cx, cy, r, paint);

        paint.setColor(Color.WHITE);
        Path p = new Path();
        p.moveTo(cx + r, cy);
        p.lineTo(cx - r, cy + r/3);
        p.lineTo(cx - r + r/3, cy);
        p.lineTo(cx - r, cy - r/3);
        p.close();
        canvas.drawPath(p, paint);

        canvas.restore();
    }

    @Override
    public void update() {

    }

    public void update(float orient) {
        this.orient = orient;
    }
}
