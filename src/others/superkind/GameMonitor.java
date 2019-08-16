
package others.superkind;

/**
 * This class creates a window that monitors the state of play of a
 * SuperKind game, by attaching itself as an observer.  A GameMonitor 
 * will only call the Super Kind
 * GameModel from within a method that you call.  In other words, a
 * GameMonitor will never call your GameModel on a different thread.
 * <p>
 * WARNING:  The restriction on GameModel and threading is not typical
 * for an MVC-based program, or for the Observer pattern.  It is a
 * simplification, for the purposes of this programming exercise.  Normally, 
 * a view can, and frequently does, consult the model on any thread of
 * its choosing, like the thread that the system uses to repaint
 * graphical components.  In order to deal with this, a model typically
 * has to correctly employ a lock object.  In Java, we can equivallently
 * say the model need to be "properly synchronized."  These are both topics
 * we will be addressing later in class.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.JComponent;

import others.Size;
import others.GameWindow;

public final class GameMonitor implements GameObserver {

    private final static int SQUARE_WIDTH = 40; // pixels wide/high

    private JComponent component = new JComponent() {
        @Override public void paint(Graphics g) {
            super.paint(g);
            GameMonitor.this.paintComponent((Graphics2D) g);
        }
    };

    private final GameWindow window;
    private final GameModel model;
    private GameState boardState;
    private final Size boardSize;
    private final Lock lock = new ReentrantLock();


    public GameMonitor(String name, GameModel model) {
        window = new GameWindow(name, component, () -> {
            handleWindowClosing();
        });
        this.model = model;
        this.boardSize = model.getBoardSize();
        this.boardState = model.getState();
    }

    public void start() {
        component.setPreferredSize(new Dimension(
            boardSize.width * SQUARE_WIDTH,
            boardSize.height * SQUARE_WIDTH
        ));
        window.start();
        model.attach(this);
    }

    private void paintComponent(Graphics2D g) {
        Dimension size = component.getSize();
        GameState state;
        try {
            lock.lock();
            state = boardState;
        } finally {
            lock.unlock();
        }
        g.setColor(Color.gray);
        g.fillRect(0, 0, size.width, size.height);
        if (state.getGameOver()) {
            handleWindowClosing();
            return;
        }

        // Width and height of square we draw to show Super Kind or
        // Rude Dude:
        double w = size.getWidth() / boardSize.width;
        double h = size.getHeight() / boardSize.height;

        state.accept(new BoardVisitor() {
            
            @Override public void visitRudeDude(int x, int y) {
                g.setColor(Color.red);
                Shape shape = new Rectangle2D.Double(x * w, y * h, w, h);
                g.fill(shape);
            }

            @Override public void visitSuperKind(int x, int y) {
                g.setColor(Color.green);
                Shape shape = new Rectangle2D.Double(x * w, y * h, w, h);
                g.fill(shape);
            }

            @Override public void visitBadtzMaru(int x, int y) {
                g.setColor(Color.black);
                Shape shape = new Rectangle2D.Double(x * w, y * h, w, h);
                g.fill(shape);
            }

            @Override public void visitBackground(int x, int y) {
                // No action required.
            }
        });
    }

    private void handleWindowClosing() {
        window.finish();
        model.detach(this);
    }

    @Override
    public void update() {
        try {
            lock.lock();
            boardState = model.getState();
        } finally {
            lock.unlock();
        }
        component.repaint();
    }

}
