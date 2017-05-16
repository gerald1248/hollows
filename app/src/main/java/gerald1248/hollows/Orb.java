package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * all static orb objects need to implement Orb
 */

public interface Orb {
    public void draw(Canvas canvas, Paint paint);
    public void onFire(Canvas canvas, Paint paint);
    public void onLand(Canvas canvas, Paint paint);
    public String getBannerText();
    public String[] getInfoLines();
}
