package superkind;

import others.GameWindow;
import others.superkind.GameModel;
import others.superkind.GameObserver;
import others.superkind.GameState;
import superkind.network.SuperKindNetworkModel;
import superkind.local.SuperKindLocalModel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SuperKindViewer extends JComponent implements GameObserver {

    private GameModel model;
    private GameWindow window;
    private GameState state;

    static BufferedImage logo;
    static BufferedImage rudeDude;
    static BufferedImage superKind;
    static BufferedImage badtzMaru;
    static BufferedImage kibitzer;

    public SuperKindViewer(GameModel model){
        this.model = model;
        init();
        this.window = new GameWindow("Superkind ft. バッドばつ丸", this, null);
    }

    public void start(){
        window.start();
        update();
    }

    public void update(){
        /*
        if(model instanceof SuperKindNetworkModel) {
            if (((SuperKindNetworkModel) model).getGameOver()) {
                window.finish();
            }
        }
        else if (model instanceof SuperKindLocalModel){
            if (((SuperKindLocalModel) model).getGameOver()) {
                window.finish();
            }
        }
        */
        if(state == null){
            state = model.getState();
        }
        if(state.getGameOver()){
            window.finish();
        }
        this.repaint();
    }

    //Do any initializations here!
    public void init(){
        this.setPreferredSize(new Dimension(600, 500));
        this.addMouseListener(new ClickHandler(model, this));
        model.attach(this);
    }

    //Called by the system
    @Override
    public void paint(Graphics g){

        if(state == null) {
            state = model.getState();
        }
        GameTileVisitor visitor = new GameTileVisitor(g, getSize());
        state.accept(visitor);
        state = null;

        //Draw the player type in the upper left corner
        if(model instanceof SuperKindNetworkModel){
            char player = ((SuperKindNetworkModel) model).getPlayerType();
            int drawWidth = (getSize().width / 12);
            int drawHeight = (getSize().height / 10);
            if(player == 'o') {
                g.drawImage(SuperKindViewer.kibitzer, 0, 0, drawWidth, drawHeight, null);
            } else if(player == 's'){
                g.drawImage(SuperKindViewer.superKind, 0, 0, drawWidth, drawHeight, null);

            } else if(player == 'b') {
                g.drawImage(SuperKindViewer.badtzMaru, 0, 0, drawWidth, drawHeight, null);
            }
        }

    }
}
