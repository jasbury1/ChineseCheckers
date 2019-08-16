package checkers;

import java.awt.*;

public class MoveCommand implements Command {
    private ChineseCheckersModel model;
    private Point to;
    private Point from;

    //test
    public MoveCommand(ChineseCheckersModel model, Point to, Point from){
        this.model = model;
        this.to = to;
        this.from = from;
    }

    public void execute(){
        if("".equals(model.findOccupant(to)) && BoardUtil.onBoard(to) && model.getSelectedPoint() != null) {
            model.executeMove(this);
            model.notifyObservers();
        }
    }

    public void undo(){
        model.executeUndo();
        model.notifyObservers();
    }

    public Point getTo(){
        return to;
    }

    public Point getFrom(){
        return from;
    }
}
