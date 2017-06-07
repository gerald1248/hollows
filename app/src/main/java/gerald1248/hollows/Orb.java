package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * all static orb objects need to implement Orb
 */

public interface Orb {
    void draw(Canvas canvas, Paint paint);
    String getBannerText();
    String getAlertText();
    String[] getInfoLines();
}
