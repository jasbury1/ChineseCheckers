package checkers;

import java.awt.*;
import java.util.ArrayList;

public class BoardUtil {
    public BoardUtil(ChineseCheckersModel model){

    }

    /**
     * Return true if a given point is on the board
     */
    public static boolean onBoard(Point p){
        if(p.x < -12 || p.x > 12 || p.y < -8 || p.y > 8){
            return false;
        }
        if(((Math.abs(p.x) <= 8-p.y) && (Math.abs(p.y)%2 == Math.abs(p.x)%2) && (p.y>=-4)) ||
                ((Math.abs(p.x) <= Math.abs(-8 - p.y)) && (Math.abs(p.y)%2 == Math.abs(p.x)%2) && (p.y<= 4))){
            return true;
        }
        return false;
    }

    public static boolean inHome(Player player, Point p){
        for(Point pos : player.getStartPositions()){
            if(pos.equals(p)){
                return true;
            }
        }
        return false;
    }

    public static boolean inDestination(Player player, Point p, ChineseCheckersModel model){
        ArrayList<Point> destinationPositions;
        if(player.getColor().equals("blue")){
            destinationPositions = (model.getPlayerByColor("red").getStartPositions());
        } else if(player.getColor().equals("green")){
            destinationPositions = (model.getPlayerByColor("magenta").getStartPositions());
        } else if(player.getColor().equals("cyan")){
            destinationPositions = (model.getPlayerByColor("yellow").getStartPositions());
        } else if(player.getColor().equals("magenta")){
            destinationPositions = (model.getPlayerByColor("green").getStartPositions());
        } else if(player.getColor().equals("yellow")){
            destinationPositions = (model.getPlayerByColor("cyan").getStartPositions());
        } else {
            destinationPositions = (model.getPlayerByColor("blue").getStartPositions());
        }
        for(Point pos : destinationPositions){
            if(pos.equals(p)){
                return true;
            }
        }
        return false;
    }
}
