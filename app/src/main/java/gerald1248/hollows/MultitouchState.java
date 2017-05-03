package gerald1248.hollows;

/**
 * Wraps state for a single multitouch event sequence
 */

public class MultitouchState extends Object {
    public enum Motion {
        None, Pressed, Thrust, Move
    }

    public Motion state;
    public float x1, x2;
    public int ticks = 0;
}