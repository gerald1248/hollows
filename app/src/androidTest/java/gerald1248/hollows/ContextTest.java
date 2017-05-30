package gerald1248.hollows;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ContextTest {
    @Test
    public void verifyPackageName() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertThat("gerald1248.hollows", is(appContext.getPackageName()));
    }

    @Test
    public void verifyCustomFont() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        AssetManager am = appContext.getAssets();
        Typeface typeface = Typeface.createFromAsset(am, "fonts/PressStart2P.ttf");
        assertThat(typeface.getStyle(), is(Typeface.NORMAL));
    }

    //this test only asks how many levels are available
    //see LevelsTest for level validation
    @Test
    public void verifyLevels() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Resources resources = appContext.getResources();
        String[] levels = resources.getStringArray(R.array.levels);
        assertTrue(levels.length >= 4);
    }

    //TODO tests for resources
}
