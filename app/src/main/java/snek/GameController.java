package snek;
/**
 * Kontroler, przechowujący informacje o rozmiarze planszy, samą planszę, oraz stan gry
 */
public class GameController {
    /**
     * AKtualny stan gry
     */
    private GameState state;
    /**
     * Plansza
     */
    private Board b;
    /**
     * Rozmiar planszy wyrażony w liczbie pól
     */
    private int w,h;

    /**
     * Prosty konstruktor, ustawiający wartości na domyślne oraz inicjalizujący boarda
     */
    public GameController(){
        w = 64;
        h = 32;
        state = GameState.BEGGINING;
        b = new Board(w,h, 0);
    }

    /**
     * Prosty setter, umożliwiający aktualizację stanu gry
     * @param s Stan, który chcemy ustawić
     */
    public void setState(GameState s){
        this.state = s;
    }

    /**
     * Prosty getter, umożliwiający pobranie informacji o stanie gry
     * @return Stan gry
     */
    public GameState getState(){
        return state;
    }

    /**
     * Prosty getter, umożliwiający pozostałym klasom korzystanie z boarda
     * @return Plansza
     */
    public Board getBoard(){
        return b;
    }
}
