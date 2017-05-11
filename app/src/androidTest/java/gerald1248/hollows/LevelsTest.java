package gerald1248.hollows;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Ensures that all levels given are valid (dimensions, required tiles are present, etc.)
 */

@RunWith(AndroidJUnit4.class)
public class LevelsTest {
    @Test
    public void useAppContext() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Resources resources = appContext.getResources();
        String[] levels = resources.getStringArray(R.array.levels);
        for (String level : levels) {
            level = level.trim();
            requiredTiles(level);

            String[] lines = level.split("\\s+");
            dimensions(lines);
            validTiles(lines);
        }

    }

    public void requiredTiles(String level) {
        boolean b = level.contains("e");
        assertThat(b, is(true));
    }

    public void dimensions(String[] lines) {
        assertThat(lines.length, is(Constants.CHARMAP_LENGTH));
        for (String line : lines) {
            assertThat(line.length(), is(Constants.CHARMAP_LENGTH));
        }
    }

    public void validTiles(String[] lines) {
        for (String line : lines) {
            Pattern p = Pattern.compile("^[\\.|/+^v\u00b4`se1-9]+$");
            Matcher m = p.matcher(line);
            boolean b = m.matches();
            assertThat(b, is(true));
        }
    }
}