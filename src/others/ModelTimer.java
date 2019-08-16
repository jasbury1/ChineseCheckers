
package others;

import javax.swing.Timer;
import java.awt.event.ActionEvent;


/**
 * This utiity class provides a timer for use by a game model in
 * a pure event-driven program.  Each instance of your model should 
 * create at most one ModelTimer.
 */
public class ModelTimer {

    private final static long SECONDS_TO_NANOS = 1_000_000_000L;

    private long startTimeNanos;
    private final Timer eventTimer;
    private Runnable nextEvent = null;
    private boolean started = false;

    public ModelTimer() {
        eventTimer = new Timer(1_000_000, (ActionEvent e) -> { runNextEvent(); });
        eventTimer.setRepeats(false);
    }

    /**
     * Start the ModelTimer
     */
    public void start() {
        assert !started;
        started = true;
        startTimeNanos = System.nanoTime();
    }

    /**
     * Get the elapsed time since start was called, in seconds. 
     */
    public double getTimeSinceStart() {
        long nanos = System.nanoTime() - startTimeNanos;
        return ((double) nanos) / SECONDS_TO_NANOS;
    }

    /**
     * Cause the given runnable to run after the given number of seconds has
     * elapsed.  Calling this method cancels any pending events.  The event will
     * execute on the AWT event thread (the same thread as Component paint() calls
     * and mouse clicks).
     */
    public void runAfter(double seconds, Runnable event) {
        eventTimer.stop();
        nextEvent = event;
        if (seconds <= 0.0)  {
            seconds = 0.0;
        }
        int delay = (int) (seconds * 1000 + 5);         // Overshoot by a tiny bit
        eventTimer.setInitialDelay(delay);
        eventTimer.start();
    }

    private void runNextEvent() {
        Runnable r = nextEvent;
        nextEvent = null;
        r.run();
    }

    /**
     * Finish with this timer.
     */
    public void finish() {
        eventTimer.stop();
    }

}

