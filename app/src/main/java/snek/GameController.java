package snek;

public class GameController {
    private GameState state;

    public GameController(){
        state = GameState.BEGGINING;
    }

    public void setState(GameState s){
        this.state = s;
    }

    public GameState getState(){
        return state;
    }
}
