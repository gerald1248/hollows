package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.magnos.impulse.Circle;

/**
 * Generic orbs follow the map's radius setting but have no additional capabilities
 */

public class GenericOrb extends QualifiedShape implements Orb {
    public GenericOrb(float r, int x, int y) {
        super(new Circle(r), x, y, 0.0f);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle((float) this.x, (float) this.y, shape.radius, paint);
    }

    @Override
    public String getBannerText() {
        return "";
    }

    @Override
    public String getAlertText() {
        return "";
    }

    @Override
    public String[] getInfoLines() {
        return new String[]{};
    }

}
