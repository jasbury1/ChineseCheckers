package checkers;

import others.GameWindow;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ChineseCheckersGame {
    private ChineseCheckersModel gameModel;
    private ArrayList<Point> boardPositions = new ArrayList<>();
    private HashMap<String, HashMap<Point, Point>> positionsByPlayer = new HashMap<>();
    private Player[] players = new Player[6];

    public ChineseCheckersGame(){
        initPlayers();
        initBoard();
        this.gameModel = new ChineseCheckersModel(boardPositions, players, positionsByPlayer);
    }

    public static void loadImages() {
        ChineseCheckersViewer.undo = GameWindow.loadImage("images/undo.png");
        ChineseCheckersViewer.wood = GameWindow.loadImage("images/wood.jpg");
    }

    public void play(){
    }

    public ChineseCheckersModel getModel(){
        return gameModel;
    }

    public void initBoard(){
        synchronized (players){
            for (int y = 8; y >= -8; y--) {
                for (int x = -12; x <= 12; x++) {
                    if ((Math.abs(x) <= 8 - y) && (Math.abs(y) % 2 == Math.abs(x) % 2) && (y >= -4)) {
                        boardPositions.add(new Point(x, y));
                    } else if ((Math.abs(x) <= Math.abs(-8 - y)) && (Math.abs(y) % 2 == Math.abs(x) % 2) && (y <= 4)) {
                        boardPositions.add(new Point(x, y));
                    }
                }
            }
        }

    }

    public void initPlayers(){
        synchronized(players) {
            positionsByPlayer.put("blue", new HashMap<>());
            players[0] = new Player(0, positionsByPlayer.get("blue"));
            players[0].initStartLocations(new Point(0, 8));

            positionsByPlayer.put("magenta", new HashMap<>());
            players[1] = new Player(1, positionsByPlayer.get("magenta"));
            players[1].initStartLocations(new Point(9, 1));

            positionsByPlayer.put("yellow", new HashMap<>());
            players[2] = new Player(2, positionsByPlayer.get("yellow"));
            players[2].initStartLocations(new Point(9, -1));

            positionsByPlayer.put("red", new HashMap<>());
            players[3] = new Player(3, positionsByPlayer.get("red"));
            players[3].initStartLocations(new Point(0, -8));

            positionsByPlayer.put("green", new HashMap<>());
            players[4] = new Player(4, positionsByPlayer.get("green"));
            players[4].initStartLocations(new Point(-9, -1));

            positionsByPlayer.put("cyan", new HashMap<>());
            players[5] = new Player(5, positionsByPlayer.get("cyan"));
            players[5].initStartLocations(new Point(-9, 1));
        }

    }

    public void attachRobot(double thinkingTime, ArrayList<Integer> positions){
        synchronized(players) {
            for (int pos : positions) {
                if (players[pos] == null) {
                    System.err.println("Error: Robot must be initialized after the game!");
                    System.exit(0);
                }
                players[pos] = new Robot(players[pos], pos, thinkingTime, gameModel);
                //(players[pos]).start();
                //attach((CheckersObserver) (players[pos]));
                gameModel.notifyObservers();
            }
        }
    }

}
