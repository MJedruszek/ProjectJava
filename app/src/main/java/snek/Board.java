package snek;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private List<Item> items;

    private int width;
    private int height;
    private PlayerSnake snek1;
    private AISnake snek2;
    private AISnake snek3;
    private Direction prev_dir;

    private int isTaken(int x, int y){
        for(Item i:  items){
            if(i.getX() == x && i.getY() == y){
                if(i.getType()){
                    return 2; //kamień zwraca 2
                }
                else{
                    return 1; //owocek 1
                }
            }
        }

        if(snek1 != null && snek1.checkIfHit(x, y)){
            return 2;
        }

        if(snek2 != null && snek2.checkIfHit(x, y)){
            return 2;
        }

        if(snek3 != null && snek3.checkIfHit(x, y)){
            return 2;
        }

        if(x>width-1 || x<0 || y>height-1 || y<0){
            return 2;
        }
        //TODO: frog do dodania, zwróci 3
        return 0;
    }

    private int findFruitByPosition(int x, int y){
        for(int i = 10; i<15; i++){
            if(items.get(i).getX() == x && items.get(i).getY() == y){
                return i;
            }
        }
        return -1;
    }

    private int snakeGo(Direction dir, int s){
        int result = 0;
        if(
            (dir == Direction.RIGHT && prev_dir == Direction.LEFT) ||
            (dir == Direction.LEFT && prev_dir == Direction.RIGHT) ||
            (dir == Direction.UP && prev_dir == Direction.DOWN) ||
            (dir == Direction.DOWN && prev_dir == Direction.UP) 
        ){
            //nic nie robimy, kierunek niedozwolony
        }
        else{
            prev_dir = dir;
        }
        if(s == 0){
            //snake gracza
            //znajdź nowe koordynaty sneka
            int newx = snek1.getSnakePart(0).getX() + prev_dir.getX();
            int newy = snek1.getSnakePart(0).getY() + prev_dir.getY();
            //sprawdź, co na nich jest, i zwróć to
            result= isTaken(newx, newy);
        }
        else{
            //TODO: dodać case'y do pozostałych sneków
            return 0;
        }
        //jeśli zjedliśmy owoc, dodaj nowy owoc
        if(result == 1){
            int fruit = findFruitByPosition(snek1.getSnakePart(0).getX() + prev_dir.getX(), snek1.getSnakePart(0).getY() + prev_dir.getY());
            boolean found = false;
            while(!found){
                if(fruit == -1){
                    //the fruit is a lie
                    found = true;
                }
                Random rand = new Random();
                int randomX = rand.nextInt((width));
                int randomY = rand.nextInt((height));
                if(isTaken(randomX, randomY) == 0){
                    //znaleźliśmy dla niego miejsce, wsadzamy go tam
                    found = true;
                    items.get(fruit).setPosition(randomX, randomY);
            }

            }
        }
        return result;
    }

    private void addItem(boolean type){
        boolean found = false;
        while(!found){
            Random rand = new Random();
            int randomX = rand.nextInt((width));
            int randomY = rand.nextInt((height));
            if(isTaken(randomX, randomY) == 0){
                found = true;
                items.add(new Item(randomX, randomY, type));
            }
        }
    }

    private void generateAllItems(){
        items.clear();
        //dodaj 10 kamieni
        for(int i = 0; i<10; i++){
            addItem(true);
        }
        //dodaj 5 owocków
        for(int i = 0; i<5; i++){
            addItem(false);
        }
    }

    private void generateSnek(int i){
        boolean found = false;
        while(!found){
             Random rand = new Random();
            int randomX = rand.nextInt(3, (width - 3));
            int randomY = rand.nextInt(3, (height - 3));
            if(isTaken(randomX, randomY) == 0){
                if(isTaken(randomX-1, randomY) == 0){
                    if(i == 0){
                        snek1 = new PlayerSnake(randomX, randomX-1, randomY, randomY);
                    }
                    else if(i==1){
                        snek2 = new AISnake(randomX, randomX-1, randomY, randomY);
                    }

                    else if(i==2){
                        snek3 = new AISnake(randomX, randomX-1, randomY, randomY);
                    }
                    
                    found = true;
                }
            }
        }
    }

    public Board(int width, int height, int s){
        items = new ArrayList<>();
        this.width = width;
        this.height = height;
        generateAllItems();
        prev_dir = Direction.RIGHT;
        generateSnek(0);
        snek2 = null;
        snek3 = null;
        if(s == 0){
            return;
        }
        generateSnek(1);
        if(s == 1){
            return;
        }
        generateSnek(2);
        
    }

    public Item getItem(int i){
        return items.get(i);
    }

    public SnakePart getSnakePart(int i, int s){
        if(s == 0){
            return snek1.getSnakePart(i);
        }
        else if(s == 1){
            return snek2.getSnakePart(i);
        }
        else if(s==2){
            return snek3.getSnakePart(i);
        }
        else{
            return snek1.getSnakePart(0);
        }

        
    }

    public int getSnakeSize(int i){
        if(i==0){
            return snek1.getSnakeSize();
        }
        else if(i==1){
            return snek2.getSnakeSize();
        }
        else if(i==2){
            return snek3.getSnakeSize();
        }
        else{
            return 0;
        }
    }

    public boolean getSnakeStatus(int s){
        if(s==0){
            return snek1.getState();
        }
        else if(s==1){
            return snek2.getState();
        }
        else if(s==2){
            return snek3.getState();
        }
        else{
            return false;
        }
    }

    public void update(){
        
    }

    public void updatePlayerSnake(Direction dir){
        int next = snakeGo(dir, 0); //sprawdzamy dla sneka gracza
        snek1.move(dir, next); //snek idzie
    }

    public void reset(int i){
        prev_dir = Direction.RIGHT;
        generateAllItems();
        generateSnek(0);
        snek2 = null;
        snek3 = null;
        if(i == 0){
            return;
        }
        generateSnek(1);
        if(i == 1){
            return;
        }
        generateSnek(2);
        
        
    }

    public Color getSnakeColor(int i, boolean head){
        if(i == 0){
            return snek1.getColor(head);
        }
        else if (i==1){
            return snek2.getColor(head);
        }
        else if(i==2){
            return snek3.getColor(head);
        }
        else{
            return Color.blue;
        }
    }
}
