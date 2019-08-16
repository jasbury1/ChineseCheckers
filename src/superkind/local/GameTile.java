package superkind.local;

public class GameTile {
    protected String tileType;
    protected int x;
    protected int y;
    private double changeTime;

    public GameTile(String tileType, int x, int y, double changeTime){
        this.tileType = tileType;
        this.x = x;
        this.y = y;
        this.changeTime = changeTime;
    }


    //GETTERS AND SETTERS

    public String getTileType(){
        return tileType;
    }

    public void setTileType(String newTileType){
        this.tileType = newTileType;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public double getChangeTime(){
        return changeTime;
    }
}
