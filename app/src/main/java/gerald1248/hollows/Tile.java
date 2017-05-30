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
    private float left, top, right, bottom;
    private float side = Constants.TILE_LENGTH;
    private float maxElev = 4.0f;

    Tile(char type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.left = (float)this.col * side;
        this.top = (float)this.row * side;
        this.right = this.left + this.side;
        this.bottom = this.top + this.side;
    }

    public char getType() {
        return type;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    void draw(Canvas canvas, Paint paint) {
        if (type == '.') {
            return;
        }

        float rand = (float)Math.random();
        float mid = rand * side;
        float elev = 0.5f + rand * maxElev;
        float sign = (rand > 0.5) ? -1.0f : 1.0f;

        Path p = new Path();
        switch (type) {
            case '+':
                p.moveTo(left, top);
                p.lineTo(left + mid, top - elev);
                p.lineTo(right, top);
                p.lineTo(right + elev, top + mid);
                p.lineTo(right, bottom);
                p.lineTo(right - mid, bottom + elev);
                p.lineTo(left, bottom);
                p.lineTo(left - elev, top + mid);
                p.close();
                canvas.drawPath(p, paint);
                break;
            case '|': //approx. for backslash
                p.moveTo(left, top);
                p.lineTo(left + side/2 + elev * sign, top + side/2);
                p.lineTo(right, bottom);
                p.lineTo(left + mid, bottom + elev);
                p.lineTo(left, bottom);
                p.lineTo(left - elev, top + mid);
                p.close();
                canvas.drawPath(p, paint);
                break;
            case '/':
                p.moveTo(left, bottom);
                p.lineTo(left + side/2 + elev * sign, top + side/2);
                p.lineTo(right, top);
                p.lineTo(right + elev, top + mid);
                p.lineTo(right, bottom);
                p.lineTo(left + mid, bottom + elev);
                p.close();
                canvas.drawPath(p, paint);
                break;
            case '`':
                p.moveTo(left, top);
                p.lineTo(left + mid, top - elev);
                p.lineTo(right, top);
                p.lineTo(right + elev, top + mid);
                p.lineTo(right, bottom);
                p.lineTo(left + side/2 + sign * elev, top + side/2);
                p.close();
                canvas.drawPath(p, paint);
                break;
            case '´':
                p.moveTo(left, top);
                p.lineTo(left + mid, top - elev);
                p.lineTo(right, top);
                p.lineTo(left + side/2, top + side/2 + sign * elev);
                p.lineTo(left, bottom);
                p.lineTo(left - elev, top + side/2);
                p.close();
                canvas.drawPath(p, paint);
                break;
            case '^':
                p.moveTo(left, bottom);
                p.lineTo(left + side/2 + sign * elev, top + sign * elev);
                p.lineTo(right, bottom);
                p.lineTo(left + side/2, bottom + elev);
                p.close();
                canvas.drawPath(p, paint);
                break;
            case 'v':
                p.moveTo(left, top);
                p.lineTo(left + side/2, top - elev);
                p.lineTo(right, top);
                p.lineTo(left + side/2 + sign * elev, bottom + sign * elev);
                p.close();
                canvas.drawPath(p, paint);
                break;
            default:
                break;
        }
    }
    static public boolean detectCollision(char type, float x, float y) {
        switch (type) {
            case '+':
                return true;
            case '|': //approx. for backslash
                return x < y;
            case '/':
                return x + y >= Constants.TILE_LENGTH;
            case '`':
                return x > y;
            case '´':
                return x + y <= Constants.TILE_LENGTH;
            case '^':
                //TODO
                return x > Constants.TILE_LENGTH * 0.25f && x < Constants.TILE_LENGTH * 0.75f;
            case 'v':
                //TODO
                return x > Constants.TILE_LENGTH * 0.25f && x < Constants.TILE_LENGTH * 0.75f;
            default:
                break;
        }
        return false;
    }
}
