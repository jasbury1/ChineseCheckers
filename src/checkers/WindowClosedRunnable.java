package checkers;

import others.GameWindow;

import java.awt.*;

public class WindowClosedRunnable implements Runnable {
    private ChineseCheckersModel model;
    private GameWindow window;
    private ChineseCheckersViewer viewer;

    public WindowClosedRunnable(ChineseCheckersModel model, GameWindow window, ChineseCheckersViewer viewer){
        this.model = model;
        this.window = window;
        this.viewer = viewer;
    }
    @Override
    public void run(){
        model.detach(viewer);
        window.finish();
    }

    public void setWindow(GameWindow newWindow){
        this.window = newWindow;
    }
}
