package gerald1248.hollows;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;

import static gerald1248.hollows.MainThread.canvas;

public class MainActivity extends Activity {

    private LoopMediaPlayer loopMediaPlayer = null;
    private Panel panel = null;
    private int levelIndex = 0;
    private int highestLevelIndex = 0;
    private int nonRedshiftHighestLevelIndex = 0;
    private boolean playAudio = false;
    private boolean redshift = false;
    private Typeface typeface = null;
    BroadcastReceiver receiver = null;
    private int masterColor = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //filters
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        receiver = new ScreenReceiver();
        registerReceiver(receiver, filter);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        //level count
        Resources resources = MainActivity.this.getResources();
        Constants.MAX_LEVEL = resources.getStringArray(R.array.levels).length;

        //preferences
        readPreferences();

        //set panel font
        AssetManager am = this.getApplicationContext().getAssets();
        typeface = Typeface.createFromAsset(am, "fonts/PressStart2P.ttf");

        //media player
        loopMediaPlayer = LoopMediaPlayer.create(MainActivity.this, getAudioResource(levelIndex));
        loopMediaPlayer.pause();

        try {
            panel = new Panel(this, levelIndex, typeface);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView(panel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        readPreferences();

        if (playAudio == true) {
            loopMediaPlayer.start();
        }

        panel.showPauseScreen();

    }


    @Override
    protected void onPause() {
        super.onPause();

        writePreferences();

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loopMediaPlayer.release();
        panel = null;
        unregisterReceiver(receiver);
    }

    private void readPreferences() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if (preferences == null) {
            return;
        }
        levelIndex = preferences.getInt("levelIndex", 0);
        highestLevelIndex = preferences.getInt("highestLevelIndex", 0);
        nonRedshiftHighestLevelIndex = preferences.getInt("nonRedshiftHighestLevelIndex", 0);
        redshift = preferences.getBoolean("redshift", false);
        playAudio = preferences.getBoolean("playAudio", false);

        if (redshift) {
            masterColor = Color.RED;
        }
    }

    private void writePreferences() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("levelIndex", levelIndex);
        editor.putInt("highestLevelIndex", highestLevelIndex);
        editor.putInt("nonRedshiftHighestLevelIndex", highestLevelIndex);
        editor.putBoolean("redshift", redshift);
        editor.putBoolean("playAudio", playAudio);
        editor.commit();
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
            playAudio = false;
        } else {
            loopMediaPlayer.start();
            playAudio = true;
        }
    }

    public void toggleRedshift() {
        redshift = !redshift;

        masterColor = (redshift) ? Color.RED : Color.WHITE;

        if (redshift) {
            nonRedshiftHighestLevelIndex = highestLevelIndex;
            highestLevelIndex = 9999;
        } else {
            highestLevelIndex = nonRedshiftHighestLevelIndex;
        }
    }

    public void setLevelIndex(int i) {
        levelIndex = i;
        if (i > highestLevelIndex) {
            highestLevelIndex = i;
        }
        if (loopMediaPlayer != null) {
            loopMediaPlayer.setResourceId(getAudioResource(i));
        }
    }

    public int getHighestLevelIndex() {
        return highestLevelIndex;
    }

    public boolean getPlayAudio() {
        return playAudio;
    }

    public boolean getRedshift() {
        return redshift;
    }

    public int getMasterColor() {
        return masterColor;
    }
}
