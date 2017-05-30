package gerald1248.hollows;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.magnos.impulse.Circle;
import org.magnos.impulse.ImpulseMath;
import org.magnos.impulse.Polygon;
import org.magnos.impulse.Shape;
import org.magnos.impulse.Vec2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LevelMapTest {
    Context appContext = null;
    Resources resources = null;
    Typeface typeface = null;
    private int x = 100;
    private int y = 100;
    private float orient = 0.0f;

    @Test
    public void useAppContext() throws Exception {
        appContext = InstrumentationRegistry.getTargetContext();
        resources = appContext.getResources();
        typeface = Typeface.DEFAULT;
        addRect_Successful();
        addPolygon_Successful();
        addCircle_Successful();
    }

    public void addRect_Successful() throws Exception {
        LevelMap map = new LevelMap(appContext, typeface, 0);
        float w = 100.0f;
        float h = 50.0f;

        map.addRect(w, h, x, y, orient);

        LinkedList<QualifiedShape> shapes = map.getShapes();
        assertThat(shapes.size(), is(1));

        Shape shape = shapes.get(0).shape;
        assertThat(shape instanceof Polygon, is(true));

        Polygon p = (Polygon) shape;
        assertThat(p.vertexCount, is(4));
    }

    public void addPolygon_Successful() throws Exception {
        Typeface typeface = Typeface.DEFAULT;
        LevelMap map = new LevelMap(appContext, typeface, 0);
        int len = Polygon.MAX_POLY_VERTEX_COUNT;

        Vec2[] v = Vec2.arrayOf(len);
        float r = ImpulseMath.random(10.0f, 50.0f);
        for (int i = 0; i < len; i++) {
            v[i].set(ImpulseMath.random(-r, r), ImpulseMath.random(-r, r));
        }

        map.addPolygon(v, x, y, orient);

        LinkedList<QualifiedShape> shapes = map.getShapes();
        assertThat(shapes.size(), is(1));

        Shape shape = shapes.get(0).shape;
        assertThat(shape instanceof Polygon, is(true));
    }

    public void addCircle_Successful() throws Exception {
        LevelMap map = new LevelMap(appContext, typeface, 0);
        float r = 30.0f;
        map.addOrb(r, x, y);

        LinkedList<QualifiedShape> shapes = map.getShapes();
        assertThat(shapes.size(), is(1));

        Shape shape = shapes.get(0).shape;
        assertThat(shape instanceof Circle, is(true));
    }
}