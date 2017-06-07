package gerald1248.hollows;

import android.graphics.Canvas;
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
        this.panel = panel;
    }

    @Override
    public void run() {
        long startTime, timeMillis, waitTime;
        int frameCount = 0;
        long totalTime = 0;
        long targetTime = 1000 / Constants.MAX_FPS;

        while (running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    panel.update();
                    panel.draw(canvas);
                    panel.tick(); //1.0.5
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
                    sleep(waitTime);
                } else if (Constants.LOG) {
                    Log.d(TAG, String.format("run (%d overrun)", waitTime));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == Constants.MAX_FPS) {
                frameCount = 0;
                totalTime = 0;
            }
            //panel.tick();
        }
    }
}
