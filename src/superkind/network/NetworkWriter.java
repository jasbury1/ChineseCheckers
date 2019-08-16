package superkind.network;

import others.NetMonitor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class NetworkWriter extends Thread {
    private Socket socket;
    private MessageQueue queue;
    private OutputStream stream;
    private DataOutputStream out;

    public NetworkWriter(Socket socket, MessageQueue queue){
        this.socket = socket;
        this.queue = queue;
    }

    @Override
    public void run(){
        try{
            stream = NetMonitor.getOutputStream(socket);
            out = new DataOutputStream(stream);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        while(!(Thread.currentThread().isInterrupted()) && !(socket.isClosed())){
            try {
                ArrayList<String> message = queue.getMessage();
                parseMessage(message);

                out.flush();
                stream.flush();

            } catch(Exception e){
                e.printStackTrace();
                System.err.println("Error in parsing message");
            }
        }
    }

    public void parseMessage(ArrayList<String> message){

        if("occupy".equals(message.get(0))) {
            try {
                char player = message.get(1).charAt(0);
                int x = Integer.parseInt(message.get(2));
                int y = Integer.parseInt(message.get(3));

                out.writeUTF(message.get(0));
                out.writeChar(player);
                out.writeInt(x);
                out.writeInt(y);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error in Occupy message");
            }
        }
        else if ("start".equals(message.get(0))){
            try{
                int protocolVersion = Integer.parseInt(message.get(2));
                int debugMode = Integer.parseInt(message.get(3));
                String sessionID = message.get(4);
                int gameLength = Integer.parseInt(message.get(5));

                out.writeLong(0xe44f3484e89b0de6L);
                out.writeInt(protocolVersion);
                out.write(debugMode);
                out.writeUTF(sessionID);
                out.writeInt(gameLength);

            } catch (Exception e){
                e.printStackTrace();
                System.err.println("Error in Start message");
            }
        }
        else if("end".equals(message.get(0))){
            try {
                out.writeUTF(message.get(0));
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                //Write the command
                out.writeUTF(message.get(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
