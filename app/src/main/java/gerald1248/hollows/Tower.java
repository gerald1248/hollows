package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.magnos.impulse.Shape;

/**
 * Towers are partially hidden circles attached to walls
 * They fire at intervals in the direction of the player
 * and can in turn be eliminated by the ship's laser cannon
 */

public class Tower extends QualifiedShape implements Orb {
    public Tower(Shape shape, int x, int y, float orient) {
        super(shape, x, y, orient);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {

    }

    @Override
    public void onLand() {

    }

    @Override
    public void onFire() {

    }

    @Override
    public String getBannerText() {
        return "";
    }

    @Override
    public String[] getInfoLines() {
        return new String[0];
    }
}
