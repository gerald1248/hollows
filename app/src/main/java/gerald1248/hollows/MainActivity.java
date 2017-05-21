package gerald1248.hollows;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;

public class MainActivity extends Activity {

    private LoopMediaPlayer loopMediaPlayer = null;
    private Panel panel = null;
    private int levelIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

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

        try {
            panel = new Panel(this, levelIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(panel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        panel.setRunning(true);

        loopMediaPlayer = LoopMediaPlayer.create(MainActivity.this, getAudioResource(levelIndex));
        loopMediaPlayer.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        panel.setRunning(false);
        loopMediaPlayer.pause();
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
            R.raw.synth_ix2,
            R.raw.synth_iix2,
            R.raw.synth_iiix2,
            R.raw.synth_ivx2
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
