package superkind.network;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class ServerCommandParser {
    private SuperKindNetworkModel model;
    private MessageQueue queue;

    public ServerCommandParser(SuperKindNetworkModel model, MessageQueue queue) {
        this.model = model;
        this.queue = queue;
    }

    public void Start(DataInputStream in){
        try {
            char player = in.readChar();
            model.setPlayerType(player);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void update(DataInputStream in) {
        try{
            String board = in.readUTF();
            model.updateBoard(board);
            char gameWinner = in.readChar();
            model.setWinner(gameWinner);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void ping(){
        ArrayList<String> command = new ArrayList<>();
        command.add("ping-ack");
        queue.submitMessage(command);
    }
}
