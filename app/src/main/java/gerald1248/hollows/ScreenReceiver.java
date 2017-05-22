package gerald1248.hollows;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

/*
 * Receiver req'd for Screen On (to restart the game loop after waking up)
 */

public class ScreenReceiver extends BroadcastReceiver {

    public enum ScreenBroadcast {
        On, Off, None
    }

    public static ScreenBroadcast state;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            state = ScreenBroadcast.Off;
            System.out.println("ACTION_SCREEN_OFF");
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            state = ScreenBroadcast.On;
            System.out.println("ACTION_SCREEN_ON");
        } else {
            state = ScreenBroadcast.None;
        }
    }
}
