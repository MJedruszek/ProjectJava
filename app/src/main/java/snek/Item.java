package snek;

public class Item {
    private int x;
    private int y;
    private boolean isRock;

    public Item(int x, int y, boolean isRock){
        this.x = x;
        this.y = y;
        this.isRock = isRock;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public boolean getType(){
        return isRock;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }
}
