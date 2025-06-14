package snek;

/**
 * Klasa przechowująca informacje o segmentach snake'a
 */
public class SnakePart {
    /**
     * Położenie segmentu na osi X
     */
    private int x;
    /**
     * Położenie segmentu na osi Y
     */
    private int y;

    /**
     * Konstruktor klasy, przypisujący wartości do pól
     * @param x Położenie segmentu na osi X
     * @param y Położenie segmentu na osi Y
     */
    public SnakePart(int x,int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Prosty getter pozycji
     * @return Pozycja na osi X
     */
    public int getX(){return x;}
    /**
     * Prosty setter pozycji
     * @param x Pozycja na osi X
     */
    public void setX(int x){this.x=x;}

    /**
     * Prosty getter pozycji
     * @return Pozycja na osi Y
     */
    public int getY(){return y;}
    /**
     * Prosty setter pozycji
     * @param y Pozycja na osi Y
     */
    public void setY(int y){this.y=y;}
}
