package gerald1248.hollows;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import org.magnos.impulse.Circle;

/**
 * The orb that houses any remaining targets
 * The homing device points to the center of this orb
 * There must be exactly one BaseOrb in every level
 */

public class BaseOrb extends QualifiedShape implements Orb {
    private Context context;
    private Typeface typeface;

    public BaseOrb(Context context, float r, int x, int y, Typeface typeface) {
        super(new Circle(r), x, y, 0.0f);
        this.context = context;
        this.typeface = typeface;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float cx = (float)this.x;
        float cy = (float)this.y;
        canvas.drawCircle(cx, cy, shape.radius, paint);

        TextUtils.draw(canvas, context.getResources().getString(R.string.base_label), Constants.FONT_SIZE_MEDIUM, cx, cy, Paint.Align.CENTER, Color.TRANSPARENT, typeface, true);
    }

    @Override
    public void onFire() {
        //do nothing for now
    }

    @Override
    public void onLand() {

    }

    @Override
    public String getAlertText() {
        return "";
    }

    @Override
    public String getBannerText() {
        return context.getResources().getString(R.string.base_banner);
    }

    @Override
    public String[] getInfoLines() {
        return new String[]{};
    }
}
