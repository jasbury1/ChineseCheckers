package superkind.local;

import others.superkind.BoardVisitor;
import others.superkind.GameState;

public class SuperKindLocalState implements GameState{
    private boolean gameOver;
    private GameTile[][] tiles;

    public SuperKindLocalState(boolean gameOver, GameTile[][] tiles){
        this.gameOver = gameOver;
        this.tiles = tiles;
    }

    /**
     * Visit all the squares in the game board
     */
    public void accept(BoardVisitor v){
        for(int i = 0; i < tiles.length; ++i) {
            for(int j = 0; j < tiles[0].length; j++) {
                if(("rudeDude".equals(tiles[i][j].getTileType())) )
                    v.visitRudeDude(i, j);
                else if(("superKind".equals(tiles[i][j].getTileType())) )
                    v.visitSuperKind(i, j);
                else if(("badtzMaru".equals(tiles[i][j].getTileType())) )
                    v.visitBadtzMaru(i, j);
                else{
                    v.visitBackground(i, j);
                }
            }
        }
    }

    /**
     * Returns true iff the game is over
     */
    public boolean getGameOver(){
        return gameOver;
    }

    public char getGameWinner(){
        if(!gameOver){
            return ' ';
        }
        else{
            return 's';
        }
    }
}
