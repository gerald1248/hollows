package gerald1248.hollows;

import android.graphics.Canvas;

import org.magnos.impulse.Body;

/**
 * Projectiles are objects emanating from the player's ship at the center of the screen
 * e.g. Wave and Laser objects
 */

public interface Projectile {
    public void draw(Canvas canvas);
    public boolean isDone();
    public void setObserver(Body observer);
    public void setVelocityFactor(float f);
}
