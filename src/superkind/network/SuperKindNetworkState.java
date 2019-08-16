package superkind.network;

import others.superkind.BoardVisitor;
import others.superkind.GameState;

public class SuperKindNetworkState implements GameState {
    private boolean gameOver;
    private String board;
    private char winner;

    public SuperKindNetworkState(boolean gameOver, String board, char winner){
        this.gameOver = gameOver;
        this.board = board;
        this.winner = winner;
    }
    public void accept(BoardVisitor v){
        for (int i = 0; i < 30; i++){
            int x = (int)(i % 6);
            int y = (int)(i / 6);
            if('r' == board.charAt(i))
                v.visitRudeDude(x, y);
            else if('s' == board.charAt(i))
                v.visitSuperKind(x, y);
            else if('b' == board.charAt(i))
                v.visitBadtzMaru(x, y);
            else{
                v.visitBackground(x, y);
            }
        }
    }

    /**
     * Returns true iff the game is over
     */
    public boolean getGameOver(){
        return gameOver;
    }

    /**
     * Returns the winner of the game:  ' ' for no winner yet,
     * 'b' for Badtz, 's' for Super Kind, 't' for tie, 'r' for Rude Dude
     */
    public char getGameWinner(){
        return winner;
    }
}
