package snek;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private List<Item> items;

    private int width;
    private int height;
    private PlayerSnake snek1;
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
        //TODO: wąż do dodania, zwróci 2
        //TODO: frog do dodania, zwróci 3
        return 0;
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
        //dodaj 10 kamieni
        for(int i = 0; i<10; i++){
            addItem(true);
        }
        //dodaj 5 owocków
        for(int i = 0; i<5; i++){
            addItem(false);
        }
    }

    private void generateSnek(){
        boolean found = false;
        while(!found){
             Random rand = new Random();
            int randomX = rand.nextInt((width));
            int randomY = rand.nextInt((height));
            if(isTaken(randomX, randomY) == 0){
                if(isTaken(randomX-1, randomY) == 0){
                    snek1 = new PlayerSnake(randomX, randomX-1, randomY, randomY);
                    found = true;
                }
            }
        }
    }

    public Board(int width, int height){
        items = new ArrayList<>();
        this.width = width;
        this.height = height;
        generateAllItems();
        generateSnek();
        for(Item i: items){
            System.out.print(i.getX());
            System.out.print(i.getY());
            System.out.println(i.getType());
        }
    }

    public Item getItem(int i){
        return items.get(i);
    }

    public SnakePart getSnakePart(int i){
        return snek1.getSnakePart(i);
    }

    public int getSnakeSize(int i){
        if(i==0){
            return snek1.getSnakeSize();
        }
        //TODO: dodać resztę sneków
        else{
            return 0;
        }
    }
}
