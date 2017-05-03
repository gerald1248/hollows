package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Starfield manages an array of stars
 */

public class Starfield {
    private Star[] stars = new Star[64];

    public Starfield() {
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(i < 2); // make first two major stars
        }
    }

    public Star[] getStars() {
        return stars;
    }

    void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < stars.length; i++) {
            stars[i].draw(canvas, paint);
        }
    }
}
