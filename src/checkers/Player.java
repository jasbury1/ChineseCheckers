package checkers;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Player extends Thread implements GameParticipant{
    protected final int playerNumber;

    //TODO: Make these hashTables
    protected HashMap<Point, Point> occupiedPositions;
    protected ArrayList<Point> startPositions = new ArrayList<>();

    public Player(int playerNumber, HashMap<Point, Point> occupiedPositions){
        this.playerNumber = playerNumber;
        this.occupiedPositions = occupiedPositions;
    }

    public void initStartLocations(Point source){
        if(playerNumber % 2 == 0){
            int acc = 0;
            for(int y = source.y; y > source.y - 4; y--){
                for(int i = -acc; i <= acc; i = i + 2){
                    Point newPoint = new Point((source.x + i), y);
                    occupiedPositions.put(newPoint, newPoint);
                    startPositions.add(newPoint);
                }
                ++acc;
            }
        }
        else {
            int acc = 0;
            for(int y = source.y; y < source.y + 4; y++){
                for(int i = -acc; i <= acc; i = i + 2){
                    Point newPoint = new Point((source.x + i), y);
                    occupiedPositions.put(newPoint, newPoint);
                    startPositions.add(newPoint);
                }
                ++acc;
            }
        }
    }

    public HashMap<Point, Point> getOccupiedPositions() {
        return occupiedPositions;
    }
    public ArrayList<Point> getStartPositions(){
        return startPositions;
    }

    public String getColor(){
        switch(playerNumber) {
            case (0):
                return "blue";
            case (1):
                return "magenta";
            case (2):
                return "yellow";
            case (3):
                return "red";
            case (4):
                return "green";
            case (5):
                return "cyan";
            default:
                return "";

        }
    }

    public int getPlayerNumber(){
        return playerNumber;
    }

    /**
     * This equals method will return true any time two players have the same
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof Player)) {
            return false;
        }
        Player other = (Player)obj;
        return other.getPlayerNumber() == playerNumber;
    }
}
