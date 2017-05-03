package gerald1248.hollows;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class StarfieldTest {
    @Test
    public void constructor_Successful() throws Exception {
        Starfield starField = new Starfield();
        Star[] stars = starField.getStars();

        float min = 0.5f;
        float max = 3.5f;

        for (Star star : stars) {
            float r = star.getR();
            assertThat(r >= min && r <= max, is(true));
        }
    }
}