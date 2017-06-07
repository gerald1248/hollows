package gerald1248.hollows;

import android.graphics.Canvas;

import org.magnos.impulse.Body;

/**
 * Projectiles are objects emanating from the player's ship at the center of the screen
 * e.g. Wave and Laser objects
 */

public interface Projectile {
    void draw(Canvas canvas, int color);

    boolean isDone();

    void setObserver(Body observer);

    void setVelocityFactor(float f);
}
