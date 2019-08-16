
package others.superkind;

/**
 * An immutable snapshot of the superkind game state at one instant in
 * time.
 */
public interface GameState {

    /**
     * Visit all the squares in the game board
     */
    public void accept(BoardVisitor v);

    /**
     * Returns true iff the game is over
     */
    public boolean getGameOver();

    /**
     * Returns the winner of the game:  ' ' for no winner yet,
     * 'b' for Badtz, 's' for Super Kind, 't' for tie, 'r' for Rude Dude
     */
    public char getGameWinner();
}
