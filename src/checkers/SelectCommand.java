package checkers;

import java.awt.*;

public class SelectCommand implements Command {
    private ChineseCheckersModel model;
    private Point selected;
    private GameParticipant participant;

    public SelectCommand(ChineseCheckersModel model, Point selected, GameParticipant participant){
        this.model = model;
        this.selected = selected;
        this.participant = participant;
    }

    public void execute(){
        //Make sure they clicked on their own player first
        if(BoardUtil.onBoard(selected) && participant.getColor().equals(model.findOccupant(selected))){
            model.executeSelect(selected);
            model.notifyObservers();
        }

    }
    public void undo(){
        return;
    }
}
