/**
 * An Array Queue of ArrayLists representing messages to be sent
 * Often the array list will be of length 1 representing a single command String
 * Longer Messages are handled by the receiver depending on the command
 */

package superkind.network;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MessageQueue {

    private Deque<ArrayList<String>> queue = new ArrayDeque<>();
    private ReentrantLock LOCK = new ReentrantLock();
    private Condition condition = LOCK.newCondition();

    public void submitMessage(ArrayList<String> command){
        LOCK.lock();
        try{
            queue.add(command);
            condition.signalAll();
        }
        finally{
            LOCK.unlock();
        }
    }

    public ArrayList<String> getMessage() throws InterruptedException {
        LOCK.lock();
        try{
            while(queue.size() == 0){
                condition.await();
            }
            return queue.remove();
        }
        finally{
            LOCK.unlock();
        }
    }

}
