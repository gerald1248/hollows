package gerald1248.hollows;

import android.app.Activity;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;

public class MainActivity extends Activity {

    //private MediaPlayer mediaPlayer = null;
    private LoopMediaPlayer loopMediaPlayer = null;
    private Panel panel = null;

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

        try {
            panel = new Panel(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(panel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        panel.setRunning(true);

        if (Constants.PLAY_AUDIO) {
            loopMediaPlayer = LoopMediaPlayer.create(MainActivity.this, randomAudioResource());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        panel.setRunning(false);
        if (Constants.PLAY_AUDIO) {
            loopMediaPlayer.stop();
            loopMediaPlayer.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private int randomAudioResource() {
        int[] resources = {
            R.raw.synth_ix2,
            R.raw.synth_iix2,
            R.raw.synth_iiix2,
            R.raw.synth_ivx2
        };

        double range = (double)resources.length - 1.0D;
        int index = (int)Math.round(range * Math.random());
        return resources[index];
    }

    protected MediaPlayer getMediaPlayer() {
        //TODO: consider looping with dual MediaPlayer instances
        return MediaPlayer.create(MainActivity.this, randomAudioResource());
    }
}