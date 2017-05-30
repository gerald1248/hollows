package gerald1248.hollows;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class StarTest {
    @Test
    public void constructor_Successful() throws Exception {
        for (int i = 0; i < 64; i++) {
            boolean maj = i % 2 != 0;
            float side = Constants.MAX_MAP/2;
            Star star = new Star(maj, side);

            float min = (maj) ? 2.5f : 0.5f;
            float max = (maj) ? 3.5f : 1.5f;
            float r = star.getR();
            assertThat(r >= min && r <= max, is(true));
        }
    }
}