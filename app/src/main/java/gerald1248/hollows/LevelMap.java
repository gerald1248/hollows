package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.LinkedList;

import magnos.impulse.Body;
import magnos.impulse.Circle;
import magnos.impulse.Polygon;
import magnos.impulse.Shape;
import magnos.impulse.Vec2;
import magnos.impulse.ImpulseScene;

/**
 * LevelMap manages the static shapes/bodies that make up a level
 */

public class LevelMap {
    // TODO: git-friendly JSON maps
    // TODO: dedicated canvas - map drawn once, then scaled on pivot point

    LinkedList<QualifiedShape> shapes = new LinkedList<QualifiedShape>();
    LinkedList<Body> staticBodies = new LinkedList<Body>();

    public LevelMap() {
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

        if (Constants.DRAW_GRID) {
            paint.setStyle(Paint.Style.STROKE);
            // draw grid
            for (float f = 0.0F; f < 10000.0F; f += 1000.0F) {
                Path xAxis = new Path();
                xAxis.moveTo(0.0F, f);
                xAxis.lineTo(10000.0F, f);
                xAxis.close();
                canvas.drawPath(xAxis, paint);
            }

            for (float f = 0.0F; f < 10000.0F; f += 1000.0F) {
                Path yAxis = new Path();
                yAxis.moveTo(f, 0.0F);
                yAxis.lineTo(f, 10000.0F);
                yAxis.close();
                canvas.drawPath(yAxis, paint);
            }

            for (float r = 500.0F; r < 5000.0F; r += 500.0F) {
                RectF rect = new RectF(5000.0F - r, 5000.0F - r, 5000.0F + r, 5000.0F + r);
                canvas.drawArc(rect, 0.0F, 360.0F, false, paint);
            }
            paint.setStyle(Paint.Style.FILL);
        }

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
                canvas.drawPath(path, paint);
            } else if (s instanceof Circle) {
                Circle c = (Circle) s;
                float r = c.radius;
                float x = qs.x;
                float y = qs.y;
                canvas.drawCircle(x, y, r, paint);
            }
        }
        canvas.restore();
    }

    public void initStaticShapes(ImpulseScene impulse) {
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
}
