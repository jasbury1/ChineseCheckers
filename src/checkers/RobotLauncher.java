package checkers;

import others.GameLauncher;
import edu.calpoly.testy.TestRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class RobotLauncher implements GameLauncher {
    private ReentrantLock LOCK = new ReentrantLock();

    /**
     * The name of the game this object launches.  It must end with a colon.
     */
    public String getName(){
        return "robot:";
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
            if (args.size() < 3) {
                return false;
            }

            //Get the game that is being played
            String name = args.get(1);
            double thinkingTime = Double.parseDouble(args.get(0));
            ArrayList<Integer> robotPositions = new ArrayList<>();
            for(int i = 2; i < args.size() ; ++i){
                robotPositions.add(Integer.parseInt(args.get(i)));
            }

            //Add a new ChineseCheckersGame if necessary
            if (ChineseCheckersLauncher.gamesByName.get(name) == null) {
                ChineseCheckersGame newGame = new ChineseCheckersGame();
                ChineseCheckersLauncher.gamesByName.put(name, newGame);
                newGame.play();
            }
            ChineseCheckersLauncher.gamesByName.get(name).attachRobot(thinkingTime, robotPositions);
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
