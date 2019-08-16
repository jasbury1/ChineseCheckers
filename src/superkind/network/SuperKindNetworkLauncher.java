package superkind.network;

import others.GameLauncher;

import java.util.List;
import others.superkind.GameMonitor;
import edu.calpoly.testy.TestRunnable;
import superkind.SuperKindNetworkGame;

import java.io.IOException;

public class SuperKindNetworkLauncher implements GameLauncher{

    /**
     * The name of the game this object launches.  It must end with a colon.
     */
    public String getName(){
        return "skc:";
    }

    /**
     * Run the game.  This methods may use the calling thread to run the
     * game(s), or it may return immediately (after arranging play to occur on
     * some other thread or threads).
     *
     *  @return true on success, or false if there's a problem.
     */
    public boolean run(List<String> args){
        if (args.size() < 4) {
            return false;
        }
        double seconds = 0.0;
        int port;
        String IPAddress;
        String sessionID;

        try {
            seconds = Double.parseDouble(args.get(0));
            port = Integer.parseInt(args.get(2));
        } catch (NumberFormatException ex) {
            System.err.println("Error:  " + ex);
            return false;
        }
        IPAddress = args.get(1);
        sessionID = args.get(3);

        SuperKindNetworkGame.loadImages();

        SuperKindNetworkGame game = new SuperKindNetworkGame(seconds, IPAddress, port, sessionID);

        // Set up game monitors:
        for (int i = 4; i < args.size(); i++) {
            GameMonitor m = new GameMonitor(args.get(i), game.getModel());
            m.start();
        }
        try {
            game.play();
        } catch (Exception e){
            e.printStackTrace();
        }

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
        System.err.println("            skc: seconds IP_Address port Session_ID [ monitor_name * ]");
        System.err.println("                Launches superkind game lasting seconds");
        System.err.println("                May have zero or more game monitors");
    }
}
