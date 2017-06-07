package gerald1248.hollows;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import org.magnos.impulse.Circle;

/**
 * The partner class to NextLevelOrb, this one lets the player skip back one level
 */

public class PreviousLevelOrb extends QualifiedShape implements Orb {
    private Context context = null;
    private Typeface typeface;

    public PreviousLevelOrb(Context context, float r, int x, int y, Typeface typeface) {
        super(new Circle(r), x, y, 0.0f);
        this.context = context;
        this.typeface = typeface;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float cx = (float)this.x;
        float cy = (float)this.y;
        canvas.drawCircle(cx, cy, shape.radius, paint);
        TextUtils.draw(canvas, context.getResources().getString(R.string.previous_level_label), Constants.FONT_SIZE_MEDIUM, cx, cy, Paint.Align.CENTER, Color.TRANSPARENT, typeface, true);
    }

    @Override
    public String getBannerText() {
        return context.getResources().getString(R.string.previous_level_banner);
    }

    @Override
    public String getAlertText() { return ""; }

    @Override
    public String[] getInfoLines() {
        return new String[]{};
    }
}
