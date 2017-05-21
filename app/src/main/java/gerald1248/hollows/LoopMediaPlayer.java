package gerald1248.hollows;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Based on LoopMediaPlayer class by Mattia Maestrini
 * with utility functions by code-read
 * see http://stackoverflow.com/questions/26274182/not-able-to-achieve-gapless-audio-looping-so-far-on-android/29883923#29883923
 */

public class LoopMediaPlayer {
    private Context context = null;
    private int resourceId = 0;

    private MediaPlayer current = null;
    private MediaPlayer next = null;

    public static LoopMediaPlayer create(Context context, int resId) {
        return new LoopMediaPlayer(context, resId);
    }

    private LoopMediaPlayer(Context context, int id) {
        this.context = context;
        this.resourceId = id;

        current = MediaPlayer.create(this.context, this.resourceId);
        current.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                current.start();
            }
        });

        createNextMediaPlayer();
    }

    private void createNextMediaPlayer() {
        next = MediaPlayer.create(context, resourceId);
        current.setNextMediaPlayer(next);
        current.setOnCompletionListener(onCompletionListener);
    }

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.release();
            current = next;

            createNextMediaPlayer();
        }
    };

    // utility methods by code-read
    public boolean isPlaying() throws IllegalStateException {
        return current.isPlaying();
    }

    public void setVolume(float leftVolume, float rightVolume) {
        current.setVolume(leftVolume, rightVolume);
    }

    public void start() throws IllegalStateException {
        current.start();
    }

    public void stop() throws IllegalStateException {
        current.stop();
    }

    public void pause() throws IllegalStateException {
        current.pause();
    }

    public void release() {
        current.release();
        next.release();
    }

    public void setResourceId(int id) {
        resourceId = id;
    }
}