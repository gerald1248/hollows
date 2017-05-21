package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * text drawing boilerplate
 */

public class TextUtils {
    static void draw(Canvas canvas, String text, float size, float x, float y, Paint.Align align, int color, Typeface typeface) {
        canvas.save();
        Paint p = new Paint();
        Rect r = new Rect();
        p.setColor(color);
        p.setTextAlign(align);
        p.setTextSize(size);
        p.setAntiAlias(true);
        p.setTypeface(typeface);

        //vertical centering
        if (align == Paint.Align.CENTER) {
            p.getTextBounds(text, 0, text.length(), r);
            y += r.height()/2;
        }
        canvas.drawText(text, x, y, p);
        canvas.restore();
    }
}
