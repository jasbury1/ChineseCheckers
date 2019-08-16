package superkind.local;

public class EventGenerator implements Runnable{
    private SuperKindLocalModel model;

    public EventGenerator(SuperKindLocalModel model){
        this.model = model;
    }

    @Override
    public void run(){
        if(model.getRudeDudeQueue().isEmpty()){
            model.setGameOver();
            //window.finish();
        }
        else{
            GameTile tile = model.getRudeDudeQueue().peek();
            model.setToRudeDude(tile.getX(), tile.getY());
        }
    }
}
