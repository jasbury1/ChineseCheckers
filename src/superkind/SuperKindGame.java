package superkind;

import others.GameWindow;
import others.superkind.BadtzPlayer;
import others.superkind.GameModel;

public interface SuperKindGame {


    GameModel getModel();

    boolean play();
}
