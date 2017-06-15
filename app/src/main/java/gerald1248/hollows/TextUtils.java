package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * text drawing boilerplate
 */

public class TextUtils {
    //exp
    static Paint p = new Paint();
    static Rect r = new Rect();
    static PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    static void draw(Canvas canvas, String text, float size, float x, float y, Paint.Align align, int color, Typeface typeface, boolean clear) {
        canvas.save();
        //Paint p = new Paint();
        //Rect r = new Rect();

        p.reset();

        if (clear == false) {
            p.setColor(color);
        } else {
            p.setXfermode(mode);
        }

        p.setTextAlign(align);
        p.setTextSize(size);
        p.setAntiAlias(true);
        p.setTypeface(typeface);

        //vertical centering
        if (align == Paint.Align.CENTER) {
            p.getTextBounds(text, 0, text.length(), r);
            y += r.height() / 2;
        }
        canvas.drawText(text, x, y, p);
        canvas.restore();
    }
}
