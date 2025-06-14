package snek;

import java.awt.Color;
import java.util.Vector;
/**
 * <p>Klasa odpowiedzialna za obsługę AI Snake'ów, implementująca interfejs Snake.</p>
 */
public class AISnake implements Snake{
    /**
     * <p>Poprzedni kierunek, w którym szedł Snake</p>
     */
    private Direction prev_dir;
    /**
     * <p>Czy ten snake jest martwy? Zmieniane, jeśli nastąpiła kolizja;
     *  jeśli jest prawdziwe, snake się już nie poruszy</p>
     */
    private boolean is_ded;
    /**
     * <p>Wektor zawierający segmenty snake'a</p>
     */
    private Vector<SnakePart> snek = new Vector<SnakePart>();
    /**
     * <p>Kolor głowy snake'a: jeśli nie jest martwy, pomarańczowy, jeśli jest martwy, czerwony</p>
     */
    private Color headColor;
    /**
     * <p>Kolor ciała snake'a: żółty</p>
     */
    private Color bodyColor;

    /**
     * <p>Konstruktor klasy AISnake. Podane muszą zostać parametry, które określą położenie snake'a
     *  na pozycji startowej. Na początku snake ma dwa segmenty. W konstruktorze są inicjowane także kolor głowy
     *  i ciała. Domyślnie zaczynamy skierowani w prawo, więc ruch poprzedni jest zapisany jako w prawo</p>
     * 
     * @param x1 położenie głowy w osi X
     * @param x2 położenie ogona w osi X
     * @param y1 położenie głowy w osi Y
     * @param y2 położenie ogona w osi Y
     */
    public AISnake(int x1, int x2, int y1, int y2){
        prev_dir = Direction.RIGHT; //w prawo
        is_ded = false;
        //board inicjalizuje sneka, przekazuje mu dwa obok siebie miejsca na planszy (x1>x2, y1=y2)
        snek.add(new SnakePart(x1, y1));
        snek.add(new SnakePart(x2, y2));
        headColor = new Color(225, 140, 0);
        bodyColor = Color.yellow;
    }

    @Override
    public void movePart(int pos) {
        if(pos == 0) return;
        snek.get(pos).setX(snek.get(pos-1).getX());
        snek.get(pos).setY(snek.get(pos-1).getY());
    }

    @Override
    public void addPart(int num) {
        for(int i = 0; i<num; i++){
            snek.add(new SnakePart(snek.getLast().getX(), snek.getLast().getY()));
        }
    }

    @Override
    public boolean checkIfHit(int x, int y) {
        for(int i = 0; i < snek.size(); i++){
            if(snek.get(i).getX() == x && snek.get(i).getY() == y){
                return true;
            }
        }
        return false;
    }


    /**
     * <p>Zbliżone do PlayerSnake, ale nie ma walidacji poprzedniego kierunku.</p>
     */
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
        //AI snake nie zrobi ruchu wbrew poprzedniemu kierunkowi
        prev_dir = dir;
        
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


    /**
     * <p>Prosty getter, umożliwiający sterowanie w boardzie</p>
     * @return zwracamy poprzedni kierunek
     */
    public Direction getPrev(){
        return prev_dir;
    }
    
}
