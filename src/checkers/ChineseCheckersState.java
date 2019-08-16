package checkers;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ChineseCheckersState {
    private final String currentPlayer;
    private final ArrayList<Point> board;
    private final ArrayList<Point> highlightedPoints;
    private final HashMap<String, HashMap<Point, Point>> positionsByPlayer;
    private final HashMap<String, HashMap<Point, Point>> startingPositions;

    public ChineseCheckersState(ArrayList<Point> board, HashMap<String, HashMap<Point, Point>> positionsByPlayer,
                                ArrayList<Point> highlightedPoints, String currentPlayer, HashMap<String, HashMap<Point, Point>> startingPositions){
        this.board = board;
        this.highlightedPoints = highlightedPoints;
        this.positionsByPlayer = positionsByPlayer;
        this.currentPlayer = currentPlayer;
        this.startingPositions = startingPositions;
    }

    /**
     * Visit every location in the game board
     */
    public void accept(ChineseCheckersVisitor v){
        for (Point p : highlightedPoints) {
            if("blue".equals(currentPlayer)) {
                v.visitHighlight(p.x, p.y, Color.BLUE);
            } else if("magenta".equals(currentPlayer)){
                v.visitHighlight(p.x, p.y, Color.MAGENTA);
            } else if("yellow".equals(currentPlayer)){
                v.visitHighlight(p.x, p.y, Color.YELLOW);
            } else if("red".equals(currentPlayer)){
                v.visitHighlight(p.x, p.y, Color.RED);
            } else if("green".equals(currentPlayer)){
                v.visitHighlight(p.x, p.y, Color.GREEN);
            } else if("cyan".equals(currentPlayer)){
                v.visitHighlight(p.x, p.y, Color.CYAN);
            }
        }
        for(Point p: board){
            v.visitBackground(p.x, p.y);
        }
        for(Point p: positionsByPlayer.get("blue").keySet()){
            v.visitBlue(p.x, p.y);
        }
        for(Point p: positionsByPlayer.get("magenta").keySet()){
            v.visitMagenta(p.x, p.y);
        }
        for(Point p: positionsByPlayer.get("yellow").keySet()){
            v.visitYellow(p.x, p.y);
        }
        for(Point p: positionsByPlayer.get("red").keySet()){
            v.visitRed(p.x, p.y);
        }
        for(Point p: positionsByPlayer.get("green").keySet()){
            v.visitGreen(p.x, p.y);
        }
        for(Point p: positionsByPlayer.get("cyan").keySet()){
            v.visitCyan(p.x, p.y);
        }

    }

    public String getCurrentPlayer(){
        return currentPlayer;
    }

    //TODO
    public String getGameWinner(){
        HashMap<Point, Point> goal;

        for(String player : positionsByPlayer.keySet()){
            if("blue".equals(player)) {
                goal = startingPositions.get("red");
            } else if("magenta".equals(player)){
                goal = startingPositions.get("green");
            } else if("yellow".equals(player)){
                goal = startingPositions.get("cyan");
            } else if("red".equals(player)){
                goal = startingPositions.get("blue");
            } else if("green".equals(player)){
                goal = startingPositions.get("magenta");
            } else {
                goal = startingPositions.get("yellow");
            }

            boolean containsEnemy = false;
            boolean full = true;
            for(Point p : goal.keySet()){
                boolean pointIsOccupied = false;
                //See if another player occupies this location
                for(String occupant : positionsByPlayer.keySet()){
                    if(positionsByPlayer.get(occupant).get(p) != null){
                        pointIsOccupied = true;
                        //is it one of our own players?
                        if(occupant.equals(player)){
                            containsEnemy = true;
                        }

                    }
                }
                if(!pointIsOccupied){
                    full = false;
                }
            }
            if(containsEnemy && full){
                return player;
            }
        }
        return null;
    }

    public String getLastPlayer() {
        if("blue".equals(currentPlayer)) {
            return "cyan";
        } else if("magenta".equals(currentPlayer)) {
            return "blue";
        } else if("yellow".equals(currentPlayer)) {
            return "magenta";
        } else if("red".equals(currentPlayer)) {
            return "yellow";
        } else if("green".equals(currentPlayer)) {
            return "red";
        } else if("cyan".equals(currentPlayer)) {
            return "green";
        } else {
            return null;
        }
    }

    public String getNextPlayer() {
        if("blue".equals(currentPlayer)) {
            return "magenta";
        } else if("magenta".equals(currentPlayer)) {
            return "yellow";
        } else if("yellow".equals(currentPlayer)) {
            return "red";
        } else if("red".equals(currentPlayer)) {
            return "green";
        } else if("green".equals(currentPlayer)) {
            return "cyan";
        } else if("cyan".equals(currentPlayer)) {
            return "blue";
        } else {
            return null;
        }
    }


    public HashMap<String, HashMap<Point, Point>> getPositionsByPlayer() {
        return positionsByPlayer;
    }

    public ArrayList<Point> getBoard() {
        return board;
    }

    public ArrayList<Point> getHighlightedPoints() {
        return highlightedPoints;
    }

    public HashMap<String, HashMap<Point, Point>> getStartingPositions() {
        return startingPositions;
    }

    @Override
    public int hashCode(){
        int i = currentPlayer.hashCode();
        for(String player : positionsByPlayer.keySet()){
            i += player.hashCode();
            i *= 13;
            for(Point p : positionsByPlayer.get(player).keySet()){
                i += p.hashCode();
            }
        }
        return i;
    }

    /**
     * This equals method returns true if the same player is currently playing
     * AND if all of the positions are the same for each player. Everything else is ignored.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof ChineseCheckersState)) {
            return false;
        }
        ChineseCheckersState other = (ChineseCheckersState) obj;
        if(!(other.getCurrentPlayer().equals(currentPlayer))){
            return false;
        }
        for(String player : positionsByPlayer.keySet()){
            for(Point p : positionsByPlayer.get(player).keySet()){
                if(other.getPositionsByPlayer().get(player).get(p) == null){
                    return false;
                }
            }
        }
        return true;
    }
}
