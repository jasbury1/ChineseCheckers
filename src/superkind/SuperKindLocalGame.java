package superkind;

import others.GameWindow;
import others.superkind.BadtzPlayer;
import others.superkind.GameModel;
import superkind.local.SuperKindLocalModel;

public class SuperKindLocalGame implements SuperKindGame{
    protected double gameLengthSeconds;
    protected SuperKindLocalModel gameModel;
    protected SuperKindViewer viewer;

    public SuperKindLocalGame(double gameLengthSeconds){
        this.gameLengthSeconds = gameLengthSeconds;
        this.gameModel = new SuperKindLocalModel(gameLengthSeconds);
        viewer = new SuperKindViewer(gameModel);
    }

    public static void loadImages(){
        SuperKindViewer.logo = GameWindow.loadImage("images/LACMTA-Logo.png");
        SuperKindViewer.rudeDude = GameWindow.loadImage("images/rude_dude.jpg");
        SuperKindViewer.superKind = GameWindow.loadImage("images/super_kind.jpg");
        SuperKindViewer.badtzMaru = GameWindow.loadImage("images/badtz_maru.png");
    }

    public boolean play(){
        BadtzPlayer badtz = new BadtzPlayer(gameModel, gameLengthSeconds * .15);
        viewer.start();
        gameModel.start();
        return true;
    }

    public GameModel getModel(){
        return gameModel;
    }
}
