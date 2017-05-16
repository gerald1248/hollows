package gerald1248.hollows;

import android.graphics.Canvas;

/**
 * Projectiles are objects emanating from the player's ship at the center of the screen
 * e.g. Wave and Laser objects
 */

public interface Projectile {
    public void draw(Canvas canvas);
    public boolean isDone();
}
