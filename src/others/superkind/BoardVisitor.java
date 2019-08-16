
package others.superkind;

/**
 * An interface used to visit all the squares on the board.
 * Coordinates are zero-based.  Each square on the board holds
 * either Rude Dude, Super Kind, Badtz Maru or the background.
 */
public interface BoardVisitor {

    public void visitRudeDude(int x, int y);

    public void visitSuperKind(int x, int y);

    public void visitBadtzMaru(int x, int y);

    public void visitBackground(int x, int y);

}
