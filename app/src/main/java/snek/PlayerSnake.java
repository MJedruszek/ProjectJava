package snek;

import java.util.Vector;
import java.awt.Color;

public class PlayerSnake implements Snake{
    private Direction prev_dir;
    private boolean is_ded;
    private Vector<SnakePart> snek = new Vector<SnakePart>();
    private Color headColor;
    private Color bodyColor;

    public PlayerSnake(int x1, int x2, int y1, int y2){
        prev_dir = Direction.RIGHT; //w prawo
        is_ded = false;
        //board inicjalizuje sneka, przekazuje mu dwa obok siebie miejsca na planszy (x1>x2, y1=y2)
        snek.add(new SnakePart(x1, y1));
        snek.add(new SnakePart(x2, y2));
        headColor = Color.cyan;
        bodyColor = Color.pink;
    }

    @Override
    public void movePart(int pos) {
        if(pos == 0) return;
        //jeśli dodaliśmy trzy (po zjedzeniu żaby), to ostantnie kawałki, o umiejscowieniu takim, jak kolejny, zostają
        //if(snek.get(pos).getX() != snek.get(pos-1).getX() || snek.get(pos).getY() != snek.get(pos-1).getY()){
            snek.get(pos).setX(snek.get(pos-1).getX());
            snek.get(pos).setY(snek.get(pos-1).getY());
        //}
        
    }

    @Override
    public void addPart(int num) {
        for(int i = 0; i<num; i++){
            snek.add(new SnakePart(snek.getLast().getX(), snek.getLast().getY()));
        }
    }

    @Override
    public boolean checkIfHit(int x, int y) {
        boolean hit = false;
        for(int i = 0; i < snek.size(); i++){
            if(snek.get(i).getX() == x && snek.get(i).getY() == y){
                hit = true;
            }
        }
        return hit;
    }


    @Override
    public void move(Direction dir, int status) {
        //jeśli jesteśmy martwi, nie ruszamy się
        if(is_ded == true){
            return;
        } 
        //właśnie umarliśmy, nie ruszamy się
        if(status == 2){
            is_ded = true;
            headColor = Color.red;
            return;
        }

        //jeśli nie umarliśmy, próbujemy się przesunąć w kierunku dir
        //1. Czy ten kierunek jest przeciwny do poprzedniego? jeśli tak, nic nie zmieniamy
        if(
            (dir == Direction.RIGHT && prev_dir == Direction.LEFT) ||
            (dir == Direction.LEFT && prev_dir == Direction.RIGHT) ||
            (dir == Direction.UP && prev_dir == Direction.DOWN) ||
            (dir == Direction.DOWN && prev_dir == Direction.UP) 
        ){
            //nic nie robimy, kierunek niedozwolony
        }
        else{
            //kierunek dozwolony, idziemy tam
            prev_dir = dir;
        }
        //jeśli zjedliśmy owocek, +1
        if(status == 1){
            addPart(1);
        }
        if(status == 3){
            addPart(3);
        }
        //idziemy 1 do przodu
        update(prev_dir);
    }


    @Override
    public void update(Direction dir) {
        //od tyłu przesuwamy wszystkie party
        for(int i = snek.size()-1; i>0; i--){
            movePart(i);
        }
        //przesuwamy głowę
        snek.get(0).setX(snek.get(0).getX()+dir.getX());
        snek.get(0).setY(snek.get(0).getY()+dir.getY());
    }

    @Override
    public SnakePart getSnakePart(int i) {
        return snek.get(i);
    }

    @Override
    public int getSnakeSize() {
        return snek.size();
    }

    @Override
    public boolean getState() {
        return is_ded;
    }

    @Override
    public Color getColor(boolean head) {
        if(head){
            return headColor;
        }
        else{
            return bodyColor;
        }
    }

    @Override
    public void setStatus(boolean dead) {
        is_ded = dead;
    }

    
    
}
