package gerald1248.hollows;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import static android.graphics.Bitmap.Config.ALPHA_8;
import static android.graphics.Bitmap.createBitmap;

/**
 * Starfield manages an array of stars
 */

public class Starfield {
    private Bitmap bitmap = null;
    private Canvas canvas = null;

    public Star[] getStars() {
        return stars;
    }

    private Star[] stars = new Star[128];
    private float side = Constants.MAX_MAP/2; //parallax effect: double foreground speed

    public Starfield() {
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(i < 2, side); // make first two major stars
        }
        bitmap = createBitmap((int) side, (int) side, ALPHA_8);
        canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < stars.length; i++) {
            stars[i].draw(canvas, paint);
        }
    }

    public void draw(Canvas canvas, float cx, float cy, int color) {
        //parallax effect: the bitmap is half the size of the game map
        //need to halve scrolling speed for starfield
        cx /= 2;
        cy /= 2;

        Paint paint = new Paint();
        paint.setStrokeWidth((float) 2.0);
        paint.setColor(color);

        canvas.save();
        canvas.translate(cx + Constants.SCREEN_WIDTH / 2, cy + Constants.SCREEN_HEIGHT / 2);

        // copy from offscreen canvas
        Rect r = new Rect(0, 0, (int) Constants.MAX_MAP/2, (int) Constants.MAX_MAP/2);
        canvas.drawBitmap(bitmap, null, r, paint);

        canvas.restore();
    }}
