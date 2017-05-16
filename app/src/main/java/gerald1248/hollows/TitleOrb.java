package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.magnos.impulse.Circle;

/**
 * basic information about the game
 * all information has to be obtainable from game objects
 */

public class TitleOrb extends QualifiedShape implements Orb {
    public TitleOrb(float r, int x, int y) {
        super(new Circle(r), x, y, 0.0f);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle((float)this.x, (float)this.y, shape.radius, paint);
    }

    @Override
    public void onFire(Canvas canvas, Paint paint) {
        //do nothing for now
    }

    @Override
    public void onLand(Canvas canvas, Paint paint) {

    }

    @Override
    public String getBannerText() {
        return "Hollows";
    }

    @Override
    public String[] getInfoLines() {
        return new String[]{"Move left \u2013 rotate anti-clockwise", "Move right \u2013 rotate clockwise", "Long press \u2013 accelerate", "Short press \u2013 fire"};
    }
}
