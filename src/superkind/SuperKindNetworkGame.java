package superkind;

import others.GameWindow;
import others.superkind.GameModel;
import superkind.network.MessageQueue;
import superkind.network.NetworkReader;
import superkind.network.NetworkWriter;
import superkind.network.ServerCommandParser;
import superkind.network.SuperKindNetworkModel;

import java.net.Socket;
import java.util.ArrayList;

public class SuperKindNetworkGame implements SuperKindGame{
    private double gameLengthSeconds;
    protected SuperKindNetworkModel gameModel;
    protected SuperKindViewer viewer;

    final String password = "0xe44f3484e89b0de6";
    private String host;
    private int port;
    private String sessionID;

    private Socket socket;
    private MessageQueue messageQueue;
    private NetworkWriter networkWriter;
    private NetworkReader networkReader;

    public SuperKindNetworkGame(double gameLengthSeconds, String host, int port, String sessionID){
        this.messageQueue = new MessageQueue();
        this.gameLengthSeconds = gameLengthSeconds;
        this.gameModel = new SuperKindNetworkModel(messageQueue);
        viewer = new SuperKindViewer(gameModel);

        this.host = host;
        this.port = port;
        this.sessionID = sessionID;
    }

    public static void loadImages(){
        SuperKindViewer.logo = GameWindow.loadImage("images/LACMTA-Logo.png");
        SuperKindViewer.rudeDude = GameWindow.loadImage("images/rude_dude.jpg");
        SuperKindViewer.superKind = GameWindow.loadImage("images/super_kind.jpg");
        SuperKindViewer.badtzMaru = GameWindow.loadImage("images/badtz_maru.png");
        SuperKindViewer.kibitzer = GameWindow.loadImage("images/kibitzer.jpg");
    }

    public void setupNetwork() throws Exception{
        socket = new Socket(host, port);
        networkWriter = new NetworkWriter(socket, messageQueue);
        networkReader = new NetworkReader(socket, new ServerCommandParser(gameModel, messageQueue));

        networkReader.start();
        networkWriter.start();

        ArrayList<String> startMessage = new ArrayList<>();
        startMessage.add("start");
        startMessage.add(password);
        startMessage.add("1");
        startMessage.add("1");
        startMessage.add(sessionID);
        startMessage.add(Integer.toString((int)gameLengthSeconds));
        messageQueue.submitMessage(startMessage);

    }

    @Override
    public boolean play() {
        try {
            setupNetwork();
        } catch(Exception e) {
            e.printStackTrace();
        }
        viewer.start();
        return true;
    }

    public GameModel getModel(){
        return gameModel;
    }
}
