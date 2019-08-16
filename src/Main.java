

/**
 * This is a main class that parses command-line arguments.  You shouldn't
 * need to change it for this assignment.  From the structure, you might
 * guess that in the future, we'll be adding more games to this program.
 * That would be a good guess.  When we do this, you will modify this
 * file to add new launcher objects for each game.
 * <p>
 * Please don't make other changes to this file.  This will guarantee a
 * consistent way of launching programs for all of the students.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import checkers.ChineseCheckersLauncher;
import checkers.Heuristic;
import checkers.RobotLauncher;
import edu.calpoly.testy.Testy;
import edu.calpoly.testy.TestRunnable;

import others.GameLauncher;
import superkind.local.SuperKindLocalLauncher;
import superkind.network.SuperKindNetworkLauncher;

public class Main {


    private static GameLauncher[] launchers = { new SuperKindLocalLauncher(), new SuperKindNetworkLauncher(), new ChineseCheckersLauncher(), new RobotLauncher()};

    public static void main(String[] args) {

        if (args.length == 1 && "test".equals(args[0])) {
            runUnitTests();
            System.exit(0);
        }
        // The games the user wants to play, and the args for each:
        List<GameLauncher> games = new ArrayList<>();
        List<List<String>> gameArgs = new ArrayList<>();

        Map<String, GameLauncher> launcherByName = new HashMap<>();
        for (GameLauncher launcher : launchers) {
            String name = launcher.getName();
            assert name.endsWith(":");
            launcherByName.put(launcher.getName(), launcher);
        }

        // Collect the games and arguments:
        List<String> currentGameArgs = null;
        for (String arg : args) {
            if (arg.endsWith(":")) {    // new game
                GameLauncher launcher = launcherByName.get(arg);
                if (launcher == null) {
                    System.err.println("Unrecognized argument:  " + arg);
                    usage();
                }
                currentGameArgs = new ArrayList<>();
                games.add(launcher);
                gameArgs.add(currentGameArgs);
            } else if (currentGameArgs == null) {
                usage();
            } else {
                currentGameArgs.add(arg);
            }
        }

        if (games.size() == 0) {
            usage();
        }

        // Now launch them:
        assert games.size() == gameArgs.size();
        for (int i = 0; i < games.size(); i++) {
            final GameLauncher launcher = games.get(i);
            final List<String> launcherArgs = gameArgs.get(i);
            String threadName = "main[" + i + "] " + launcher.getName();
            Thread t = new Thread(() -> { runGame(launcher, new ArrayList<>(launcherArgs)); }, threadName);
            t.start();
        }
    }

    //
    // Run the given game.  If there's a problem, bail out with an error
    // message.
    //
    private static void runGame(GameLauncher launcher, List<String> args) {
        if (!launcher.run(args)) {
            usage();
        }
    }


    /**
     * Call the unit tests.  Generally, checkgit will run this
     * method, so don't do anything that requires graphics.  If you have
     * any images that your program needs to load, it's a good idea to
     * have a unit test that loads them.  That way, if your images don't
     * get check in to your repo, checkgit will catch it for you.
     */
    private static void runUnitTests() {
        List<TestRunnable> tests = new ArrayList<>();
        for (GameLauncher launcher : launchers) {
            launcher.addUnitTests(tests);
        }
        int failed = Testy.run(tests);
        if (failed > 0) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }

    /**
     * Print a usage statement and exit with an error code.  It's
     * considered good form to
     * print out a help message explaining the command-line arguments if
     * the program is invoked with something that's incorrect.
     */
    public static void usage() {
        System.err.println();
        System.err.println("Usage:  java Main test");
        System.err.println("        java Main [game_name: arg* ]*");
        for (GameLauncher launcher : launchers) {
            launcher.printUsage();
        }
        System.err.println();
        System.exit(1);
    }
}
