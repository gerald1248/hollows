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

public class Panel extends SurfaceView implements SurfaceHolder.Callback, GameObject {
    private MainThread thread;

    private Player player;
    private LevelMap levelMap;

    public ImpulseScene impulse;
    public Body body;

    private ConcurrentLinkedQueue<Wave> waves = new ConcurrentLinkedQueue<Wave>();
    private ConcurrentLinkedQueue<Laser> lasers = new ConcurrentLinkedQueue<Laser>();

    private Starfield starfield = new Starfield();

    private ConcurrentHashMap<Integer, MultitouchState> multitouchMap = new ConcurrentHashMap<Integer, MultitouchState>();
    private MultitouchState mts = new MultitouchState();

    private int detonateFramesRemaining = 0;
    private int initialTargetsRemaining = 100;
    private int targetsRemaining = initialTargetsRemaining;

    private int levelIndex = 0;

    private Point startPoint = null; // not yet used
    private Point endPoint = null;

    private float initialBodyMass = -1.0f;

    private String bannerText;
    private String[] infoLines;

    private Context context;

    private HomingDevice homingDevice = null;

    public Panel(Context context) throws IOException {
        super(context);

        this.context = context;

        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        //2D scene
        impulse = new ImpulseScene(ImpulseMath.DT * Constants.DT_FACTOR, 10);

        levelMap = new LevelMap(context);
        levelMap.initStaticShapes(impulse);

        startPoint = levelMap.getStartPoint();
        endPoint = levelMap.getEndPoint();

        homingDevice = new HomingDevice(Constants.TILE_LENGTH, Constants.SCREEN_HEIGHT - Constants.TILE_LENGTH, Constants.TILE_LENGTH/2);

        initPlayer(); //canvas
        initBody(); //impulse

        setFocusable(true);
    }

    public void initPlayer() {
        player = new Player(new Rect(100, 100, 200, 200), 0.0f, Color.rgb(255, 255, 255));

        //screen position never changes
        Point playerPoint = new Point(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2);
        player.update(playerPoint);
    }

    public void initBody() {
        body = impulse.add(new Circle(Constants.PLAYER_RADIUS), (int) Constants.MAX_MAP / 2, (int) Constants.MAX_MAP / 2);
        body.setOrient((float)-Math.PI/2);
        initBodyPhysics(body);

        if (initialBodyMass < -0.0f) {
            initialBodyMass = body.mass;
        } else {
            body.mass = initialBodyMass;
        }

        //actual position matches start point
        body.position.x = startPoint.x;
        body.position.y = startPoint.y;
    }

    public void initBodyPhysics(Body b) {
        b.restitution = 0.2f;
        b.dynamicFriction = 0.2f;
        b.staticFriction = 0.4f;
    }

    public void setRunning(boolean b) {
        thread.setRunning(b);
        if (b && thread.isInterrupted()) {
            thread.start();
        }
    }

    public void reset(boolean advance) {
        impulse.clear();
        if (advance) {
            //which level next?
            levelIndex++;
            if (levelIndex >= Constants.MAX_LEVEL) {
                //TODO: success animation
                levelIndex = 0;
            }
        }
        player.explode(false);
        player.escape(false);
        levelMap.setLevelIndex(levelIndex);
        levelMap.initStaticShapes(impulse);
        startPoint = levelMap.getStartPoint();
        endPoint = levelMap.getEndPoint();
        initPlayer();
        initBody();
    }

