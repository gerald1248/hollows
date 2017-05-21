package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * all static orb objects need to implement Orb
 */

public interface Orb {
    public void draw(Canvas canvas, Paint paint);
    public void onLand(); //TODO: necessary?
    public void onFire();
    public String getBannerText();
    public String getAlertText();
    public String[] getInfoLines();
}
