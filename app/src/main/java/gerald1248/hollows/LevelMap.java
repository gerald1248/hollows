package gerald1248.hollows;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.LinkedList;

import org.magnos.impulse.Body;
import org.magnos.impulse.Circle;
import org.magnos.impulse.Polygon;
import org.magnos.impulse.Shape;
import org.magnos.impulse.Vec2;
import org.magnos.impulse.ImpulseScene;

import static android.graphics.Bitmap.createBitmap;

/**
 * LevelMap manages the static shapes/bodies that make up a level
 * it also holds linked lists of shapes and towers
 */

public class LevelMap {
    private static final String TAG = LevelMap.class.getSimpleName();

    LinkedList<QualifiedShape> shapes = new LinkedList<QualifiedShape>();
    LinkedList<QualifiedShape> towers = new LinkedList<QualifiedShape>();
    private char[][] charMap = new char[Constants.CHARMAP_LENGTH][Constants.CHARMAP_LENGTH];

    private Canvas offscreenCanvas = null;
    private Bitmap offscreenBitmap = null;
    private Typeface typeface = null;

    private Context context;

    private Point startPoint, endPoint;

    private Vec2[] collisionVertices = null;
    private int vertexCount = 4;

    private int levelIndex = 0;

    public LevelMap(Context context, Typeface typeface, int levelIndex) {
        this.context = context;
        this.typeface = typeface;
        this.levelIndex = levelIndex;

        offscreenBitmap = createBitmap((int) Constants.MAX_MAP, (int) Constants.MAX_MAP, Bitmap.Config.ARGB_8888);
        offscreenCanvas = new Canvas(offscreenBitmap);
        startPoint = new Point((int) Constants.MAX_MAP / 2, (int) Constants.MAX_MAP / 2); //sane default
        endPoint = new Point((int) Constants.MAX_MAP, (int) Constants.MAX_MAP);

        clearLevelMap();

        collisionVertices = new Vec2[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            collisionVertices[i] = new Vec2(0.0f, 0.0f);
        }
    }

    public void clearTowers() {
        towers.clear();
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
        shapes.clear();
        clearLevelMap();
        offscreenCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    public void drawGrid(Canvas canvas, int limit) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        float r = (float) limit / 2;
        for (int i = 0; i < limit / 2; i += limit / 20) {
            canvas.drawCircle(r, r, i, paint);
        }

        Path path = new Path();
        float f = (float) limit;
        for (int i = 0; i < limit; i += limit / 10) {
            path.moveTo((float) i, 0.0f);
            path.lineTo((float) i, 2.0f * f);
            path.moveTo(0.0f, (float) i);
            path.lineTo(2.0f * f, (float) i);
        }
        canvas.drawPath(path, paint);
    }

