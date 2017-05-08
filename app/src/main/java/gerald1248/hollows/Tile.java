package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Tiles make up the static components of level maps
 * They are not bodies in the sense of the Impulse engine
 * Typically this means that objects that collide with tiles are destroyed
 */


public class Tile {
    private char type;
    private int row, col;
    float left, top, right, bottom;
    float side = Constants.TILE_LENGTH;

    Tile(char type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.left = (float)this.col * side;
        this.top = (float)this.row * side;
        this.right = this.left + this.side;
        this.bottom = this.top + this.side;
    }

    void draw(Canvas canvas, Paint paint) {
        if (type == '.') {
            return;
        }
        Path p = new Path();
        switch (type) {
            case '+':
                float rand = (float)Math.random();
                float mid = rand * side;
                float elev = rand * 4.0f;
                p.moveTo(left, top);
                p.lineTo(left + mid, top - elev);
                p.lineTo(right, top);
                p.lineTo(right + elev, top + mid);
                p.lineTo(right, bottom);
                p.lineTo(right - mid, bottom + elev);
                p.lineTo(left, bottom);
                p.close();
                canvas.drawPath(p, paint);
                break;
            case '|': //approx. for backslash
                p.moveTo(left, top);
                p.lineTo(right, bottom);
                p.lineTo(left, bottom);
                p.close();
                canvas.drawPath(p, paint);
                break;
            case '/':
                p.moveTo(left, bottom);
                p.lineTo(right, top);
                p.lineTo(right, bottom);
                p.close();
                canvas.drawPath(p, paint);
                break;
            case '`':
                p.moveTo(left, top);
                p.lineTo(right, top);
                p.lineTo(right, bottom);
                p.close();
                canvas.drawPath(p, paint);
                break;
            case '‘':
                p.moveTo(left, top);
                p.lineTo(right, top);
                p.lineTo(left, bottom);
                p.close();
                canvas.drawPath(p, paint);
                break;
            case '^':
                p.moveTo(left, bottom);
                p.lineTo(left + side/2, top);
                p.lineTo(right, bottom);
                p.close();
                canvas.drawPath(p, paint);
                break;
            default:
                break;
        }
    }
}
