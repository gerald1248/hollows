package gerald1248.hollows;

import magnos.impulse.Shape;

/**
 * Qualified shapes carry all information required to
 * (a) add them to an impulse scene and
 * (b) draw them
 */

public class QualifiedShape {
    public Shape shape;
    public int x, y;
    float orient;

    public QualifiedShape(Shape shape, int x, int y, float orient) {
        this.shape = shape;
        this.x = x;
        this.y = y;
        this.orient = orient;
    }
}
