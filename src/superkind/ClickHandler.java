package superkind;

import others.superkind.GameModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.locks.ReentrantLock;

import others.Size;
import superkind.network.SuperKindNetworkModel;

public class ClickHandler extends MouseAdapter{
    private GameModel model;
    private SuperKindViewer viewer;
    private ReentrantLock LOCK = new ReentrantLock();

    public ClickHandler(GameModel model, SuperKindViewer viewer){
        this.model = model;
        this.viewer = viewer;
    }

    @Override
    public void mouseReleased(MouseEvent e){
        Size gameSize = model.getBoardSize();
        int x = (e.getX() / (viewer.getSize().width / gameSize.width));
        int y = (e.getY() / (viewer.getSize().height / gameSize.height));
        if (model instanceof SuperKindNetworkModel) {
            if (((SuperKindNetworkModel) model).getPlayerType() == 'o') {
                return;
            }
            if (((SuperKindNetworkModel) model).getPlayerType() == 'b') {
                model.setToBadtzMaru(x, y);
                return;
            }
        }
        model.setToSuperKind(x, y);
    }

}
