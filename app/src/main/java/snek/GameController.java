package snek;

public class GameController {
    private GameState state;
    private Board b;
    private int w,h;

    public GameController(){
        w = 64;
        h = 32;
        state = GameState.BEGGINING;
        b = new Board(w,h);
    }

    public void setState(GameState s){
        this.state = s;
    }

    public GameState getState(){
        return state;
    }

    public Board getBoard(){
        return b;
    }
}
