
package others.superkind;

/**
 * This class will simulate another player who is playing Badtz Maru.
 * Whenever one or more Rude Dudes are visible, it will set one of them to
 * Badz Maru, after a delay between 10% and 100% of the maximum time passed in 
 * to the constructor (in seconds).  Calls to the model will all
 * be performed on the AWT event thread.
 */

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;


public class BadtzPlayer implements GameObserver {

    private final Timer eventTimer;
    private boolean eventPending = false;
    private final GameModel model;
    private final double maxTime;
    private Random rand = new Random();

    /**
     * Create a new BadtzPlayer.  The player will start itself, by attaching
     * to the model.  It
     * will automatically shut down when the GameModel tells it the game 
     * is over.
     *
     * @param   model           The game Badtz is playing
     * @param   maxTime         The maximum amount of delay for the Badtz 
     *                          player, in seconds.  A value around 15% of
     *                          the total game time works OK.
     */
    public BadtzPlayer(GameModel model, double maxTime) {
        eventTimer = new Timer(1_000_000, 
                              (ActionEvent e) -> { runNextEvent(); });
        eventTimer.setRepeats(false);
        this.model = model;
        this.maxTime = maxTime;
        model.attach(this);
    }

    private void runNextEvent() {
        eventPending = false;
        GameState state = model.getState();
        final List<Point> rude = new ArrayList<Point>();
        state.accept(new BoardVisitor() {
            @Override public void visitRudeDude(int x, int y) {
                rude.add(new Point(x, y));
            }
            @Override public void visitSuperKind(int x, int y) {
            }
            @Override public void visitBadtzMaru(int x, int y) {
            }
            @Override public void visitBackground(int x, int y) {
            }
        });
        if (rude.size() == 0) {
            return;
        }
        int i = rand.nextInt(rude.size());
        Point victim = rude.get(i);
        model.setToBadtzMaru(victim.x, victim.y);
        if (rude.size() > 1) {
            scheduleNextEvent();
        }
    }

    @Override
    public void update() {
        GameState state = model.getState();
        if (state.getGameOver()) {
            model.detach(this);
            eventTimer.stop();
            return;
        }
        if (eventPending) {
            return;
        }
        state.accept(new BoardVisitor() {
            @Override public void visitRudeDude(int x, int y) {
                scheduleNextEvent();
            }
            @Override public void visitSuperKind(int x, int y) {
            }
            @Override public void visitBadtzMaru(int x, int y) {
            }
            @Override public void visitBackground(int x, int y) {
            }
        });
    }

    private void scheduleNextEvent() {
        if (eventPending) {
            return;
        }
        eventPending = true;
        int delay = (int) ((0.1 + 0.9 * rand.nextDouble()) * maxTime * 1000);
        eventTimer.setInitialDelay(delay);
        eventTimer.start();
    }
}
