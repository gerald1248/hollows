package gerald1248.hollows;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
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

    private Point startPoint, endPoint;

    private int levelIndex = 0;

    public LevelMap(Context context) {
        this.context = context;
        offscreenBitmap = createBitmap((int) Constants.MAX_MAP, (int) Constants.MAX_MAP, ARGB_8888);
        offscreenCanvas = new Canvas(offscreenBitmap);
        startPoint = new Point((int) Constants.MAX_MAP / 2, (int) Constants.MAX_MAP / 2); //sane default
        endPoint = new Point((int) Constants.MAX_MAP, (int) Constants.MAX_MAP);
        clearLevelMap();
    }

    private void clearLevelMap() {
        //initialize charMap: '.' represents a blank tile
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                charMap[i][j] = '.';
            }
        }
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
        shapes.clear();
        staticBodies.clear();
        clearLevelMap();
        offscreenCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
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

        Resources resources = context.getResources();
        String[] levels = resources.getStringArray(R.array.levels);
        if (levelIndex >= levels.length) {
            levelIndex = 0;
        }
        int row = 0, col = 0;
        String level = levels[levelIndex].trim();
        int len = level.length();
        for (int i = 0; i < len; i++) {
            char c = level.charAt(i);
            if (Character.isWhitespace(c)) {
                row++;
                col = 0;
                continue;
            } else if (Character.isDigit(c)) {
                int d = Character.getNumericValue(c);
                addCircle(Constants.TILE_LENGTH / 2.0f * (float) d, Math.round(col * Constants.TILE_LENGTH), Math.round((float) row * Constants.TILE_LENGTH));
                if (row < 50 && col < 50) {
                    charMap[row][col] = '.';
                }
            } else if (c == 's') {
                startPoint = new Point(col * (int) Constants.TILE_LENGTH, row * (int) Constants.TILE_LENGTH);
                // no need to update charMap - '.' is fine
                // TODO: use startPoint
            } else if (c == 'e') {
                endPoint = new Point(col * (int) Constants.TILE_LENGTH, row * (int) Constants.TILE_LENGTH);
                addCircle(Constants.TILE_LENGTH, endPoint.x, endPoint.y);
                if (row < 50 && col < 50) {
                    charMap[row][col] = c;
                }
            }
            else {
                Tile tile = new Tile(c, row, col);
                tile.draw(offscreenCanvas, paint);
                if (row < 50 && col < 50) {
                    charMap[row][col] = c;
                }
            }
            col++;
        }

        paint.setStyle(Paint.Style.FILL);
        int shapesLen = shapes.size();
        for (int i = 0; i < shapesLen; i++) {
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

        canvas.restore();
    }

    public void initStaticShapes(ImpulseScene impulse) {
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
            staticBodies.add(b); //not used yet
        }
    }

    public boolean collisionDetected(float cx, float cy, float r) {
        //TODO: add collision rules for each shape

        //consider rectangle
        float left = cx - r;
        float top = cy - r;
        float right = cx + r;
        float bottom = cy + r;

        int row = (int) Math.round(cy / Constants.TILE_LENGTH);
        int col = (int) Math.round(cx / Constants.TILE_LENGTH);

        if (row < 0 || row >= 50 || col < 0 || col >= 50) {
            return false;
        }

        char c = charMap[row][col];

        return (c != '.');
    }

    public QualifiedShape shapeCollisionDetected(float cx, float cy, float r) {
        for (int i = 0; i < shapes.size(); i++) {
            QualifiedShape qs = shapes.get(i);
            Shape s = qs.shape;
            if (s instanceof Circle) {
                float r2 = s.radius;
                float x2 = qs.x;
                float y2 = qs.y;

                float d = (float) Math.hypot((double) x2 - (double) cx, (double) y2 - (double) cy);

                if (d < r + r2) {
                    //shape is part of offline canvas, so keep it and animate main canvas
                    return qs;
                }
            }
        }
        return null;
    }

    public Point getEndPoint() {
        return endPoint;
    }
    public Point getStartPoint() { return startPoint; }
}
