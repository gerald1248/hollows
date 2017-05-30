package gerald1248.hollows;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TileTest {
    float len = Constants.TILE_LENGTH;

    @Test
    public void constructor_Successful() throws Exception {
        char type = '+';
        int row = 0;
        int col = 0;
        Tile t = new Tile(type, row, col);
        assertThat(t.getType(), is(type));
        assertThat(t.getRow(), is(row));
        assertThat(t.getCol(), is(col));
    }

    @Test
    public void detectCollisionSolid_Successful() throws Exception {
        //middle plus corners NW, NE, SE, SW
        assertThat(Tile.detectCollision('+', len/2, len/2), is(true));
        assertThat(Tile.detectCollision('+', 0.0f, 0.0f), is(true));
        assertThat(Tile.detectCollision('+', len, 0.0f), is(true));
        assertThat(Tile.detectCollision('+', len, len), is(true));
        assertThat(Tile.detectCollision('+', 0.0f, len), is(true));
    }

    @Test
    public void detectCollisionCornerNW_Successful() throws Exception {
        assertThat(Tile.detectCollision('´', len/2, len/2 - 1.0f), is(true));
        assertThat(Tile.detectCollision('´', len/2, len/2 + 1.0f), is(false));
        assertThat(Tile.detectCollision('´', 0.0f, 0.0f), is(true));
        assertThat(Tile.detectCollision('´', len, 0.0f), is(true));
        assertThat(Tile.detectCollision('´', len, len), is(false));
        assertThat(Tile.detectCollision('´', 0.0f, len), is(true));
    }

    @Test
    public void detectCollisionCornerNE_Successful() throws Exception {
        assertThat(Tile.detectCollision('`', len/2 + 1.0f, len/2), is(true));
        assertThat(Tile.detectCollision('`', len/2 - 1.0f, len/2), is(false));
        assertThat(Tile.detectCollision('`', 1.0f, 0.0f), is(true));
        assertThat(Tile.detectCollision('`', len, 0.0f), is(true));
        assertThat(Tile.detectCollision('`', len, len - 1.0f), is(true));
        assertThat(Tile.detectCollision('`', 0.0f, len), is(false));
    }

    @Test
    public void detectCollisionCornerSE_Successful() throws Exception {
        assertThat(Tile.detectCollision('/', len/2, len/2 + 1.0f), is(true));
        assertThat(Tile.detectCollision('/', len/2, len/2 - 1.0f), is(false));
        assertThat(Tile.detectCollision('/', 0.0f, 0.0f), is(false));
        assertThat(Tile.detectCollision('/', len, 0.0f), is(true));
        assertThat(Tile.detectCollision('/', len, len), is(true));
        assertThat(Tile.detectCollision('/', 0.0f, len), is(true));
    }

    @Test
    public void detectCollisionCornerSW_Successful() throws Exception {
        assertThat(Tile.detectCollision('|', len/2 - 1.0f, len/2), is(true));
        assertThat(Tile.detectCollision('|', len/2 + 1.0f, len/2), is(false));
        assertThat(Tile.detectCollision('|', 0.0f, 1.0f), is(true));
        assertThat(Tile.detectCollision('|', len, 0.0f), is(false));
        assertThat(Tile.detectCollision('|', len - 1.0f, len), is(true));
        assertThat(Tile.detectCollision('|', 0.0f, len), is(true));
    }
}