    public void drawOffscreen() {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        if (Constants.DRAW_GRID) {
            drawGrid(offscreenCanvas, (int) Constants.MAX_MAP);
        }

        Resources resources = context.getResources();
        String[] levels = resources.getStringArray(R.array.levels);
        if (levelIndex >= levels.length) {
            levelIndex = 0;
        }
        int row = 0, col = 0;
        String level = levels[levelIndex].trim();
        int len = level.length();
        float half = Constants.TILE_LENGTH / 2;
        for (int i = 0; i < len; i++) {
            char c = level.charAt(i);
            if (Character.isWhitespace(c)) {
                row++;
                col = 0;
                continue;
            } else if (Character.isDigit(c)) {
                int d = Character.getNumericValue(c);
                addOrb(Constants.TILE_LENGTH / 2.0f * (float) d, Math.round(col * Constants.TILE_LENGTH + half), Math.round((float) row * Constants.TILE_LENGTH + half));
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = '.';
                }
            } else if (c == 't') {
                addTitleOrb(Constants.TILE_LENGTH, Math.round(col * Constants.TILE_LENGTH + half), Math.round((float) row * Constants.TILE_LENGTH + half));
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = '.';
                }
            } else if (c == 'a') {
                addAudioOrb(Constants.TILE_LENGTH, Math.round(col * Constants.TILE_LENGTH + half), Math.round((float) row * Constants.TILE_LENGTH + half));
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = '.';
                }
            } else if (c == 'n') {
                addNextLevelOrb(Constants.TILE_LENGTH / 2, Math.round(col * Constants.TILE_LENGTH + half), Math.round((float) row * Constants.TILE_LENGTH + half));
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = '.';
                }
            } else if (c == 'p') {
                addPreviousLevelOrb(Constants.TILE_LENGTH / 2, Math.round(col * Constants.TILE_LENGTH + half), Math.round((float) row * Constants.TILE_LENGTH + half));
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = '.';
                }
            } else if (c == 'r') {
                addRedshiftOrb(Constants.TILE_LENGTH / 2, Math.round(col * Constants.TILE_LENGTH + half), Math.round((float) row * Constants.TILE_LENGTH + half));
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = '.';
                }
            } else if (c == 's') {
                startPoint = new Point(col * (int) Constants.TILE_LENGTH + (int) half, row * (int) Constants.TILE_LENGTH + (int) half);
                // no need to update charMap - '.' is fine
            } else if (c == 'e') {
                endPoint = new Point(col * (int) Constants.TILE_LENGTH + (int) half, row * (int) Constants.TILE_LENGTH + (int) half);
                addBaseOrb(Constants.TILE_LENGTH, endPoint.x, endPoint.y);
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = c;
                }
            } else if (c == 'w') {
                addTowerS((float) col * Constants.TILE_LENGTH + half, (float) row * Constants.TILE_LENGTH);
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = '.';
                }
            } else if (c == 'm') {
                addTowerN((float) col * Constants.TILE_LENGTH + half, (float) row * Constants.TILE_LENGTH + Constants.TILE_LENGTH);
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = '.';
                }
            } else {
                Tile tile = new Tile(c, row, col);
                tile.draw(offscreenCanvas, paint);
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = c;
                }
            }
            col++;
        }

        paint.setStyle(Paint.Style.FILL);
        int shapesLen = shapes.size();
        for (int i = 0; i < shapesLen; i++) {
            QualifiedShape qs = shapes.get(i);
            if (qs instanceof Orb) {
                Orb o = (Orb) qs;
                o.draw(offscreenCanvas, paint);
            }
        }
    }

    public LinkedList<QualifiedShape> getShapes() {
        return shapes;
    }

    public LinkedList<QualifiedShape> getTowers() {
        return towers;
    }

    public void addPolygon(Vec2[] v, int x, int y, float orient) {
        shapes.add(new QualifiedShape(new Polygon(v), x, y, orient));
    }

    public void addRect(float w, float h, int x, int y, float orient) {
        shapes.add(new QualifiedShape(new Polygon(w, h), x, y, orient));
    }

    public void addOrb(float r, int cx, int cy) {
        shapes.add(new GenericOrb(r, cx, cy));
    }

    public void addTitleOrb(float r, int cx, int cy) {
        shapes.add(new TitleOrb(context, r, cx, cy));
    }

    public void addAudioOrb(float r, int cx, int cy) {
        shapes.add(new AudioOrb(context, r, cx, cy, typeface));
    }

    public void addNextLevelOrb(float r, int cx, int cy) {
        shapes.add(new NextLevelOrb(context, r, cx, cy, typeface));
    }

    public void addPreviousLevelOrb(float r, int cx, int cy) {
        shapes.add(new PreviousLevelOrb(context, r, cx, cy, typeface));
    }

    public void addBaseOrb(float r, int cx, int cy) {
        shapes.add(new BaseOrb(context, r, cx, cy, typeface));
    }

    public void addRedshiftOrb(float r, int cx, int cy) {
        shapes.add(new RedshiftOrb(context, r, cx, cy, typeface));
    }

    public void addTowerS(float cx, float cy) {
        towers.add(new Tower(new Circle(Constants.TILE_LENGTH / 1.5f), (int) cx, (int) cy, (float) Math.PI / 2)); // 6 o'clock
    }

    public void addTowerN(float cx, float cy) {
        towers.add(new Tower(new Circle(Constants.TILE_LENGTH / 1.5f), (int) cx, (int) cy, -(float) Math.PI / 2)); // 12 o'clock
    }

    public void draw(Canvas canvas, float cx, float cy, int color) {
        Paint paint = new Paint();
        paint.setColor(color);

        canvas.save();

        int x = (int) cx;
        int y = (int) cy;
        int w = Constants.SCREEN_WIDTH;
        int h = Constants.SCREEN_HEIGHT;
        Rect rSrc = new Rect(x - w/2, y - h/2, x + w/2, y + h/2);
        Rect rDest = new Rect(0, 0, w, h);
        canvas.drawBitmap(offscreenBitmap, rSrc, rDest, paint);

        // now draw towers - these aren't physical objects in the game
        for (QualifiedShape qs : towers) {
            Tower t = (Tower) qs;
            if (Collision.circleRect(t.x, t.y, (int) t.shape.radius, rSrc)) {
                int adjX = t.x - x + w/2;
                int adjY = t.y - y + h/2;
                canvas.drawCircle(adjX, adjY, t.shape.radius, paint);
            }
        }

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
        }
    }

    public boolean detectCollision(float cx, float cy, float r, float orient) {
        //nose
        collisionVertices[0].x = cx + r * (float) Math.cos(orient);
        collisionVertices[0].y = cy + r * (float) Math.sin(orient);

        //bottom right corner
        collisionVertices[1].x = cx + r * (float) Math.cos(orient + Math.PI * 0.75);
        collisionVertices[1].y = cy + r * (float) Math.sin(orient + Math.PI * 0.75);

        //bottom left corner
        collisionVertices[2].x = cx + r * (float) Math.cos(orient + Math.PI * 1.25);
        collisionVertices[2].y = cy + r * (float) Math.sin(orient + Math.PI * 1.25);

        //center
        collisionVertices[3].x = cx;
        collisionVertices[3].y = cy;

        for (int i = 0; i < vertexCount; i++) {
            float x = collisionVertices[i].x;
            float y = collisionVertices[i].y;

            int row = (int) Math.floor(y / Constants.TILE_LENGTH);
            int col = (int) Math.floor(x / Constants.TILE_LENGTH);

            if (row < 0 || row >= 50 || col < 0 || col >= 50) {
                continue;
            }

            char type = charMap[row][col];
            if (type == '.') {
                continue;
            }

            float xOffset = x % Constants.TILE_LENGTH;
            float yOffset = y % Constants.TILE_LENGTH;
            if (Tile.detectCollision(type, xOffset, yOffset)) {
                return true;
            }
        }
        return false;
    }

    public QualifiedShape detectShapeCollision(float cx, float cy, float r) {
        // all towers are (half-)circles
        for (QualifiedShape qs : towers) {
            Tower t = (Tower) qs;
            float r2 = t.shape.radius;
            float x2 = t.x;
            float y2 = t.y;

            if (Collision.circleCircle(cx, cy, x2, y2, r, r2)) {
                return qs;
            }
        }

        for (QualifiedShape qs : shapes) {
            Shape s = qs.shape;
            if (s instanceof Circle) {
                float r2 = s.radius;
                float x2 = qs.x;
                float y2 = qs.y;

                if (Collision.circleCircle(cx, cy, x2, y2, r, r2)) {
                    return qs;
                }
            }
        }

        return null;
    }

    public void removeTower(Tower t) {
        towers.remove(t);
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    private void clearLevelMap() {
        //initialize charMap: '.' represents a blank tile
        for (int i = 0; i < Constants.CHARMAP_LENGTH; i++) {
            for (int j = 0; j < Constants.CHARMAP_LENGTH; j++) {
                charMap[i][j] = '.';
            }
        }
    }
}
