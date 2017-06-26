package gerald1248.hollows;

import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.util.Log;

/**
 * MainThread manages the central refresh loop
 * fixed at Constants.MAX_FPS
 */

public class MainThread extends Thread {
    private static final String TAG = MainThread.class.getSimpleName();

    public static Canvas canvas;

    private SurfaceHolder surfaceHolder;
    private Panel panel;
    private boolean running;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public MainThread(SurfaceHolder surfaceHolder, Panel panel) {
        super();
        this.surfaceHolder = surfaceHolder;

        this.surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        this.surfaceHolder.setFixedSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        this.panel = panel;
    }

    @Override
    public void run() {
        long startTime, timeMillis, waitTime;
        int frameCount = 0;
        int missed = 0;
        long totalTime = 0;
        long targetTime = 1000 / Constants.MAX_FPS;
        long debt = 0;

        while (running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    panel.update();
                    panel.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            try {
                if (waitTime > 0) {
                    //recover lost time if possible
                    if (waitTime > debt) {
                        waitTime -= debt;
                        debt = 0;
                        sleep(waitTime);
                    } else {
                        debt -= waitTime;
                    }
                } else if (waitTime < 0) {
                    missed++;
                    debt += -waitTime;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == Constants.MAX_FPS) {
                if (Constants.LOG == true) {
                    Log.d(TAG, String.format("Target: %d FPS - actual time: %.2fs - %d frames missed target (%.2f%%)", Constants.MAX_FPS, (float) totalTime/1000000000.0f, missed, ((float) missed/(float) Constants.MAX_FPS) * 100.0f));
                }
                frameCount = 0;
                totalTime = 0;
                missed = 0;
            }
        }
    }
}
