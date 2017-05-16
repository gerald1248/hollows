package gerald1248.hollows;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.magnos.impulse.Circle;

import static gerald1248.hollows.R.string.audio_label;
import static gerald1248.hollows.R.string.audio_banner;

/**
 * There's only one screen, so audio controls need an orb of their own
 * Also: the default must be quiet: no FX, no music, nothing
 */

public class AudioOrb extends QualifiedShape implements Orb {
    private Context context = null;

    public AudioOrb(Context context, float r, int x, int y) {
        super(new Circle(r), x, y, 0.0f);
        this.context = context;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float cx = (float)this.x;
        float cy = (float)this.y;
        canvas.drawCircle(cx, cy, shape.radius, paint);

        String label = context.getResources().getString(audio_label);
        TextUtils.draw(canvas, label, 48.0f, cx, cy, Paint.Align.CENTER, Color.BLACK);
    }

    @Override
    public void onFire() {

    }

    @Override
    public void onLand() {

    }

    @Override
    public String getBannerText() {
        return context.getResources().getString(audio_banner);
    }

    @Override
    public String[] getInfoLines() {
        Resources r = context.getResources();
        return new String[]{};
    }
}
