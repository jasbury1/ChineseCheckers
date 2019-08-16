package checkers;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.Math;

public class ClickHandler extends MouseAdapter{
    private ChineseCheckersModel model;
    private ChineseCheckersViewer component;
    private Command command;

    public ClickHandler(ChineseCheckersModel model, ChineseCheckersViewer component){
        this.model = model;
        this.component = component;
    }

    @Override
    public void mouseReleased(MouseEvent e){
        int xS = e.getX();
        int yS = e.getY();

        int undoRadius = (int)((.5) * (Math.min(component.getWidth(), component.getHeight())/6));
        int undoCoord = Math.min(component.getWidth(), component.getHeight()) /28 + undoRadius;

        //CHECK FOR UNDO
        int distance = (int)(Math.sqrt((yS - undoCoord) * (yS - undoCoord) + (xS - undoCoord) * (xS - undoCoord)));
        if(distance < undoRadius){
            command = new MoveCommand(model, null, null);
            command.undo();
            return;
        }

        //If you are a robot or the game is over, you can't move!
        if(model.getCurrentPlayer() instanceof Robot || model.getState().getGameWinner() != null){
            return;
        }

        double unitR = 1 / (32 * Math.cos(Math.PI / 6) + 2);

        double wS = component.getWidth();
        double hS = component.getHeight();
        double minS = Math.min(wS, hS);
        double xn = 0.5 + (xS - 0.5 * wS) / minS;
        double yn = 0.5 - (yS - 0.5 * hS) / minS;
        double xi = (xn - 0.5) / unitR;
        double yi = (yn - 0.5) / (unitR * Math.tan(Math.PI/3));
        int xii;
        int yii;
        yii = (int) Math.round(yi);
        if (yii % 2 == 0) {
            xii = 2 * (int) Math.round(xi / 2);
        } else {
            xii = 1 + 2 * (int) Math.round((xi - 1) / 2);
        }
        if(model.findOccupant(new Point(xii, yii)).equals(model.getCurrentPlayer().getColor())){
            command = new SelectCommand(model, new Point(xii, yii), model.getCurrentPlayer());
        }
        else{
            command = new MoveCommand(model, new Point (xii, yii), model.getSelectedPoint());
        }
        command.execute();
    }

}
