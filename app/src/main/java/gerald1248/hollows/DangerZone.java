package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Need to draw a visible danger zone around the map
 * to avoid player frustration over unexplained explosions
 */

public class DangerZone {
    private int w = 10;
    private double angle = 0.0f;
    private double delta = Math.PI / 10.0;
    private Rect r;//, rNorth, rEast, rSouth, rWest, rIntersect;
    private int screenW2 = Constants.SCREEN_WIDTH/2;
    private int screenH2 = Constants.SCREEN_HEIGHT/2;
    private Paint paint;

    public DangerZone() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) w);
        r = new Rect(0, 0, (int) Constants.MAX_MAP, (int) Constants.MAX_MAP);

        /*
        rIntersect = new Rect(0, 0, 0, 0);
        rNorth = new Rect(0, 0, (int) Constants.MAX_MAP, w);
        rEast = new Rect((int) Constants.MAX_MAP - w, 0, (int) Constants.MAX_MAP, (int) Constants.MAX_MAP);
        rSouth = new Rect(0, (int) Constants.MAX_MAP - w, (int) Constants.MAX_MAP, (int) Constants.MAX_MAP);
        rWest = new Rect(0, 0, w, (int) Constants.MAX_MAP);
        */
    }

    public void draw(Canvas canvas, float cx, float cy, int color) {
        angle = (angle + delta) % (Math.PI * 2.0);
        int val = 20 + (int) Math.round(20.0 * Math.sin(angle));
        boolean isRed = color == Color.RED;
        paint.setColor(Color.rgb(val, (isRed) ? 0 : val, (isRed) ? 0 : val));


        canvas.save();
        canvas.translate(-cx + screenW2, -cy + screenH2);
        canvas.drawRect(r, paint);
        canvas.restore();

        /*
        int x = (int) cx;
        int y = (int) cy;

        //screen
        r.set(x - screenW2, y - screenH2, x + screenW2, y + screenH2);
        if (rIntersect.setIntersect(r, rNorth)) {
            rIntersect.offset(-x + screenW2, -y + screenH2);
            canvas.drawRect(rIntersect, paint);
        }
        if (rIntersect.setIntersect(r, rEast)) {
            rIntersect.offset(-x + screenW2, -y + screenH2);
            canvas.drawRect(rIntersect, paint);
        }
        if (rIntersect.setIntersect(r, rSouth)) {
            rIntersect.offset(-x + screenW2, -y + screenH2);
            canvas.drawRect(rIntersect, paint);
        }
        if (rIntersect.setIntersect(r, rWest)) {
            rIntersect.offset(-x + screenW2, -y + screenH2);
            canvas.drawRect(rIntersect, paint);
        }
        */
    }
}
