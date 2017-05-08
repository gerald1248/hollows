package gerald1248.hollows;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import java.util.LinkedList;

import org.magnos.impulse.Body;
import org.magnos.impulse.Circle;
import org.magnos.impulse.Polygon;
import org.magnos.impulse.Shape;
import org.magnos.impulse.Vec2;
import org.magnos.impulse.ImpulseScene;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Bitmap.createBitmap;

/**
 * LevelMap manages the static shapes/bodies that make up a level
 */

public class LevelMap {
    LinkedList<QualifiedShape> shapes = new LinkedList<QualifiedShape>();
    LinkedList<Body> staticBodies = new LinkedList<Body>();
    private char[][] charMap = new char[50][50];

    private Canvas offscreenCanvas = null;
    private Bitmap offscreenBitmap = null;

    private Context context;

    public LevelMap(Context context) {
        this.context = context;
        offscreenBitmap = createBitmap((int) Constants.MAX_MAP, (int) Constants.MAX_MAP, ARGB_8888);
        offscreenCanvas = new Canvas(offscreenBitmap);

        //initialize charMap: '.' represents a blank tile
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                charMap[i][j] = '.';
            }
        }

    }

    public void drawOffscreen() {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        if (Constants.DRAW_GRID) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.0f);
            for (int i = 100; i < 2500; i += 100) {
                offscreenCanvas.drawCircle(Constants.MAX_MAP / 2, Constants.MAX_MAP / 2, (float) i, paint);
            }
        }

        // draw circles here, rects/polygons in draw()
        paint.setStyle(Paint.Style.FILL);
        int len = shapes.size();
        for (int i = 0; i < len; i++) {
            QualifiedShape qs = shapes.get(i);
            Shape s = qs.shape;
            if (s instanceof Circle) {
                Circle c = (Circle) s;
                float r = c.radius;
                float x = qs.x;
                float y = qs.y;
                offscreenCanvas.drawCircle(x, y, r, paint);
            }
        }

        Resources resources = context.getResources();
        String[] levels = resources.getStringArray(R.array.levels);

        int row = 0, col = 0;
        String level = levels[0].trim();
        len = level.length();
        for (int i = 0; i < len; i++) {
            char c = level.charAt(i);
            if (Character.isWhitespace(c)) {
                row++;
                col = 0;
                continue;
            } else {
                Tile tile = new Tile(c, row, col);
                tile.draw(offscreenCanvas, paint);
                if (row < 50 && col < 50) {
                    charMap[row][col] = c;
                }
            }
            col++;
        }
    }

    public LinkedList<QualifiedShape> getShapes() {
        return shapes;
    }

    public void addPolygon(Vec2[] v, int x, int y, float orient) {
        shapes.add(new QualifiedShape(new Polygon(v), x, y, orient));
    }

    public void addRect(float w, float h, int x, int y, float orient) {
        shapes.add(new QualifiedShape(new Polygon(w, h), x, y, orient));
    }

    public void addCircle(float r, int cx, int cy) {
        shapes.add(new QualifiedShape(new Circle(r), cx, cy, 0.0f));
    }

    public void draw(Canvas canvas, float cx, float cy, int color) {
        Paint paint = new Paint();
        paint.setStrokeWidth((float) 2.0);
        paint.setColor(color);

        canvas.save();
        canvas.translate(cx + Constants.SCREEN_WIDTH / 2, cy + Constants.SCREEN_HEIGHT / 2);

        // copy from offscreen canvas
        Rect r = new Rect(0, 0, (int) Constants.MAX_MAP, (int) Constants.MAX_MAP);
        canvas.drawBitmap(offscreenBitmap, null, r, null);

        int len = shapes.size();
        for (int i = 0; i < len; i++) {
            QualifiedShape qs = shapes.get(i);
            Shape s = qs.shape;
            if (s instanceof Polygon) {
                Polygon p = (Polygon) s;
                int vertexCount = p.vertexCount;
                Path path = new Path();
                for (int j = 0; j < vertexCount; j++) {
                    Vec2 v = new Vec2(p.vertices[j]);
                    p.u.muli(v);
                    v.addi(new Vec2((float) qs.x, (float) qs.y));
                    if (j == 0) {
                        path.moveTo(v.x, v.y);
                    } else {
                        path.lineTo(v.x, v.y);
                    }
                }
                path.close();
                offscreenCanvas.drawPath(path, paint);
            }
        }

        canvas.restore();
    }

    public void initStaticShapes(ImpulseScene impulse) {
        // draw offscreen
        drawOffscreen();

        // now create corresponding physics objects
        for (int i = 0; i < shapes.size(); i++) {
            QualifiedShape s = shapes.get(i);
            Body b = impulse.add(s.shape, s.x, s.y);
            b.setOrient(s.orient);
            b.restitution = 0.2f;
            b.dynamicFriction = 0.2f;
            b.staticFriction = 0.4f;
            b.setStatic();
            staticBodies.add(b);
        }
    }

    public boolean collisionDetected(float cx, float cy, float r) {
        //consider rectangle
        float left = cx - r;
        float top = cy - r;
        float right = cx + r;
        float bottom = cy + r;

        int row = (int)Math.round(cy / Constants.TILE_LENGTH);
        int col = (int)Math.round(cx / Constants.TILE_LENGTH);

        if (row < 0 || row >= 50 || col < 0 || col >= 50) {
            return false;
        }

        char c = charMap[row][col];

        //debug collisions
        if (c != '.') {
            System.out.printf("[x=%.2f y=%.2f r=%.2f] row=%d col=%d charMap has: %c\n", cx, cy, r, row, col, charMap[row][col]);
        }
        //end debug

        return (c != '.');
    }
}
