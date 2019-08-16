package superkind.network;

import others.Size;
import others.superkind.GameModel;
import others.superkind.GameObserver;
import others.superkind.GameState;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class SuperKindNetworkModel implements GameModel{
    private ArrayList<GameObserver> observers = new ArrayList<>();
    private ArrayList<GameObserver> observersCopy;
    private ReentrantLock LOCK = new ReentrantLock();
    private ReentrantLock STATE_LOCK = new ReentrantLock();
    private ReentrantLock TILE_LOCK = new ReentrantLock();

    private char playerType;
    private char winner = ' ';

    private final Size GAME_SIZE = new Size(6, 5);
    private String tiles = "                              ";

    private MessageQueue messageQueue;

    //CONSTRUCTOR
    public SuperKindNetworkModel(MessageQueue messageQueue){
        this.messageQueue = messageQueue;
    }

    public void updateBoard(String board){
        TILE_LOCK.lock();
        try{
            tiles = board;
            notifyObservers();
        } finally {
            TILE_LOCK.unlock();
        }
    }

    /**
     * Attach an observer.  If the same observer is already attached, the
     * semantics of this method are undefined.
     */
    public void attach(GameObserver o){
        LOCK.lock();
        try {
            observers.add(o);
            observersCopy = null;
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * Detach an observer.  Does nothing if that observer isn't present.
     */
    public void detach(GameObserver o){
        LOCK.lock();
        try {
            observers.remove(o);
            observersCopy = null;
        } finally {
            LOCK.unlock();
        }
    }

    public void notifyObservers(){
        LOCK.lock();
        ArrayList<GameObserver> copy;
        try{
            if(observersCopy == null){
                observersCopy = new ArrayList<>(observers);
            }
            copy = observersCopy;
        }
        finally{
            LOCK.unlock();
        }
        for(GameObserver o : copy){
            o.update();
        }
    }

    /**
     * Set the given square to contain Super Kind, if this is valid.  Does nothing
     * if x or y are out of range, or if the square doesn't contain Rude Dude.
     */
    public void setToSuperKind(int x, int y){
        TILE_LOCK.lock();
        try {
            if (x < 0 || x > 5 || y < 0 || y > 4)
                return;
            if(tiles.charAt(y * 6 + x) == 'r'){
                ArrayList<String> command = new ArrayList<>();
                command.add("occupy");
                command.add("s");
                command.add(Integer.toString(x));
                command.add(Integer.toString(y));
                messageQueue.submitMessage(command);
                notifyObservers();
            }
        } finally {
            TILE_LOCK.unlock();
        }
    }

    /**
     * Set the given square to contain Badtz Maru, if this is valid.  Does nothing
     * if x or y are out of range, or if the square doesn't contain Rude Dude.
     */
    public void setToBadtzMaru(int x, int y){
        TILE_LOCK.lock();
        try {
            if (x < 0 || x > 5 || y < 0 || y > 4) {
                return;
            }
            if(tiles.charAt(y * 6 + x) == 'r'){
                ArrayList<String> command = new ArrayList<>();
                command.add("occupy");
                command.add("b");
                command.add(Integer.toString(x));
                command.add(Integer.toString(y));
                messageQueue.submitMessage(command);
                notifyObservers();
            }
        } finally {
            TILE_LOCK.unlock();
        }
    }

    /**
     * Get a snapshot the current state of play at this moment in time.
     */
    public GameState getState(){
        STATE_LOCK.lock();
        try {
            boolean gameOver = false;
            if(winner != ' '){
                gameOver = true;
            }
            String board = new String(tiles);
            char gameWinner = winner;
            return new SuperKindNetworkState(gameOver, board, gameWinner);
        } finally {
            STATE_LOCK.unlock();
        }
    }

    /**
     * Get the size of the board.  This never changes.
     */
    public Size getBoardSize(){
        return GAME_SIZE;
    }

    public void setPlayerType(char player){
        this.playerType = player;
    }

    public char getPlayerType(){
        return playerType;
    }

    public void setWinner(char winner){
        this.winner = winner;
        if(winner == ' '){
            return;
        }
        System.out.println();
        if(winner == 'r'){
            System.out.println("***  No chocolate for you ***");
        }
        else if(winner == 'b'){
            System.out.println("***  Congratulations Batdz!  BABYMETAL should give " +
                    "*you* chocolate!  ***");
        }
        else if(winner == 's'){
            System.out.println("***  Congratulations SuperKind!  BABYMETAL should give " +
                    "*you* chocolate!  ***");
        }
        else{
            System.out.println("***  Congratulations SuperKind!  BABYMETAL should give " +
                    "*you* chocolate!  ***");
        }
        System.out.println();
        ArrayList<String> command = new ArrayList<>();
        command.add("end");
        messageQueue.submitMessage(command);

        notifyObservers();
    }

}
