package checkers;

import others.GameWindow;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ChineseCheckersViewer extends JComponent implements CheckersObserver{
    String name;

    private ChineseCheckersModel model;
    private GameWindow window;
    private ChineseCheckersState state;

    static BufferedImage undo;
    static BufferedImage wood;

    public ChineseCheckersViewer(ChineseCheckersModel model, String name){
        this.name = name;
        this.model = model;
        WindowClosedRunnable closer = new WindowClosedRunnable(model, null, this);
        this.window = new GameWindow("Chinese Checkers: " + name, this, closer);
        closer.setWindow(this.window);
        init();
    }

    public void init(){
        this.setPreferredSize(new Dimension(400, 400));
        this.addMouseListener(new ClickHandler(model, this));

    }
    public void start(){
        window.start();
        update();
    }

    public void update(){
        this.repaint();
    }

    @Override
    public void paint(Graphics g){
        Dimension size = getSize();
        double componentH = size.getHeight();
        double componentW = size.getWidth();

        g.drawImage(wood, 0, 0, getWidth(), getHeight(), null);
        state = model.getState();


        //Draw Background and undo button
        String winner = state.getGameWinner();
        if(winner == null) {
            //Draw Some wood
            //Paint a border
            Color borderColor;
            if("blue".equals(state.getCurrentPlayer())){
                borderColor = Color.BLUE;
            } else if("magenta".equals(state.getCurrentPlayer())){
                borderColor = Color.MAGENTA;
            } else if("yellow".equals(state.getCurrentPlayer())){
                borderColor = Color.YELLOW;
            } else if("red".equals(state.getCurrentPlayer())){
                borderColor = Color.RED;
            } else if("green".equals(state.getCurrentPlayer())){
                borderColor = Color.GREEN;
            } else {
                borderColor = Color.CYAN;
            }
            LineBorder border = new LineBorder(borderColor, 5);
            border.paintBorder(this, g, 0, 0, getWidth(), getHeight());
        }
        else{
            int alpha = 127;
            if("blue".equals(winner)){
                g.setColor(new Color(0, 0, 255, alpha));
            } else if("magenta".equals(winner)){
                g.setColor(new Color(255, 0, 255, alpha));
            } else if("yellow".equals(winner)){
                g.setColor(new Color(255, 255, 0, alpha));
            } else if("red".equals(winner)){
                g.setColor(new Color(255, 0, 0, alpha));
            } else if("green".equals(winner)){
                g.setColor(new Color(0, 255, 0, alpha));
            } else {
                g.setColor(new Color(0, 255, 255, alpha));
            }
            g.fillRect(0, 0, size.width, size.height);

        }

        g.drawImage(undo, Math.min(getWidth(), getHeight()) /28, Math.min(getWidth(), getHeight()) /28,
                Math.min(getWidth(), getHeight())/6, Math.min(getWidth(), getHeight())/6,null);

        ChineseCheckersVisitor visitor = new ChineseCheckersVisitor((Graphics2D)g, componentH, componentW);
        state.accept(visitor);
    }

}
