
package others;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * This class implements a graphical window that can be used to hold
 * a single component.
 */
public class GameWindow {

    private final static long SECONDS_TO_NANOS = 1_000_000_000L;

    private final JFrame frame;

    /**
     * Create a new window that holds a single component.  Normally, the window will be
     * sized to the component's preferred size.  You may with to set this; see
     * java.awt.Component.setPreferredSize(java.awt.Dimension).
     *
     * @param title     The title for the window
     * @param contents   The component inside the frame
     * @param onWindowClose  Code you want run when the window closes.  
                             May be null.
     */
    public GameWindow(String title, final JComponent contents, 
                      final Runnable onWindowClose) 
    {
        frame = new JFrame(title);
        frame.setLocationByPlatform(true);
        frame.setLayout(new BorderLayout());
        if (onWindowClose != null) {
            frame.addWindowListener(new WindowAdapter() {
                @Override public void windowClosing(WindowEvent e) {
                    onWindowClose.run();
                }
            });
        }
        frame.add(contents);
    }

    /**
     * Start up the window:  Set the size, and make it visible.
     */
    public void start() {
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Finish with this window:  Make it invisible, and release its
     * resources.
     */
    public void finish() {
        frame.dispose();
    }

    /**
     * A utility method for loading an image, so you don't have to find
     * ImageIO.  On failure, this method prints out an error message,
     * and terminates the program with an error code.  This makes it a 
     * little more convenient  to use this method to initialize a 
     * static final data member.
     */
    public static BufferedImage loadImage(String imageFile) {
        try { 
            return ImageIO.read(new File(imageFile));
        } catch (IOException ex) {
            System.err.println("***  Error reading image " + imageFile);
            ex.printStackTrace();
            System.exit(1);
            return null;        // This is never executed
        }
    }
}
