package gerald1248.hollows;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * No collisions, no game, so make sure they work correctly
 */

public class CollisionTest {
    float x1 = 100.0f;
    float y1 = 100.0f;
    float r1 = 50.0f;

    @Test
    public void detectCollisionCircleCircleWithin() throws Exception {
        float x2 = 110.0f;
        float y2 = 110.0f;
        float r2 = 10.0f;
        assertThat(Collision.circleCircle(x1, y1, x2, y2, r1, r2), is(true));
    }

    @Test
    public void detectCollisionCircleCircleIdentity() throws Exception {
        float x2 = x1;
        float y2 = y1;
        float r2 = r1;
        assertThat(Collision.circleCircle(x1, y1, x2, y2, r1, r2), is(true));
    }

    @Test
    public void detectCollisionCircleCircleNorth() throws Exception {
        float x2 = 100.0f;
        float y2 = 40.0f;
        float r2 = 20.0f;
        assertThat(Collision.circleCircle(x1, y1, x2, y2, r1, r2), is(true));
        assertThat(Collision.circleCircle(x1, y1, x2, y2 - r2, r1, r2), is(false));
    }

    @Test
    public void detectCollisionCircleCircleEast() throws Exception {
        float x2 = 160.0f;
        float y2 = 100.0f;
        float r2 = 20.0f;
        assertThat(Collision.circleCircle(x1, y1, x2, y2, r1, r2), is(true));
        assertThat(Collision.circleCircle(x1, y1, x2 + r2, y2, r1, r2), is(false));
    }

    @Test
    public void detectCollisionCircleCircleSouth() throws Exception {
        float x2 = 100.0f;
        float y2 = 160.0f;
        float r2 = 20.0f;
        assertThat(Collision.circleCircle(x1, y1, x2, y2, r1, r2), is(true));
        assertThat(Collision.circleCircle(x1, y1, x2, y2 + r2, r1, r2), is(false));
    }

    @Test
    public void detectCollisionCircleCircleWest() throws Exception {
        float x2 = 40.0f;
        float y2 = 100.0f;
        float r2 = 20.0f;
        assertThat(Collision.circleCircle(x1, y1, x2, y2, r1, r2), is(true));
        assertThat(Collision.circleCircle(x1, y1, x2 - r2, y2, r1, r2), is(false));
    }
}
