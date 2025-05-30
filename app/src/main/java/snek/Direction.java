package snek;

public enum Direction {
    RIGHT(1,0), LEFT(-1,0), UP(0,-1), DOWN(0,1);

    private int x,y;

    private Direction(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){return x;}
    public int getY(){return y;}

}
