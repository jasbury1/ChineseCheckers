
package others.superkind;

import others.Size;

public interface GameModel  {

    /**
     * Attach an observer.  If the same observer is already attached, the
     * semantics of this method are undefined.
     */
    public void attach(GameObserver o);

    /**
     * Detach an observer.  Does nothing if that observer isn't present.
     */
    public void detach(GameObserver o);

    /**
     * Set the given square to contain Super Kind, if this is valid.  Does nothing
     * if x or y are out of range, or if the square doesn't contain Rude Dude.
     */
    public void setToSuperKind(int x, int y);

    /**
     * Set the given square to contain Badtz Maru, if this is valid.  Does nothing
     * if x or y are out of range, or if the square doesn't contain Rude Dude.
     */
    public void setToBadtzMaru(int x, int y);

    /**
     * Get a snapshot the current state of play at this moment in time.
     */
    public GameState getState();

    /**
     * Get the size of the board.  This never changes.
     */
    public Size getBoardSize();
}
