package superkind.local;

import others.GameLauncher;

import java.util.List;
import others.superkind.GameMonitor;
import edu.calpoly.testy.TestRunnable;
import superkind.SuperKindLocalGame;

public class SuperKindLocalLauncher implements GameLauncher{

    /**
     * The name of the game this object launches.  It must end with a colon.
     */
    public String getName(){
        return "sk:";
    }

    /**
     * Run the game.  This methods may use the calling thread to run the
     * game(s), or it may return immediately (after arranging play to occur on
     * some other thread or threads).
     *
     *  @return true on success, or false if there's a problem.
     */
    public boolean run(List<String> args){
        if (args.size() < 1) {
            return false;
        }
        double seconds = 0.0;
        try {
            seconds = Double.parseDouble(args.get(0));
        } catch (NumberFormatException ex) {
            System.err.println("Error:  " + ex);
            return false;
        }



        SuperKindLocalGame.loadImages();

        SuperKindLocalGame game = new SuperKindLocalGame(seconds);

        // Set up game monitors:
        for (int i = 1; i < args.size(); i++) {
            GameMonitor m = new GameMonitor(args.get(i), game.getModel());
            m.start();
        }
        game.play();

        return true;
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
        System.err.println("            sk: seconds [ monitor_name * ]");
        System.err.println("                Launches superkind game lasting seconds");
        System.err.println("                May have zero or more game monitors");
    }
}