package gerald1248.hollows;

import org.junit.Test;

import java.util.LinkedList;

import org.magnos.impulse.Circle;
import org.magnos.impulse.ImpulseMath;
import org.magnos.impulse.Polygon;
import org.magnos.impulse.Shape;
import org.magnos.impulse.Vec2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class LevelMapTest {
    private int x = 100;
    private int y = 100;
    private float orient = 0.0f;

    @Test
    public void addRect_Successful() throws Exception {
        LevelMap map = new LevelMap(null);
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

    @Test
    public void addPolygon_Successful() throws Exception {
        LevelMap map = new LevelMap(null);
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

    @Test
    public void addCircle_Successful() throws Exception {
        LevelMap map = new LevelMap(null);
        float r = 30.0f;
        map.addCircle(r, x, y);

        LinkedList<QualifiedShape> shapes = map.getShapes();
        assertThat(shapes.size(), is(1));

        Shape shape = shapes.get(0).shape;
        assertThat(shape instanceof Circle, is(true));
    }
}