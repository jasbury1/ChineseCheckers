package checkers;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Heuristic {

    private static ReentrantLock LOCK = new ReentrantLock();

    private static Heuristic singletonHeuristic = null;

    private static HashMap<RankedPosition, Integer> blueValues;
    private static HashMap<RankedPosition, Integer> magentaValues;
    private static HashMap<RankedPosition, Integer> yellowValues;
    private static HashMap<RankedPosition, Integer> redValues;
    private static HashMap<RankedPosition, Integer> greenValues;
    private static HashMap<RankedPosition, Integer> cyanValues;

    private Heuristic(){
        blueValues = generateValues(new Point(0, -8));
        magentaValues = generateValues(new Point(-12, -4));
        yellowValues = generateValues(new Point(-12, 4));
        redValues = generateValues(new Point(0, 8));
        greenValues = generateValues(new Point(12, 4));
        cyanValues = generateValues(new Point(12, -4));

    }

    public static Heuristic getInstance() {
        LOCK.lock();
        try {
            if (singletonHeuristic == null) {
                singletonHeuristic = new Heuristic();
            }
            return singletonHeuristic;
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * Generates a list of point values
     * @param goal: The farthest point in the destination triangle
     */
    private static HashMap<RankedPosition, Integer> generateValues(Point goal){
        HashMap<RankedPosition, Integer> result = new HashMap<RankedPosition, Integer>();

        PriorityQueue<RankedPosition> bfsQueue = new PriorityQueue<>(100,
                (RankedPosition a, RankedPosition b) -> (a.value - b.value));

        RankedPosition start = new RankedPosition(goal.x, goal.y, 0);
        bfsQueue.add(start);
        result.put(start, start.value);

        while(!(bfsQueue.isEmpty())){
            RankedPosition pos = bfsQueue.poll();

            ArrayList<RankedPosition> neighbors = new ArrayList<>();
            neighbors.add(new RankedPosition(pos.x + 1, pos.y + 1, pos.value + 1));
            neighbors.add(new RankedPosition(pos.x + 1, pos.y - 1, pos.value + 1));
            neighbors.add(new RankedPosition(pos.x - 1, pos.y + 1, pos.value + 1));
            neighbors.add(new RankedPosition(pos.x - 1, pos.y - 1, pos.value + 1));
            neighbors.add(new RankedPosition(pos.x - 2, pos.y, pos.value + 1));
            neighbors.add(new RankedPosition(pos.x + 2, pos.y, pos.value + 1));

            for(RankedPosition neighbor : neighbors){
                if(result.get(neighbor) != null){
                    continue;
                }
                else if(BoardUtil.onBoard(new Point(neighbor.x, neighbor.y))){
                    bfsQueue.add(neighbor);
                    result.put(neighbor, neighbor.value);
                }
            }
            //++counter;
        }
        return result;
    }

    public static HashMap<RankedPosition, Integer> getMovesByColor(String color){
        if("blue".equals(color)) {
            return blueValues;
        } else if("magenta".equals(color)){
            return magentaValues;
        } else if("yellow".equals(color)){
            return yellowValues;
        } else if("red".equals(color)){
            return redValues;
        } else if("green".equals(color)){
            return greenValues;
        } else {
            return cyanValues;
        }
    }

}
