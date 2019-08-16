package checkers;

import others.GameLauncher;
import edu.calpoly.testy.TestRunnable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ChineseCheckersLauncher implements GameLauncher {
    public static Map<String, ChineseCheckersGame> gamesByName = new HashMap<>();
    private ReentrantLock LOCK = new ReentrantLock();

    /**
     * The name of the game this object launches.  It must end with a colon.
     */
    public String getName(){
        return "cch:";
    }

    /**
     * Run the game.  This methods may use the calling thread to run the
     * game(s), or it may return immediately (after arranging play to occur on
     * some other thread or threads).
     *
     *  @return true on success, or false if there's a problem.
     */
    public boolean run(List<String> args){
        LOCK.lock();
        try {
            if (args.size() < 1) {
                return false;
            }
            ChineseCheckersGame.loadImages();

            String name = args.get(0);

            //Add a new ChineseCheckersGame
            if (gamesByName.get(name) == null) {
                ChineseCheckersGame newGame = new ChineseCheckersGame();
                gamesByName.put(name, newGame);
                newGame.play();
            }
            ChineseCheckersViewer viewer = new ChineseCheckersViewer(gamesByName.get(name).getModel(), name);
            gamesByName.get(name).getModel().attach(viewer);
            viewer.start();

            return true;
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * Add unit tests to this game to the given list.
     */
    public void addUnitTests(List<TestRunnable> tests){

    }

    /**
     * Print the usage method for this game to System.err
     */
    public void printUsage(){
        System.err.println("            cch: game_name");
        System.err.println("                robot: <seconds> game_name [player_number*]");
        System.err.println("                May have zero to six robots");
    }
}
