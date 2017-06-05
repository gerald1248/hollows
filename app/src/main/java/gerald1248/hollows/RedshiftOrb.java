package gerald1248.hollows;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import org.magnos.impulse.Circle;

/**
 * This is the invisible trigger for redshift mode,
 * which improves battery life and offers unlimited level-ups
 * at the price of turning the whole screen red
 */

public class RedshiftOrb extends QualifiedShape implements Orb {
    private Context context = null;
    private Typeface typeface;

    public RedshiftOrb(Context context, float r, int x, int y, Typeface typeface) {
        super(new Circle(r), x, y, 0.0f);
        this.context = context;
        this.typeface = typeface;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        // these are invisible, so don't draw anything
    }

    @Override
    public void onFire() {

    }

    @Override
    public void onLand() {

    }

    @Override
    public String getBannerText() {
        return context.getResources().getString(R.string.redshift_banner);
    }

    @Override
    public String getAlertText() { return ""; }

    @Override
    public String[] getInfoLines() {
        return new String[]{};
    }
}
