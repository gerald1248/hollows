package gerald1248.hollows;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;

import org.magnos.impulse.Circle;

import static gerald1248.hollows.R.string.app_name;
import static gerald1248.hollows.R.string.info_line1;
import static gerald1248.hollows.R.string.info_line2;
import static gerald1248.hollows.R.string.info_line3;
import static gerald1248.hollows.R.string.info_line4;

/**
 * basic information about the game
 * all information has to be obtainable from game objects
 */

public class TitleOrb extends QualifiedShape implements Orb {
    private Context context;

    public TitleOrb(Context context, float r, int x, int y) {
        super(new Circle(r), x, y, 0.0f);
        this.context = context;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle((float) this.x, (float) this.y, shape.radius, paint);
    }

    @Override
    public String getAlertText() {
        return "";
    }

    @Override
    public String getBannerText() {
        return context.getResources().getString(app_name);
    }

    @Override
    public String[] getInfoLines() {
        Resources r = context.getResources();
        return new String[]{
                r.getString(info_line1),
                r.getString(info_line2),
                r.getString(info_line3),
                r.getString(info_line4)
        };
    }
}
