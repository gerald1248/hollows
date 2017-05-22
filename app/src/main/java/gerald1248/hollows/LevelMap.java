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
import android.graphics.Typeface;

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
 * it also holds linked lists of shapes and towers
 */

public class LevelMap {
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

    public LevelMap(Context context, Typeface typeface) {
        this.context = context;
        this.typeface = typeface;

        //TODO: use ALPHA_8 instead
        offscreenBitmap = createBitmap((int) Constants.MAX_MAP, (int) Constants.MAX_MAP, ARGB_8888);
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

    public void drawOffscreen() {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

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
                addOrb(Constants.TILE_LENGTH / 2.0f * (float) d, Math.round(col * Constants.TILE_LENGTH), Math.round((float) row * Constants.TILE_LENGTH));
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = '.';
                }
            } else if (c == 't') {
                addTitleOrb(Constants.TILE_LENGTH, Math.round(col * Constants.TILE_LENGTH), Math.round((float) row * Constants.TILE_LENGTH));
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = '.';
                }
            } else if (c == 'a') {
                addAudioOrb(Constants.TILE_LENGTH, Math.round(col * Constants.TILE_LENGTH), Math.round((float) row * Constants.TILE_LENGTH));
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = '.';
                }
            } else if (c == 's') {
                startPoint = new Point(col * (int) Constants.TILE_LENGTH, row * (int) Constants.TILE_LENGTH);
                // no need to update charMap - '.' is fine
            } else if (c == 'e') {
                endPoint = new Point(col * (int) Constants.TILE_LENGTH, row * (int) Constants.TILE_LENGTH);
                addOrb(Constants.TILE_LENGTH, endPoint.x, endPoint.y);
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = c;
                }
            } else if (c == 'w') {
                addTowerS((float)col * Constants.TILE_LENGTH + Constants.TILE_LENGTH/2, (float)row * Constants.TILE_LENGTH);
                if (row < Constants.CHARMAP_LENGTH && col < Constants.CHARMAP_LENGTH) {
                    charMap[row][col] = '.';
                }
            } else if (c == 'm') {
                addTowerN((float)col * Constants.TILE_LENGTH + Constants.TILE_LENGTH/2, (float)row * Constants.TILE_LENGTH + Constants.TILE_LENGTH);
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

    public void addTowerS(float cx, float cy) {
        towers.add(new Tower(new Circle(Constants.TILE_LENGTH/1.5f), (int)cx, (int)cy, (float)Math.PI/2)); // 6 o'clock
    }

    public void addTowerN(float cx, float cy) {
        towers.add(new Tower(new Circle(Constants.TILE_LENGTH/2), (int)cx, (int)cy, -(float)Math.PI/2)); // 12 o'clock
    }

    public void addAudioOrb(float r, int cx, int cy) {
        shapes.add(new AudioOrb(context, r, cx, cy, typeface));
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

        // now draw towers
        for (QualifiedShape qs : towers) {
            Tower t = (Tower)qs;
            canvas.drawCircle(t.x, t.y, t.shape.radius, paint);
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

            int row = (int)Math.floor(y / Constants.TILE_LENGTH);
            int col = (int)Math.floor(x / Constants.TILE_LENGTH);

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
                /*
                //debug collisions
                System.out.printf("[vertex=%d] collision orient=%.2f col=%d row=%d type=%c x=%.2f y=%.2f\n", i, orient, col, row, type, xOffset, yOffset);
                */
                return true;
            }
        }
        return false;
    }

    //TODO: move collision logic to Orb objects
    public QualifiedShape detectShapeCollision(float cx, float cy, float r) {
        for (QualifiedShape qs : shapes) {
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

        // all towers are (half-)circles
        for (QualifiedShape qs : towers) {
            Tower t = (Tower)qs;
            float r2 = t.shape.radius;
            float x2 = t.x;
            float y2 = t.y;

            float d = (float) Math.hypot((double) x2 - (double) cx, (double) y2 - (double) cy);

            if (d < r + r2) {
                return qs;
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
