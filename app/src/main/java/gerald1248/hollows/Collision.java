package gerald1248.hollows;

import android.graphics.Rect;

/**
 * The collision class contains static methods for general purpose collision detection
 */

public class Collision {

    static boolean circleCircle(float x1, float y1, float x2, float y2, float r1, float r2) {
        return circleCircle(x1, y1, x2, y2, r1 + r2);
    }

    //arbitrary distance - test proximity
    static boolean circleCircle(float x1, float y1, float x2, float y2, float d) {
        return (Math.abs((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) < (d * d));
    }

    //used for layout, not actual collisions
    //these objects overlap with the viewport
    static boolean circleRect(int cx, int cy, int r, Rect rect) {
        if (rect.contains(cx, cy) || rect.intersect(cx - r, cy - r, cx + r, cy + r)) {
            return true;
        }
        return false;
    }

    //likewise used for layout: esp. the danger zone bordering the map
    static boolean rectRect(Rect r1, Rect r2) {
        return r1.intersect(r2);
    }
}
