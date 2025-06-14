package snek;

import java.awt.Color;

/**
 * Żaba, same zmienne, bez AI
 */
public class Frog {
    /**
     * Miejsce na osi X, gdzie znajduje się żaba
     */
    private int x;
    /**
     * Miejsce na osi Y, gdzie znajduje się żaba
     */
    private int y;
    /**
     * Kolor żaby
     */
    private Color c;
    /**
     * Czy żaba jest martwa, jeśli tak, true, jeśli nie, false
     */
    private boolean is_ded;

    /**
     * Prosty setter, ustawia żabę na zadanym miejscu na osi X
     * @param x Położenie na osi X
     */
    public void setX(int x){
        this.x = x;
    }

    /**
     * Prosty setter, ustawia żabę na zadanym miejscu na osi Y
     * @param y Położenie na osi Y
     */
    public void setY(int y){
        this.y = y;
    }

    /**
     * Prosty getter, do miejsca na osi X
     * @return Aktualne położenie żaby na osi X
     */
    public int getX(){
        return x;
    }

    /**
     * Prosty getter, do miejsca na osi Y
     * @return Aktualne położenie żaby na osi Y
     */
    public int getY(){
        return y;
    }

    /**
     * Prosty getter, do wyświetlania
     * @return Kolor żaby
     */
    public Color getColor(){
        return c;
    }

    /**
     * Prosty kontruktor, ustalający wartości zmiennych wewnętrznych
     * @param x Położenie na osi X
     * @param y Położenie na osi Y
     */
    public Frog(int x, int y){
        this.x = x;
        this.y = y;
        this.c = new Color(153, 0, 204);
        is_ded = false;
    }

    /**
     * Prosty getter, do informacji o tym, czy żaba jest martwa, czy nie
     * @return True, jeśli żaba jest martwa, false, jeśli nie
     */
    public boolean getStatus(){
        return is_ded;
    }

    /**
     * Prosty setter do sterowania stanem żaby
     * @param s True, jeśli żaba ma umrzeć, false, jeśli ma ożyć
     */
    public void setStatus(boolean s){
        is_ded = s;
    }
}
