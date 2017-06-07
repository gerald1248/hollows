package gerald1248.hollows;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.util.Log;

/*
 * Receiver req'd for Screen On (to restart the game loop after waking up)
 */

public class ScreenReceiver extends BroadcastReceiver {
    private static final String TAG = ScreenReceiver.class.getSimpleName();

    public enum ScreenBroadcast {
        On, Off, None
    }

    public static ScreenBroadcast state;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            if (Constants.LOG) {
                Log.d(TAG, "onReceive (off)");
            }
            state = ScreenBroadcast.Off;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            if (Constants.LOG) {
                Log.d(TAG, "onReceive (on)");
            }
            state = ScreenBroadcast.On;
            MainActivity mainActivity = (MainActivity) context;
            if (mainActivity != null) {
                mainActivity.recreate();
            }
        } else {
            state = ScreenBroadcast.None;
        }
    }
}
