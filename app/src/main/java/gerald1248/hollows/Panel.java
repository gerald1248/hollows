package gerald1248.hollows;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

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
    public ImpulseScene impulse = null;
    public Body body = null;

    public enum PanelState {
        Paused, Running
    }

    private MainThread thread = null;
    private Player player = null;
    private LevelMap levelMap = null;
    private Typeface typeface = null;
    private Point startPoint = null;
    private Point endPoint = null;
    private Context context = null;
    private HomingDevice homingDevice = null;

    private LinkedList<Wave> waves = new LinkedList<Wave>();
    private LinkedList<Laser> lasers = new LinkedList<Laser>();

    private LinkedList<Laser> enemyLasers = new LinkedList<Laser>();

    private Starfield starfield = new Starfield();
    private DangerZone dangerZone = new DangerZone();

    private HashMap<Integer, MultitouchState> multitouchMap = new HashMap<Integer, MultitouchState>();
    private MultitouchState mts = new MultitouchState();

    private int detonateFramesRemaining = 0;
    private int initialTargetsRemaining = 100;
    private int targetsRemaining = initialTargetsRemaining;

    private int levelIndex = 0;
    private int frames = 0;
    private int alertFrames = 0;

    private float initialBodyMass = -1.0f;

    private float fontSizeLargeAdjusted = Constants.FONT_SIZE_LARGE;
    private float fontSizeMediumAdjusted = Constants.FONT_SIZE_MEDIUM;

    private Rect rectScreen;

    private String bannerText;
    private String alertText;
    private String[] infoLines;

    private PanelState state;

    public Panel(Context context, int levelIndex, Typeface typeface) throws IOException, InstantiationException, IllegalAccessException {
        super(context);

        this.context = context;
        this.levelIndex = levelIndex;
        this.typeface = typeface;

        rectScreen = new Rect(0, 0, 0, 0);

        state = PanelState.Running; //this is the default - pause is the exception

        getHolder().addCallback(this);

        //2D scene
        impulse = new ImpulseScene(ImpulseMath.DT * Constants.DT_FACTOR, Constants.ITERATIONS);

        levelMap = new LevelMap(context, typeface, levelIndex);
        levelMap.initStaticShapes(impulse);

        startPoint = levelMap.getStartPoint();
        endPoint = levelMap.getEndPoint();

        homingDevice = new HomingDevice(Constants.TILE_LENGTH, Constants.SCREEN_HEIGHT - Constants.TILE_LENGTH, Constants.TILE_LENGTH / 2);

        adjustFontSizes();

        initPlayer(); //canvas
        initBody(); //impulse

        setFocusable(true);
    }

    public void initPlayer() {
        player = new Player(new Rect(100, 100, 200, 200), 0.0f);

        //screen position never changes
        Point playerPoint = new Point(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2);
        player.update(playerPoint);
    }

    public void initBody() {
        body = impulse.add(new Circle(Constants.PLAYER_RADIUS), (int) Constants.MAX_MAP / 2, (int) Constants.MAX_MAP / 2);
        body.setOrient((float) -Math.PI / 2);
        initBodyPhysics(body);

        //currently mass does not change as the physics don't change
        //in a convincing way
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
        //safety catch
        if (thread == null) {
            //if !b, no need to allocate thread
            //TODO: if b, consider allocating new thread
            return;
        }
        thread.setRunning(b);
        if (b == true && (thread.isInterrupted())) {
            thread.start();
        }
    }

    public void clearMultitouchState() {
        multitouchMap.clear();
    }

    public void reset(boolean advance) {
        boolean towersDestroyed = levelMap.getTowers().isEmpty();
        impulse.clear();
        lasers.clear();
        enemyLasers.clear();
        levelMap.clearTowers();
        clearMultitouchState();
        targetsRemaining = initialTargetsRemaining;

        if (advance) {
            MainActivity mainActivity = (MainActivity) context;
            //which level next?
            levelIndex++;
            if (levelIndex >= Constants.MAX_LEVEL) {
                //call mainActivity so highestLevelIndex is set
                mainActivity.setLevelIndex(levelIndex);

                //mark the game as complete
                setCompletionAlert();

                //then start over
                levelIndex = 0;
            } else if (towersDestroyed) {
                setPerfectRunAlert();
            }
            mainActivity.setLevelIndex(levelIndex);
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
        lasers.clear();
        enemyLasers.clear();
        detonateFramesRemaining = Constants.FRAMES_DETONATE;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(holder, this);

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
        //handle pause screen
        if (state == PanelState.Paused) {
            bannerText = "";
            state = PanelState.Running;

            if (thread != null) {
                thread.setRunning(true);
            }

            return true;
        }

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
                multitouchMap.put(idDown, mts); //TODO: remove?
                mts.state = MultitouchState.Motion.Pressed;
                multitouchMap.put(idDown, mts);

                //System.out.printf("DOWN index=%d ID=%d\n", pointerIndex, idDown);

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
                    // but require strong movement
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
                    //System.out.printf("MOVE index=%d ID=%d\n", pointerIndex, id);
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

                    Laser l = new Laser(body.position.x, body.position.y, player.orient, 20, body);
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
        if (state == PanelState.Paused) {
            return;
        }

        // first manage touch event state
        // formerly handled separately in tick()
        frames++;

        for (Map.Entry<Integer, MultitouchState> entry : multitouchMap.entrySet()) {
            Integer key = entry.getKey();
            MultitouchState value = entry.getValue();
            int ticks = value.ticks;
            value.ticks = ticks + 1;
            multitouchMap.put(key, value);

            if ((value.state == MultitouchState.Motion.Pressed || value.state == MultitouchState.Motion.Thrust || value.state == MultitouchState.Motion.Move) && value.ticks > Constants.FRAMES_DELAY) {
                // visual indicator if thrust not already applied
                if (value.state != MultitouchState.Motion.Thrust) {
                    Wave w = new Wave(body.position.x, body.position.y, player.orient - (float) Math.PI, (float) Math.PI / 8, 3);
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

        float x1 = body.position.x;
        float y1 = body.position.y;
        if (frames % Constants.TOWER_INTERVAL_FRAMES == 0) {
            for (QualifiedShape qs : levelMap.getTowers()) {
                Tower t = (Tower) qs;

                //first check player is within detection field
                //currently only N and S orientations are used
                float x2 = t.x;
                float y2 = t.y;
                if (t.orient < 0.0f) {
                    if (y2 < y1) {
                        continue;
                    }
                } else {
                    if (y2 > y1) {
                        continue;
                    }
                }

                //1.0.5
                if (Collision.circleCircle(x1, x2, y1, y2, Constants.TOWER_RANGE) == false) {
                    continue;
                }

                float angle = (float) Math.atan2(x1 - x2, -(y1 - y2));
                angle -= (float) Math.PI / 2;
                Laser l = new Laser(x2, y2, angle, 30, body);
                l.setVelocityFactor(0.5f);
                enemyLasers.add(l);
            }
        }

        // now deal with collisions etc.

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
            targetsRemaining = initialTargetsRemaining;
            detonate();
        }

        //check if laser has hit a 2D body
        //also check if it's hit a static object; if it has, laser disappears

        //iterate over the player's laser bolts
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

            //important: need to check for 2D collisions first
            QualifiedShape qs = levelMap.detectShapeCollision(x, y, r);

            if (qs == null) {
                //try half interval just in case, and only for player's own lasers
                //so step (and thus velocity) can be high without loss of precision
                // TODO: is this necessary? disabled for now
                // qs = levelMap.detectShapeCollision(l.midX, l.midY, r);
            }

            if (qs != null) {
                laserIt.remove();

                Wave wave = new Wave(qs.x, qs.y, 0.0f, (float) (2 * Math.PI), 6);
                wave.setObserver(body);
                waves.add(wave);
                detonate();

                Orb o = (Orb) qs;
                if (o instanceof Tower) {
                    levelMap.removeTower((Tower) o);
                    continue;
                } else if (o instanceof AudioOrb) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.toggleAudio();
                    setAudioAlert(mainActivity.getPlayAudio());
                    continue;
                } else if (o instanceof NextLevelOrb) {
                    MainActivity mainActivity = (MainActivity) context;
                    int highestLevelIndex = mainActivity.getHighestLevelIndex();
                    if (highestLevelIndex > levelIndex) {
                        player.escape(true);
                        waves.clear();
                        reset(true);
                    } else {
                        setUnlockAlert();
                    }
                    continue;
                } else if (o instanceof PreviousLevelOrb) {
                    if (levelIndex > 0) {
                        setLevelIndex(levelIndex - 1);
                        player.escape(true);
                        waves.clear();
                        reset(false);
                    } else {
                        setUnlockAlert();
                    }
                    continue;
                } else if (o instanceof RedshiftOrb) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.toggleRedshift();
                    setRedshiftAlert(mainActivity.getRedshift());
                    continue;
                }
            }

            //finally check if laser has hit solid matter
            if (levelMap.detectCollision(x, y, r, orient)) {
                laserIt.remove();
                Wave wave = new Wave(x, y, orient, 2.0f * (float) Math.PI, 3);
                wave.setObserver(body);
                wave.setVelocityFactor(0.5f);
                waves.add(wave);
            }
        }

        //iterate over enemy lasers
        laserIt = enemyLasers.iterator();
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
            float dFromTower = l.d;
            float orient = l.orient;

            //2D bodies - NB: tower laser starts from shape, so set minimum radius
            if (dFromTower > Constants.TILE_LENGTH * 2) {
                if (levelMap.detectCollision(x, y, r, orient)) {
                    laserIt.remove();
                    continue;
                }

                //don't check for collisions with 2D shapes
            }

            //proximity to player
            float playerX = body.position.x;
            float playerY = body.position.y;
            if (Collision.circleCircle(playerX, playerY, x, y, r, Constants.PLAYER_RADIUS)) {
                body.setStatic();
                player.explode(true);
                targetsRemaining = initialTargetsRemaining;
                detonate();
            }
        }

        if (targetsRemaining > 0) {
            //TODO: move proximity check to specialized Orb class
            //finally, check if near endPoint
            // x1 and y1 are known
            float x2 = endPoint.x, y2 = endPoint.y;

            if (Collision.circleCircle(x1, y1, x2, y2, Constants.ORB_PROXIMITY_FACTOR * Constants.PLAYER_RADIUS)) {
                targetsRemaining -= 2;
            }

            if (targetsRemaining == 0) {
                setReadyAlert();
            }
        }

        //add 0.5r tolerance to avoid flicker
        Orb o = (Orb) levelMap.detectShapeCollision(v.x, v.y, Constants.PLAYER_RADIUS * 1.5f);
        if (state == PanelState.Paused) {
            // do nothing; bannerText has been set
        } else if (o == null && state != PanelState.Paused) {
            bannerText = "";
            infoLines = new String[]{};
        } else {
            //banner display
            bannerText = o.getBannerText();
            infoLines = o.getInfoLines();

            //pulse
            if (frames % Constants.PULSE_INTERVAL_FRAMES == 0 && (o instanceof BaseOrb || o instanceof TitleOrb || o instanceof AudioOrb || o instanceof RedshiftOrb)) {
                QualifiedShape qs = (QualifiedShape) o;
                Wave w = new Wave(qs.x, qs.y, 0.0f, 2.0f * (float) Math.PI, 7);
                w.setObserver(body);
                w.setVelocityFactor(0.5f);
                waves.add(w);
            }
        }

    }

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

        MainActivity mainActivity = (MainActivity) context;
        int masterColor = mainActivity.getMasterColor();

        if (targetsRemaining > 0) {
            dangerZone.draw(canvas, body.position.x, body.position.y, masterColor);
        }
        starfield.draw(canvas, body.position.x, body.position.y, masterColor);
        levelMap.draw(canvas, body.position.x, body.position.y, masterColor);

        player.draw(canvas, masterColor);

        // display targets remaining
        updateInfo(canvas);

        // update homing device - angle to endPoint
        float angle = (float) Math.atan2(body.position.x - endPoint.x, -(body.position.y - endPoint.y)) + (float) Math.PI / 2;
        homingDevice.update((targetsRemaining == 0) ? (float) -Math.PI / 2 : angle);
        homingDevice.draw(canvas, masterColor);

        //don't animate lasers etc. in pause mode, so exit here if the panel has been paused
        if (state == PanelState.Paused) {
            return;
        }

        int x = (int) body.position.x;
        int y = (int) body.position.y;
        int w2 = Constants.SCREEN_WIDTH/2;
        int h2 = Constants.SCREEN_HEIGHT/2;

        rectScreen.set(x - w2, y - h2, x + w2, y + h2);

        Iterator<Wave> it = waves.iterator();
        while (it.hasNext()) {
            Wave w = it.next();
            if (w.isDone()) {
                it.remove();
            } else {
                //TODO: reinstate Collision.circleRect((int) w.cx, (int) w.cy, (int) w.r, rectScreen));
                w.draw(canvas, masterColor, true);
            }
        }

        Iterator<Laser> laserIt = lasers.iterator();
        while (laserIt.hasNext()) {
            Laser l = laserIt.next();
            if (l.isDone()) {
                laserIt.remove();
            } else {
                //TODO: reinstate Collision.circleRect((int) l.cx, (int) l.cy, (int) l.r, rectScreen));
                l.draw(canvas, masterColor, true);
            }
        }

        laserIt = enemyLasers.iterator();
        while (laserIt.hasNext()) {
            Laser l = laserIt.next();
            if (l.isDone()) {
                laserIt.remove();
            } else {
                //TODO: reinstate Collision.circleRect((int) l.cx, (int) l.cy, (int) l.r, rectScreen));
                l.draw(canvas, masterColor, true);
            }
        }
    }

    void updateInfo(Canvas canvas) {
        canvas.save();
        Resources r = context.getResources();
        int color = Color.GRAY;

        String s = String.format(r.getString(R.string.rescue_format), 100 - targetsRemaining);
        TextUtils.draw(canvas, s, fontSizeMediumAdjusted, Constants.SCREEN_WIDTH - 12.0f, fontSizeMediumAdjusted + 2.0f, Paint.Align.RIGHT, color, typeface, false);

        s = String.format(r.getString(R.string.level_format), levelIndex + 1);
        TextUtils.draw(canvas, s, fontSizeMediumAdjusted, 12.0f, fontSizeMediumAdjusted + 2.0f, Paint.Align.LEFT, color, typeface, false);

        TextUtils.draw(canvas, bannerText, fontSizeLargeAdjusted, Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT * 0.25f, Paint.Align.CENTER, color, typeface, false);

        float yOffset = 0.0f;
        for (String line : infoLines) {
            TextUtils.draw(canvas, line, fontSizeMediumAdjusted, Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT * 0.7f + yOffset, Paint.Align.CENTER, color, typeface, false);
            yOffset += fontSizeMediumAdjusted * 1.66f; // was: Constants.FONT_SIZE_LARGE and so just below *= 2.0f
        }

        if (alertFrames > 0) {
            TextUtils.draw(canvas, alertText, fontSizeLargeAdjusted, Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, Paint.Align.CENTER, color, typeface, false);
            alertFrames--;
        }

        canvas.restore();
    }

    public void setLevelIndex(int i) {
        levelIndex = i;
        MainActivity activity = (MainActivity) context;
        activity.setLevelIndex(i);
    }

    public void showPauseScreen() {
        //don't show pause screen at startup
        if (thread == null || MainThread.canvas == null) {
            return;
        }
        thread.setRunning(true);
        state = PanelState.Paused;

        Resources r = context.getResources();
        bannerText = r.getString(R.string.paused_banner);
    }

    private void setReadyAlert() {
        Resources r = context.getResources();
        setAlert(r.getString(R.string.ready_alert));
    }

    private void setUnlockAlert() {
        Resources r = context.getResources();
        setAlert(r.getString(R.string.unlock_alert));
    }

    private void setPerfectRunAlert() {
        Resources r = context.getResources();
        setAlert(r.getString(R.string.perfect_run_alert));
    }

    private void setCompletionAlert() {
        Resources r = context.getResources();
        setAlert(r.getString(R.string.completion_alert));
    }

    private void setAudioAlert(boolean b) {
        Resources r = context.getResources();
        setAlert(r.getString((b) ? R.string.audio_on : R.string.audio_off));
    }

    private void setRedshiftAlert(boolean b) {
        Resources r = context.getResources();
        setAlert(r.getString((b) ? R.string.redshift_on : R.string.redshift_off));
    }

    private void setAlert(String s) {
        alertText = s;
        alertFrames = Constants.ALERT_FRAMES;
    }

    private void adjustFontSizes() {
        float w = (float) Constants.SCREEN_WIDTH;

        // large font size
        Resources r = context.getResources();
        Paint p1 = new Paint();
        p1.setTextSize(Constants.FONT_SIZE_LARGE);
        p1.setTypeface(typeface);
        float w1 = p1.measureText(r.getString(R.string.completion_alert));

        if (w1 > w) { // too large
            fontSizeLargeAdjusted = Math.round((double) (Constants.FONT_SIZE_LARGE * (w / w1)));
        } else if (w / w1 > 2.0f) { // comparatively small
            fontSizeLargeAdjusted *= 1.5f;
        }

        Paint p2 = new Paint();
        p2.setTextSize(Constants.FONT_SIZE_MEDIUM);
        p2.setTypeface(typeface);
        float w2 = p2.measureText(r.getString(R.string.info_line1));

        if (w2 > w) { // too large
            fontSizeMediumAdjusted = Math.round((double) (Constants.FONT_SIZE_MEDIUM * (w / w1)));
        }
    }
}
