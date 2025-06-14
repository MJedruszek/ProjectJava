package snek;
/**
 * Enum służący do obsługi kierunków
 */
public enum Direction {
    /**
     * Kierunki mają z góry ustalone x i y, o które przesuną element
     */
    RIGHT(1,0), LEFT(-1,0), UP(0,-1), DOWN(0,1);

    /**
     * Zmienne przechowujące odległość, o którą przemieszczą element
     */
    private int x,y;

    /**
     * <p>Konstruktor, który ustala odległości x i y, prywatny, ponieważ dzieje się to automatycznie
     * dla każego kierunku</p>
     * @param x Odległość na osi X, o którą przesunie ten kierunek
     * @param y Odległość na osi Y, o którą przesunie ten kierunek
     */
    private Direction(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Prosty getter, umożliwiający prostsze przesuwanie elementów na planszy
     * @return Odległość na osi X, o którą przesunie ten kierunek
     */
    public int getX(){return x;}
    /**
     * Prosty getter, umożliwiający prostsze przesuwanie elementów na planszy
     * @return Odległość na osi Y, o którą przesunie ten kierunek
     */
    public int getY(){return y;}

}
