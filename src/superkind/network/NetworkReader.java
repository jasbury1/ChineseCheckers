package superkind.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import others.NetMonitor;


public class NetworkReader extends Thread {
    private Socket socket;
    private InputStream stream;
    private DataInputStream in;
    private ServerCommandParser parser;

    public NetworkReader(Socket socket, ServerCommandParser parser){
        this.socket = socket;
        this.parser = parser;
    }

    @Override
    public void run() {
        try{
            stream = NetMonitor.getInputStream(socket);
            in = new DataInputStream(stream);
        } catch(Exception e){
            e.printStackTrace();
        }
        readFromServer();
    }

    public void readFromServer(){
        while(!(Thread.currentThread().isInterrupted()) && !(socket.isClosed())){
            if(socket.isClosed()){
                break;
            }
            try {
                String command = in.readUTF();
                if("start".equals(command)){
                    parser.Start(in);
                }
                else if("ping".equals(command)){
                    parser.ping();
                }
                else if ("board".equals(command)){
                    parser.update(in);
                }

            }
            catch (Exception e){
                System.exit(0);
            }
        }
    }
}
