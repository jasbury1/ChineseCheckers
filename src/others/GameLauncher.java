

package others;

import java.util.List;
import edu.calpoly.testy.TestRunnable;

public interface GameLauncher {


    /**
     * The name of the game this object launches.  It must end with a colon.  
     */
    public String getName();

    /**
     * Run the game.  This methods may use the calling thread to run the
     * game(s), or it may return immediately (after arranging play to occur on
     * some other thread or threads).
     *
     *  @return true on success, or false if there's a problem.
     */
    public boolean run(List<String> args);

    /**
     * Add unit tests to this game to the given list.
     */
    public void addUnitTests(List<TestRunnable> tests);

    /**
     * Print the usage method for this game to System.err
     */
    public void printUsage();
}
