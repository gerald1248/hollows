package gerald1248.hollows;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.magnos.impulse.Body;
import org.magnos.impulse.Circle;
import org.magnos.impulse.ImpulseMath;
import org.magnos.impulse.ImpulseScene;
import org.magnos.impulse.Vec2;

/**
 * Panel initializes the physics model for player and map
 * It also manages the user's interactions with the screen
 * MotionEvents are handled here, as is the state of each touch interaction
 */

public class Panel extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;
    private Rect r = new Rect();

    private Player player;
    private Point playerPoint;
    private LevelMap levelMap;

    public ImpulseScene impulse;
    public Body body;

    private ConcurrentLinkedQueue<Wave> waves = new ConcurrentLinkedQueue<Wave>();

    private Starfield starfield = new Starfield();

    private ConcurrentHashMap<Integer, MultitouchState> multitouchMap = new ConcurrentHashMap<Integer, MultitouchState>();
    private MultitouchState mts = new MultitouchState();

    public Panel(Context context) throws IOException {
        super(context);

        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        //canvas player
        player = new Player(new Rect(100, 100, 200, 200), 0.0f, Color.rgb(255, 255, 255));
        playerPoint = new Point(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2);
        player.update(playerPoint);


        //2D scene
        impulse = new ImpulseScene(ImpulseMath.DT * Constants.DT_FACTOR, 10);

        // stub test data - just a few simple shapes for now
        levelMap = new LevelMap();
        levelMap.addCircle(150.0f, 5000, 5300);
        levelMap.addCircle(125.0f, 5500, 5450);
        levelMap.addCircle(100.0f, 6000, 5600);
        levelMap.addCircle(75.0f, 6500, 5750);
        levelMap.addRect(1000.0f, 50.0f, 4000, 5500, (float)(Math.PI * 0.2));
        levelMap.addRect(1000.0f, 50.0f, 7000, 6000, (float)(-Math.PI * 0.25));
        levelMap.addRect(1000.0f, 50.0f, 5000, 6500, 0.0f);
        levelMap.initStaticShapes(impulse);


        //impulse player
        body = impulse.add(new Circle(Constants.PLAYER_RADIUS), (int) Constants.MAX_MAP / 2, (int) Constants.MAX_MAP / 2);
        body.setOrient(0.0f);
        body.restitution = 0.2f;
        body.dynamicFriction = 0.2f;
        body.staticFriction = 0.4f;

        setFocusable(true);
    }

    public void setRunning(boolean b) {
        thread.setRunning(b);
        if (b && thread.isInterrupted()) {
            thread.start();
        }
    }

    public void reset() {
    }

    // tick() is called from main loop when ready to draw frame
    // determines state of motion events
    public void tick() {
        // potential race condition, hence ConcurrentHashMap
        for (Map.Entry<Integer, MultitouchState> entry : multitouchMap.entrySet()) {
            Integer key = entry.getKey();
            MultitouchState value = entry.getValue();
            int ticks = value.ticks;
            value.ticks = ticks + 1;
            multitouchMap.put(key, value);

            if ((value.state == MultitouchState.Motion.Pressed || value.state == MultitouchState.Motion.Thrust || value.state == MultitouchState.Motion.Move) && value.ticks > Constants.FRAMES_DELAY) {
                // apply thrust
                value.state = MultitouchState.Motion.Thrust;
                multitouchMap.put(key, value);

                float orient = body.orient;
                float mult = 1250000.0F;
                float c = (float)Math.cos(orient + Math.PI/2);
                float s = (float)Math.sin(orient + Math.PI/2);
                Vec2 v = new Vec2(mult * -c, mult * -s);
                body.applyForce(v);
            }
        }
        impulse.step(); // calc new values

        // force fixed orient
        body.setOrient(player.orient);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(getHolder(), this);

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry == true) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        int pointerIndex = MotionEventCompat.getActionIndex(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() > Constants.MAX_POINTER) {
                    break;
                }
                int idDown = event.getPointerId(pointerIndex);
                mts = new MultitouchState();
                mts.x1 = event.getX(pointerIndex);
                multitouchMap.put(idDown, mts);
                mts.state = MultitouchState.Motion.Pressed;
                multitouchMap.put(idDown, mts);

                break;
            case MotionEvent.ACTION_MOVE:
                int count = event.getPointerCount();
                for (int i = 0; i < count && i < Constants.MAX_POINTER; i++) {
                    int id = event.getPointerId(i);

                    if (!multitouchMap.containsKey(id)) {
                        continue;
                    }

                    mts = multitouchMap.get(id);

                    if (mts == null) {
                        continue;
                    }

                    mts.x2 = event.getX(i); // loop index req'd
                    float delta = mts.x2 - mts.x1;

                    // switch to move even when currently in state Thrust
                    // but, require strong movement
                    // TODO: fine-tune values across devices
                    float minDelta = (mts.state == MultitouchState.Motion.Thrust) ? 10.0F : 2.0F;
                    if (Math.abs(delta) >= minDelta) {
                        player.move(event, delta);
                        body.setOrient(player.orient);

                        mts.state = MultitouchState.Motion.Move;
                        mts.ticks = 0; // reset tick count so switch back to Thrust is possible

                        multitouchMap.put(id, mts);
                        mts.x1 = mts.x2;
                        multitouchMap.put(id, mts);
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                int idUp = event.getPointerId(pointerIndex);
                mts = multitouchMap.get(idUp);
                if (mts == null) {
                    break;
                }
                if (mts.state == MultitouchState.Motion.Pressed) {
                    Wave w = new Wave(body.position.x, body.position.y, player.orient, (float)Math.PI / 4);
                    waves.add(w);

                    if (waves.size() > Constants.MAX_WAVES) {
                        waves.remove();
                    }
                }

                mts.state = MultitouchState.Motion.None;
                multitouchMap.put(idUp, mts);

                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    public void update() {
        // keep world circular for now, but leave border
        Vec2 v = body.position;
        float sideWithBorder = Constants.MAX_MAP + Constants.BORDER_MAP;

        if (v.x < -Constants.BORDER_MAP) {
            v.x = sideWithBorder;
        } else if (v.x > sideWithBorder) {
            v.x = -Constants.BORDER_MAP;
        }
        if (v.y < -Constants.BORDER_MAP) {
            v.y = sideWithBorder;
        } else if (v.y > sideWithBorder) {
            v.y = -Constants.BORDER_MAP;
        }

        body.position.x = v.x;
        body.position.y = v.y;

        //collision logic goes here
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) {
            return;
        }
        super.draw(canvas);

        canvas.drawColor(Color.BLACK);

        starfield.draw(canvas);
        levelMap.draw(canvas, -body.position.x, -body.position.y, Color.WHITE);
        player.draw(canvas);

        Iterator<Wave> it = waves.iterator();
        while (it.hasNext()) {
            Wave w = it.next();
            if (w.done()) {
                it.remove();
            } else {
                w.draw(canvas);
            }
        }
    }

    private void drawCenterText(Canvas canvas, Paint paint, String text) {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }
}
