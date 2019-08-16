package checkers;

import java.awt.*;
import java.util.HashMap;


public class Robot extends Player{
    private ChineseCheckersModel model;
    private MonteCarloImplementation monteCarlo;
    public boolean running = false;
    public boolean killed = false;

    public Robot(int robotNumber, HashMap<Point, Point> occupiedPositions, double thinkingTime, ChineseCheckersModel model){
        super(robotNumber, occupiedPositions);
        this.model = model;
        this.monteCarlo = new MonteCarloImplementation(thinkingTime, 500, model, this.getColor());
    }
    public Robot(Player player, int robotNumber, double thinkingTime, ChineseCheckersModel model){
        super(robotNumber, player.getOccupiedPositions());
        this.model = model;
        this.monteCarlo = new MonteCarloImplementation(thinkingTime, 500, model, this.getColor());
    }


    @Override
    public void run(){
        while(!killed) {
            model.getRobotTurn(this);
            if(killed)
                return;
            if(model.getCurrentPlayer().equals(this)){
                ChineseCheckersState state = model.getState();
                if(state.getGameWinner() == null) {
                    MoveCommand move = monteCarlo.getPlay(state);
                    if(move != null) {
                        model.executeMove(move);
                    }
                }
            }
            model.notifyObservers();
            model.finishRobotTurn();

        }

    }

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
