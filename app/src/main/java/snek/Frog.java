package snek;

import java.awt.Color;

public class Frog {
    //miejsce, gdzie teraz przebywa żaba
    private int x;
    private int y;
    private Color c;
    private boolean is_ded;

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public Color getColor(){
        return c;
    }

    public Frog(int x, int y){
        this.x = x;
        this.y = y;
        this.c = new Color(153, 0, 204);
        is_ded = false;
    }

    public boolean getStatus(){
        return is_ded;
    }

    public void setStatus(boolean s){
        is_ded = s;
    }
}
