package snek;
/**
 * Klasa odpowiedzialna za przechowywanie informacji o elementach planszy
 */
public class Item {
    /**
     * Położenie obiektu na osi X
     */
    private int x;
    /**
     * Położenie obiektu na osi Y
     */
    private int y;
    /**
     * Zmienna przechowująca informację o typie obiektu
     */
    private boolean isRock;

    /**
     * Konstruktor klasy, przypisujący do pól odpowiednie wartości
     * @param x Położenie obiektu na osi X
     * @param y Położenie obiektu na osi Y
     * @param isRock Czy obiekt jest kamieniem? Jeśli true, kamień, jeśli false, jabłko
     */
    public Item(int x, int y, boolean isRock){
        this.x = x;
        this.y = y;
        this.isRock = isRock;
    }

    /**
     * Prosty getter do położenia
     * @return Położenie obiektu na osi X
     */
    public int getX(){
        return x;
    }

    /**
     * Prosty getter do położenia
     * @return Położenie obiektu na osi Y
     */
    public int getY(){
        return y;
    }

    /**
     * Prosty getter typu
     * @return True, jeśli kamień, false, jeśli jabłko
     */
    public boolean getType(){
        return isRock;
    }

    /**
     * Prosty setter do położenia
     * @param x Żądane położenie na osi X
     * @param y Żądane położenie na osi Y
     */
    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }
}
