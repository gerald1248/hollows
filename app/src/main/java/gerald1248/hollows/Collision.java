package gerald1248.hollows;

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
}
