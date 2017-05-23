package gerald1248.hollows;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.util.Locale;

import static android.R.attr.typeface;

public class MainActivity extends Activity {

    private LoopMediaPlayer loopMediaPlayer = null;
    private Panel panel = null;
    private int levelIndex = 0;
    private Typeface typeface = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver receiver = new ScreenReceiver();
        registerReceiver(receiver, filter);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        //level count
        Resources resources = MainActivity.this.getResources();
        Constants.MAX_LEVEL = resources.getStringArray(R.array.levels).length;

        if (savedInstanceState != null) {
            levelIndex = savedInstanceState.getInt("levelIndex", 0);
        }

        //set panel font
        AssetManager am = this.getApplicationContext().getAssets();
        typeface = Typeface.createFromAsset(am, "fonts/PressStart2P.ttf");

        try {
            panel = new Panel(this, levelIndex, typeface);
        } catch (IOException e) {
            e.printStackTrace();
        }


        setContentView(panel);
    }

    @Override
    protected void onResume() {
        panel.setRunning(true);

        loopMediaPlayer = LoopMediaPlayer.create(MainActivity.this, getAudioResource(levelIndex));
        loopMediaPlayer.pause(); //TODO: work out if user specified audio before
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("MyActivity::onPause");
        panel.setRunning(false);
        panel.clearMultitouchState();

        //double lock: onPause and onStop
        if (loopMediaPlayer.isPlaying()) {
            loopMediaPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //double lock: onPause and onStop
        if (loopMediaPlayer.isPlaying()) {
            loopMediaPlayer.pause();
        }
        System.out.println("MyActivity::onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loopMediaPlayer.release();
        panel = null;
    }

    @Override
    public void onRestoreInstanceState(Bundle in) {
        if (panel != null) {
            panel.setLevelIndex(in.getInt("levelIndex"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        if (panel != null) {
            out.putInt("levelIndex", panel.getLevelIndex());
        }
    }

    private int getAudioResource(int levelIndex) {
        int[] resources = {
                R.raw.synth_i,
                R.raw.synth_ii,
                R.raw.synth_iii,
                R.raw.synth_iv,
                R.raw.synth_v
        };

        int i = levelIndex % resources.length;
        return resources[i];
    }

    public void toggleAudio() {
        if (loopMediaPlayer.isPlaying()) {
            loopMediaPlayer.pause();
        } else {
            loopMediaPlayer.start();
        }
    }

    public void setLevelIndex(int i) {
        levelIndex = i;
        if (loopMediaPlayer != null) {
            loopMediaPlayer.setResourceId(getAudioResource(i));
        }
    }
}