    public void detonate() {
        detonateFramesRemaining = Constants.FRAMES_DETONATE;
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
                // visual indicator if thrust not already applied
                if (value.state != MultitouchState.Motion.Thrust) {
                    Wave w = new Wave(body.position.x, body.position.y, player.orient - (float) Math.PI, (float) Math.PI / 8, 4);
                    waves.add(w);

                    if (waves.size() > Constants.MAX_PROJECTILES) {
                        waves.remove();
                    }
                }

                // apply thrust
                value.state = MultitouchState.Motion.Thrust;
                multitouchMap.put(key, value);

                float orient = body.orient;
                float mult = 1500000.0F;
                float c = (float) Math.cos(orient);
                float s = (float) Math.sin(orient);

                Vec2 v = new Vec2(mult * c, mult * s);
                body.applyForce(v);
            }
        }
        impulse.step(); // calc new values

        // force fixed orient
        body.setOrient(player.orient);

        //handle game over
        if (player.exploded()) {
            targetsRemaining = initialTargetsRemaining;
            reset(false);
        } else if (player.escaped()) {
            //TODO: advance to next level
            targetsRemaining = initialTargetsRemaining;
            reset(true);
        }
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
                    Laser l = new Laser(body.position.x, body.position.y, player.orient, 20);
                    lasers.add(l);

                    if (lasers.size() > Constants.MAX_PROJECTILES) {
                        lasers.remove();
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
        Vec2 v = body.position;

        if (v.x < 0.0f || v.x > Constants.MAX_MAP || v.y < 0.0f || v.y > Constants.MAX_MAP) {

            if (targetsRemaining > 0) {
                player.explode(true);
                targetsRemaining = initialTargetsRemaining;
                detonate();
            } else {
                player.escape(true);
                detonate();
            }
        }

        //check for collisions
        if (levelMap.detectCollision(v.x, v.y, Constants.PLAYER_RADIUS, player.orient)) {
            body.setStatic();
            player.explode(true);
            targetsRemaining = 100;
            detonate();
        }

        //check if laser has hit a static object; if it has, laser disappears
        //also check if it's hit a 2D body

        //iterate over lasers
        Iterator<Laser> laserIt = lasers.iterator();

        while (laserIt.hasNext()) {
            // level map
            Laser l = laserIt.next();

            if (l.isDone()) {
                laserIt.remove();
                continue;
            }

            float x = l.x;
            float y = l.y;
            float r = l.r;
            float orient = l.orient;
            if (levelMap.detectCollision(x, y, r, orient)) {
                laserIt.remove();
            }

            // 2D bodies
            QualifiedShape qs = levelMap.detectShapeCollision(x, y, r);
            if (qs != null) {
                laserIt.remove();
                Wave wave = new Wave(0.0f, 0.0f, 0.0f, (float) (2 * Math.PI), 10);
                wave.setOffset(body.position.x - qs.x, body.position.y - qs.y);
                waves.add(wave);
                detonate();

                Orb o = (Orb)qs;
                if (o instanceof AudioOrb) {
                    MainActivity mainActivity = (MainActivity)context;
                    mainActivity.toggleAudio();
                }
            }
        }

        if (targetsRemaining > 0) {
            //TODO: move proximity check to specialized Orb class
            //finally, check if near endPoint
            float x1 = v.x, y1 = v.y, x2 = endPoint.x, y2 = endPoint.y;
            float d = (float) Math.hypot((double) x2 - (double) x1, (double) y2 - (double) y1);
            if (d < 3.0f * Constants.PLAYER_RADIUS) {
                targetsRemaining--;
                body.mass = body.mass * 1.05f;
            }
            if (targetsRemaining == 99 || (targetsRemaining % 10) == 0) {
                //TODO: play bell sound?
            }
        }

        //TODO: add charMap test that ensures space between orbs
        //add 0.5r tolerance to avoid flicker
        Orb o = (Orb)levelMap.detectShapeCollision(v.x, v.y, Constants.PLAYER_RADIUS * 1.5f);
        if (o == null) {
            bannerText = "";
            infoLines = new String[]{};
        } else {
            bannerText = o.getBannerText();
            infoLines = o.getInfoLines();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) {
            return;
        }
        super.draw(canvas);

        int bgComponent = 0;
        if (detonateFramesRemaining > 0) {
            bgComponent = Math.round(128 / Constants.FRAMES_DETONATE) * detonateFramesRemaining;
            detonateFramesRemaining--;
        }

        canvas.drawColor(Color.rgb(bgComponent, bgComponent, bgComponent));

        starfield.draw(canvas);
        levelMap.draw(canvas, -body.position.x, -body.position.y, Color.WHITE);
        player.draw(canvas);

        Iterator<Wave> it = waves.iterator();
        while (it.hasNext()) {
            Wave w = it.next();
            if (w.isDone()) {
                it.remove();
            } else {
                w.draw(canvas);
            }
        }

        Iterator<Laser> laserIt = lasers.iterator();
        while (laserIt.hasNext()) {
            Laser l = laserIt.next();
            if (l.isDone()) {
                laserIt.remove();
            } else {
                l.draw(canvas);
            }
        }

        // display targets remaining
        updateInfo(canvas);

        // update homing device - angle to endPoint
        float angle = (float)Math.atan2(body.position.x - endPoint.x, -(body.position.y - endPoint.y)) + (float)Math.PI/2;

        homingDevice.update((targetsRemaining == 0) ? (float)-Math.PI/2 : angle);
        homingDevice.draw(canvas);
    }

    void updateInfo(Canvas canvas) {
        canvas.save();

        String s = String.format("%03d", targetsRemaining);
        int color = (targetsRemaining < 1) ? Color.GREEN : Color.GRAY;
        TextUtils.draw(canvas, s, 48.0f, Constants.SCREEN_WIDTH - 12.0f, 48.0f, Paint.Align.RIGHT, color);

        s = String.format("Level %03d", levelIndex + 1);
        color = Color.GRAY;
        TextUtils.draw(canvas, s, 48.0f, 12.0f, 48.0f, Paint.Align.LEFT, color);

        TextUtils.draw(canvas, bannerText, 96.0f, Constants.SCREEN_WIDTH/2, Constants.SCREEN_HEIGHT * 0.25f, Paint.Align.CENTER, color);

        float yOffset = 0.0f;
        for (String line : infoLines) {
            TextUtils.draw(canvas, line, 48.0f, Constants.SCREEN_WIDTH/2, Constants.SCREEN_HEIGHT * 0.75f + yOffset, Paint.Align.CENTER, color);
            yOffset += 56.0f;
        }

        canvas.restore();
    }
}
