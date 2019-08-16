package superkind.local;

import others.Size;
import others.ModelTimer;
import others.superkind.GameModel;
import others.superkind.GameObserver;
import others.superkind.GameState;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;


public class SuperKindLocalModel implements GameModel{
    private ArrayList<GameObserver> observers = new ArrayList<>();
    private ArrayList<GameObserver> observersCopy;

    private double gameLengthSeconds;
    private boolean gameOver = false;
    private int numRudeDudes = 0;

    private ModelTimer timer;

    private final Size GAME_SIZE = new Size(6, 5);
    private GameTile[][] tiles = new GameTile[6][5];
    private PriorityQueue<GameTile> rudeDudeQueue = new PriorityQueue<>((GAME_SIZE.width * GAME_SIZE.height),
            (GameTile a, GameTile b) -> {
                return Double.compare(a.getChangeTime(), b.getChangeTime());
            });

    //CONSTRUCTOR
    public SuperKindLocalModel(double gameLengthSeconds){
        this.gameLengthSeconds = gameLengthSeconds;
        initTiles();
    }

    public void initTiles() {
        Random r = new Random();
        //Tiles should not spawn in the first or last 5% of the game
        double min = gameLengthSeconds * .05;
        double max = gameLengthSeconds - min;
        double randomValue;

        for (int i = 0; i < GAME_SIZE.width; ++i) {
            for (int j = 0; j < GAME_SIZE.height; ++j) {
                randomValue = min + (max - min) * r.nextDouble();
                GameTile newTile = new GameTile("logo", i, j, randomValue);
                tiles[i][j] = newTile;
                rudeDudeQueue.add(newTile);
            }
        }
    }

    public void start(){
        timer = new ModelTimer();
        timer.start();
        scheduleEvent();
    }

    public void scheduleEvent(){
        double eventTime;

        if(!(rudeDudeQueue.isEmpty())) {
            eventTime = rudeDudeQueue.peek().getChangeTime() - timer.getTimeSinceStart();
        }
        else {
            eventTime = gameLengthSeconds - timer.getTimeSinceStart();
        }
        timer.runAfter(eventTime, new EventGenerator(this));
    }


    /**
     * Attach an observer.  If the same observer is already attached, the
     * semantics of this method are undefined.
     */
    public void attach(GameObserver o){
        observers.add(o);
        observersCopy = null;
    }

    /**
     * Detach an observer.  Does nothing if that observer isn't present.
     */
    public void detach(GameObserver o){
        observers.remove(o);
        observersCopy = null;
    }

    public void notifyObservers(){
        if(observersCopy == null){
            observersCopy = new ArrayList<>(observers);
        }
        for(GameObserver o : observersCopy){
            o.update();
        }
    }

    /**
     * Set the given square to contain Super Kind, if this is valid.  Does nothing
     * if x or y are out of range, or if the square doesn't contain Rude Dude.
     */
    public void setToSuperKind(int x, int y){
        if(x < 0 || x > 6 || y < 0 || y > 5)
            return;
        if("rudeDude".equals(tiles[x][y].getTileType())){
            --numRudeDudes;
            tiles[x][y].setTileType("superKind");
            notifyObservers();
        }
    }

    /**
     * Set the given square to contain Badtz Maru, if this is valid.  Does nothing
     * if x or y are out of range, or if the square doesn't contain Rude Dude.
     */
    public void setToBadtzMaru(int x, int y){
        if(x < 0 || x > 5 || y < 0 || y > 4)
            return;
        if("rudeDude".equals(tiles[x][y].getTileType())){
            --numRudeDudes;
            tiles[x][y].setTileType("badtzMaru");
            notifyObservers();
        }
    }

    public void setToRudeDude(int x, int y){
        if(x < 0 || x > 6 || y < 0 || y > 5)
            return;
        if("logo".equals(tiles[x][y].getTileType())){
            ++numRudeDudes;
            tiles[x][y].setTileType("rudeDude");
            if(numRudeDudes >= 6)
                setGameOver();
            rudeDudeQueue.poll();
            scheduleEvent();
            notifyObservers();
        }
    }

    /**
     * Get a snapshot the current state of play at this moment in time.
     */
    public GameState getState(){
        GameTile[][] tilesCopy = new GameTile[6][5];
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 5; j++){
                GameTile tile = new GameTile(tiles[i][j].getTileType(), i, j, tiles[i][j].getChangeTime());
                tilesCopy[i][j] = tile;
            }
        }

        return new SuperKindLocalState(gameOver, tilesCopy);
    }

    /**
     * Get the size of the board.  This never changes.
     */
    public Size getBoardSize(){
        return GAME_SIZE;
    }

    public PriorityQueue<GameTile> getRudeDudeQueue() {
        return rudeDudeQueue;
    }

    public void setGameOver(){
        if(gameOver)
            return;
        if(numRudeDudes <= 5){
            System.out.println();
            System.out.println("***  Congratulations!  BABYMETAL should give " +
                    "*you* chocolate!  ***");
            System.out.println();
        }
        else{
            System.out.println();
            System.out.println("***  No chocolate for you ***");
            System.out.println();
        }
        gameOver = true;
        notifyObservers();
    }
}
