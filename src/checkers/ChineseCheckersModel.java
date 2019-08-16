package checkers;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ChineseCheckersModel {

    private HashMap<String, HashMap<Point, Point>> positionsByPlayer;
    private ArrayList<Point> boardPositions;
    private Player[] players;
    private ReentrantLock LOCK = new ReentrantLock();
    //Lock for observers
    private ReentrantLock O_LOCK = new ReentrantLock();

    private ReentrantLock conditionLock = new ReentrantLock();
    public Condition condition = conditionLock.newCondition();

    //MUST NOT BE MODIFIED
    private HashMap<String, HashMap<Point, Point>> startingPositions;

    private ArrayList<CheckersObserver> observers = new ArrayList<>();
    private ArrayList<CheckersObserver> observersCopy;

    public Player currentPlayer;
    private Point selectedPoint;
    private ArrayList<Point> possibleMoves = new ArrayList<>();
    private Stack<MoveCommand> movesTaken = new Stack<>();

    private boolean gameOver = false;

    public ChineseCheckersModel(ArrayList<Point> boardPositions, Player[] players, HashMap<String, HashMap<Point, Point>> positionsByPlayer){
        this.players = players;
        this.boardPositions = boardPositions;
        this.positionsByPlayer = positionsByPlayer;
        Heuristic.getInstance();
    }

    public ChineseCheckersState getState(){
        LOCK.lock();
        try{
            HashMap<String, HashMap<Point, Point>> positionsCopy = new HashMap<>();
            ArrayList<Point> possibleMovesCopy = new ArrayList<>();
            //Get Positions by player
            for(String player: positionsByPlayer.keySet()){
                HashMap<Point, Point> occupiedPoints = new HashMap<>();
                for(Point p: positionsByPlayer.get(player).keySet()){
                    occupiedPoints.put(new Point(p.x, p.y), new Point(p.x, p.y));
                }
                positionsCopy.put(player, occupiedPoints);
            }
            //Get possible moves to highlight them
            for(Point p : possibleMoves){
                possibleMovesCopy.add(new Point(p.x, p.y));
            }
            //Get Starting Locations
            if(startingPositions == null){
                startingPositions = new HashMap<>();
                for(String player : positionsByPlayer.keySet()){
                    HashMap<Point, Point> locations = new HashMap<>();
                    for(Point p : positionsByPlayer.get(player).keySet()){
                        locations.put(p, p);
                    }
                    startingPositions.put(player, locations);
                }
            }

            return new ChineseCheckersState(boardPositions, positionsCopy, possibleMovesCopy, getCurrentPlayer().getColor(), startingPositions);
        }
        finally{
            LOCK.unlock();
        }
    }

    public GameParticipant getCurrentPlayer(){
        synchronized (players){
            if(currentPlayer == null){
                currentPlayer = players[0];
            }
            if(currentPlayer instanceof Robot){
                if(((Robot)currentPlayer).running == false){
                    ((Robot) currentPlayer).running = true;
                    currentPlayer.start();
                }
            }
            return currentPlayer;
        }
    }

    public Point getSelectedPoint(){
        return selectedPoint;
    }


    //CANNOT CALL NOTIFY OBSERVERS!
    //called by execute move which takes out lock
    //notify observers gets the lock because of getState
    public void nextTurn(){
        synchronized (players){
            if(currentPlayer.getPlayerNumber() == 5){
                currentPlayer = players[0];
                return;
            }
            currentPlayer = players[currentPlayer.getPlayerNumber() + 1];
            finishRobotTurn();
        }

    }

    public void prevTurn(){
        synchronized (players){
            if(currentPlayer.getPlayerNumber() == 0){
                currentPlayer = players[5];
                return;
            }
            currentPlayer = players[currentPlayer.getPlayerNumber() - 1];
            finishRobotTurn();
        }
    }


    public void executeMove(MoveCommand move){
        LOCK.lock();
        try {
            Point to = move.getTo();
            Point from = move.getFrom();
            Point initial = currentPlayer.getOccupiedPositions().get(from);
            if(initial == null){
                return;
            }
            if(currentPlayer instanceof Robot){
                currentPlayer.getOccupiedPositions().remove(initial);
                currentPlayer.getOccupiedPositions().put(to, to);
                selectedPoint = null;
                possibleMoves = new ArrayList<>();
                movesTaken.push(move);
                nextTurn();
                return;
            }
            else{
                for (Point t : possibleMoves) {
                    if(t.x == to.x && t.y == to.y){
                        currentPlayer.getOccupiedPositions().remove(initial);
                        currentPlayer.getOccupiedPositions().put(to, to);
                        selectedPoint = null;
                        possibleMoves = new ArrayList<>();
                        movesTaken.push(move);
                        nextTurn();
                        return;
                    }
                }
                //Not a valid "to" location
                return;
            }
        } finally {
            LOCK.unlock();
        }
    }

    public void executeUndo(){
        LOCK.lock();
        try {
            if(movesTaken.isEmpty()){
                return;
            }
            MoveCommand move = movesTaken.pop();

            Point to = move.getFrom();
            Point from = move.getTo();
            prevTurn();


            Point p = currentPlayer.getOccupiedPositions().get(from);
            if(p == null){
                return;
            }
            currentPlayer.getOccupiedPositions().remove(p);
            currentPlayer.getOccupiedPositions().put(to, to);
            return;
        } finally {
            LOCK.unlock();
        }

    }

    public void executeSelect(Point newPoint){
        LOCK.lock();
        try {
            possibleMoves = new ArrayList<>();
            selectedPoint = newPoint;
            getNeighboringMoves(newPoint, possibleMoves);
        } finally {
            LOCK.unlock();
        }

    }

    public Player getPlayerByColor(String color){
        if("blue".equals(color)) {
            return players[0];
        } else if("magenta".equals(color)) {
            return players[1];
        } else if("yellow".equals(color)) {
            return players[2];
        } else if("red".equals(color)) {
            return players[3];
        } else if("green".equals(color)) {
            return players[4];
        } else if("cyan".equals(color)) {
            return players[5];
        } else {
            return null;
        }
    }

    /**
     * from a given game piece point find all board locations that piece can move to
     */
    public void getNeighboringMoves(Point current, ArrayList<Point> result) {
        //if point is occupied, add to searchAround
        //else add to result
        //you can't move then hop

        Point[] neighbors = new Point[]{new Point(current.x + 1, current.y + 1),
                new Point(current.x + 1, current.y - 1),
                new Point(current.x - 1, current.y + 1),
                new Point(current.x - 1, current.y - 1),
                new Point(current.x - 2, current.y),
                new Point(current.x + 2, current.y)};

        for (Point neighbor : neighbors) {
            if (BoardUtil.onBoard(neighbor)) {
                if (!("".equals(findOccupant(neighbor)))) {
                    getHoppingMoves(current, current, neighbor, result, new ArrayList<>());
                } else {
                    //Make sure we aren't hopping out of our final destination
                    //or hopping back into home
                    if(BoardUtil.inHome(currentPlayer, current) ||
                            !(BoardUtil.inHome(currentPlayer, current)) && !(BoardUtil.inHome(currentPlayer, neighbor))) {
                        if(BoardUtil.inDestination(currentPlayer, neighbor, this) ||
                                !BoardUtil.inDestination(currentPlayer, current, this)) {
                            result.add(neighbor);
                        }
                    }
                }
            }
        }
    }

    public void getHoppingMoves(Point original, Point current, Point other, ArrayList<Point> result, ArrayList<Point> discard){
        Point search = new Point(current.x + 2*(other.x - current.x), current.y + 2*(other.y - current.y) );
        //Make sure it's on the board!
        if(!(BoardUtil.onBoard(search))){
            return;
        }
        if ("".equals(findOccupant(search))){
            //Make sure we haven't found this point already!
            for(Point p : result){
                if(search.x == p.x && search.y == p.y){
                    return;
                }
            }
            for(Point p : discard){
                if(search.x == p.x && search.y == p.y){
                    return;
                }
            }
            //Make sure we aren't hopping out of our final destination
            //or hopping back into home
            if(BoardUtil.inHome(currentPlayer, current) ||
                    !(BoardUtil.inHome(currentPlayer, current)) && !(BoardUtil.inHome(currentPlayer, search))) {
                if(BoardUtil.inDestination(currentPlayer, search, this) ||
                        !BoardUtil.inDestination(currentPlayer, original, this)) {
                    result.add(search);
                }
                else{
                    discard.add(search);
                }
            }
            Point[] neighbors = new Point[]{new Point(search.x + 1, search.y + 1),
                    new Point(search.x + 1, search.y - 1),
                    new Point(search.x - 1, search.y + 1),
                    new Point(search.x - 1, search.y - 1),
                    new Point(search.x - 2, search.y),
                    new Point(search.x + 2, search.y)};
            for (Point neighbor : neighbors) {
                if (BoardUtil.onBoard(neighbor)) {
                    if (!("".equals(findOccupant(neighbor)))) {
                        getHoppingMoves(original, search, neighbor, result, discard);
                    }
                }
            }
        }

    }

    /**
     * returns the color of the player on a spot, or empty string if its not occupied
     */
    public String findOccupant(Point p){

        for(String opponent : positionsByPlayer.keySet()){
            if(positionsByPlayer.get(opponent).get(p) != null){
                return opponent;
            }
        }
        return "";
    }

    /**
     * Attach an observer.  If the same observer is already attached, the
     * semantics of this method are undefined.
     */
    public void attach(CheckersObserver o){
        O_LOCK.lock();
        try {
            observers.add(o);
            observersCopy = null;
        } finally {
            O_LOCK.unlock();
        }
    }

    /**
     * Detach an observer.  Does nothing if that observer isn't present.
     */
    public void detach(CheckersObserver o){
        O_LOCK.lock();
        try {
            observers.remove(o);
            observersCopy = null;
            if(observers.size() == 0){
                for(Player p : players){
                    //call next turn
                    //finish robot turn
                    if(p instanceof Robot){
                        p.interrupt();
                        ((Robot) p).killed = true;
                    }
                }
                //Make 2 laps to ensure robots are all dead
                for(int i = 0; i < 13; ++i){

                    finishRobotTurn();

                    nextTurn();
                }

            }
        } finally {
            O_LOCK.unlock();
        }
    }

    public void getRobotTurn(Robot robot) {
        conditionLock.lock();
        try {
            while (!(robot.equals(currentPlayer)) || robot.killed) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
        finally {
            conditionLock.unlock();
        }
    }

    public void finishRobotTurn(){
        conditionLock.lock();
        try {
            condition.signalAll();
        } finally {
            conditionLock.unlock();
        }
    }

    public void notifyObservers(){
        O_LOCK.lock();
        ArrayList<CheckersObserver> copy;
        try{
            if(observersCopy == null){
                observersCopy = new ArrayList<>(observers);
            }
            copy = observersCopy;
        }
        finally{
            O_LOCK.unlock();
        }
        for(CheckersObserver o : copy){
            o.update();
        }
    }

    public boolean isGameOver(){
        return gameOver;
    }



}
