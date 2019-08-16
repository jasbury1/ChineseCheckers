package checkers;

public class RankedPosition {
    public final int x;
    public final int y;
    public final int value;

    public RankedPosition(int x, int y, int value){
        this.x = x;
        this.y = y;
        this.value = value;
    }

    @Override
    public String toString(){
        return "[ " + x + ", " + y + ", " + value + "]";
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;

    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof RankedPosition))
            return false;
        RankedPosition p = (RankedPosition) o;
        return p.x == x && p.y == y;
    }
}