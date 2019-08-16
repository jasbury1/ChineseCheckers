package superkind;

import others.superkind.BoardVisitor;

import java.awt.*;

public class GameTileVisitor implements BoardVisitor {
    private Graphics g;
    private Dimension windowSize;

    public GameTileVisitor(Graphics g, Dimension windowSize){
        this.g = g;
        this.windowSize = windowSize;
    }

    public void visitRudeDude(int x, int y){
        int drawWidth = (windowSize.width / 6);
        int drawHeight = (windowSize.height / 5);
        g.drawImage(SuperKindViewer.rudeDude, x * drawWidth, y * drawHeight, drawWidth, drawHeight, null);
    }

    public void visitSuperKind(int x, int y){
        int drawWidth = (windowSize.width / 6);
        int drawHeight = (windowSize.height / 5);
        g.drawImage(SuperKindViewer.superKind, x * drawWidth, y * drawHeight, drawWidth, drawHeight, null);
    }

    public void visitBadtzMaru(int x, int y){
        int drawWidth = (windowSize.width / 6);
        int drawHeight = (windowSize.height / 5);
        g.drawImage(SuperKindViewer.badtzMaru, x * drawWidth, y * drawHeight, drawWidth, drawHeight, null);
    }

    public void visitBackground(int x, int y){
        int drawWidth = (windowSize.width / 6);
        int drawHeight = (windowSize.height / 5);
        g.drawImage(SuperKindViewer.logo, x * drawWidth, y * drawHeight, drawWidth, drawHeight, null);
    }
}
