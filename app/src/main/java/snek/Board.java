package snek;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Board {
    private List<Item> items;

    private int width;
    private int height;
    private PlayerSnake snek1;
    private AISnake snek2;
    private AISnake snek3;
    private Direction prev_dir;
    private Direction new_dir;
    private Thread[] t;
    private Frog forg;
    private Point[] next_positions;
    private Point[] past_tail_positions;
    private CyclicBarrier barrier;
    private CyclicBarrier finished_barrier;
    private boolean shouldWork;

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
        if(forg != null && x == forg.getX() && y == forg.getY()){
            return 3;
        }
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
        
        if(s == 0){
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
            //snake gracza
            //znajdź nowe koordynaty sneka
            int newx = snek1.getSnakePart(0).getX() + prev_dir.getX();
            int newy = snek1.getSnakePart(0).getY() + prev_dir.getY();
            //sprawdź, co na nich jest, i zwróć to
            result= isTaken(newx, newy);
            
        }
        else if(s == 1){
            //te sneki są mądre, nie muszą się martwić prev_dir
            int newx = snek2.getSnakePart(0).getX() + dir.getX();
            int newy = snek2.getSnakePart(0).getY() + dir.getY();

            result = isTaken(newx, newy);
        }
        else if(s==2){
            //te sneki są mądre, nie muszą się martwić prev_dir
            int newx = snek3.getSnakePart(0).getX() + dir.getX();
            int newy = snek3.getSnakePart(0).getY() + dir.getY();

            result = isTaken(newx, newy);
        }
        //jeśli zjedliśmy owoc, dodaj nowy owoc
        if(result == 1){
            int fruit = 0;
            if(s==0){
                fruit = findFruitByPosition(snek1.getSnakePart(0).getX() + prev_dir.getX(), snek1.getSnakePart(0).getY() + prev_dir.getY());
            }
            else if(s==1){
                fruit = findFruitByPosition(snek2.getSnakePart(0).getX() + dir.getX(), snek2.getSnakePart(0).getY() + dir.getY());
            }
            else if(s==2){
                fruit = findFruitByPosition(snek3.getSnakePart(0).getX() + dir.getX(), snek3.getSnakePart(0).getY() + dir.getY());
            }
            
            boolean found = false;
            while(!found){
                if(fruit == -1){
                    //the fruit is a lie
                    found = true;
                    return 0;
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
        if(result == 3) forg.setStatus(true); //żaba umarła
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

    private void generateFrog(){
        boolean found = false;
        while(!found){
            Random rand = new Random();
            int randomX = rand.nextInt((width));
            int randomY = rand.nextInt((height));
            if(isTaken(randomX, randomY) == 0){
                found = true;
                forg = new Frog(randomX, randomY);
            }
        }
    }

    public Board(int width, int height, int s){
        shouldWork = false;
        items = new ArrayList<>();
        barrier = new CyclicBarrier(3);
        finished_barrier = new CyclicBarrier(4);
        this.width = width;
        this.height = height;
        generateAllItems();
        prev_dir = Direction.RIGHT;
        generateSnek(0);
        snek2 = null;
        snek3 = null;
        generateFrog();
        next_positions = new Point[3];
        for(int j = 0; j<3; j++){
            next_positions[j] = new Point(-2,-2);
        }
        past_tail_positions = new Point[3];
        for(int j = 0; j<3; j++){
            past_tail_positions[j] = new Point(-2,-2);
        }
        new_dir = Direction.RIGHT;
        t = new Thread[4];
        t[0] = new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        waitForSignal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updatePlayerSnake(new_dir);
                    try {
                        finishMove();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        t[1] = new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        waitForSignal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateAISnake(1);
                    try {
                        finishMove();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        t[2] = new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        waitForSignal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateAISnake(2);
                    try {
                        finishMove();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        t[3] = new Thread(){
            @Override
            public void run(){
                while(true){
                    try {
                        waitForSignal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateFrog();
                    try {
                        finishMove();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
            }
        };
        for(int i = 0; i<4; i++){
            t[i].start();
        }
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

    public void updatePlayerSnake(Direction dir){
        int next = snakeGo(dir, 0); //sprawdzamy dla sneka gracza
        if(!snek1.getState()) {
            next_positions[0] = new Point(
                snek1.getSnakePart(0).getX() + dir.getX(),
                snek1.getSnakePart(0).getY() + dir.getY()
            );
            //jedyna sytuacja, kiedy ogon ucieknie: nic tam nie ma, w pozostałych i tak snek umrze
            //ogon to size-1
            if(next == 0){
                past_tail_positions[0] = new Point(
                    snek1.getSnakePart(getSnakeSize(0)-1).getX(),
                    snek1.getSnakePart(getSnakeSize(0)-1).getY()
                );
            }
        }
        //snek jest martwy, już nigdzie nie pójdzie, umarł w poprzednim kroku
        else {
            next_positions[0] = new Point(-1,-1);
            past_tail_positions[0] = new Point(-1, -1);
        }
        //Czekamy, aż wszyscy tu przyjdą
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        boolean dont_die = false;
        //miał wpaść na ogon sneka 1, nie zrobi tego
        if(next_positions[0]==past_tail_positions[0]){
            //miały się zderzyć głowa ogon, nie zrobią tego
            printStuff();
            dont_die = true;
        }
        else if(snek2!=null && next_positions[0]==past_tail_positions[1]){
            //miały się zderzyć głowa ogon, nie zrobią tego
            printStuff();
            dont_die = true;
        }
        else if(snek3 != null && next_positions[0]==past_tail_positions[2]){
            //miały się zderzyć głowa ogon, nie zrobią tego
            printStuff();
            dont_die = true;
        }
        snek1.move(dir, next); //snek idzie
        if(dont_die) snek1.setStatus(false);
    }

    public void reset(int i){
        prev_dir = Direction.RIGHT;
        //resetujemy też ten wektor
        for(int j = 0; j<3; j++){
            next_positions[j] = new Point(-2,-2);
        }
        for(int j = 0; j<3; j++){
            past_tail_positions[j] = new Point(-2,-2);
        }
        barrier = new CyclicBarrier(3);
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

    public void updateAISnake(int s){
        if(s == 1 && snek2!=null){
            Direction dir = findAIMove(s);
            int next = snakeGo(dir, s);
            if(!snek2.getState()) {
                next_positions[s] = new Point(
                    snek2.getSnakePart(0).getX() + dir.getX(),
                    snek2.getSnakePart(0).getY() + dir.getY()
                );
                //jedyna sytuacja, kiedy ogon ucieknie: nic tam nie ma, w pozostałych i tak snek umrze
                //ogon jest na pozycji size-1
                if(next == 0){
                    past_tail_positions[1] = new Point(
                        snek2.getSnakePart(getSnakeSize(1)-1).getX(),
                        snek2.getSnakePart(getSnakeSize(1)-1).getY()
                    );
                }
            }
            //snek jest martwy, już nigdzie nie pójdzie, umarł w poprzednim kroku
            else {
                next_positions[s] = new Point(-1,-1);
                past_tail_positions[s] = new Point(-1, -1);
            }
            //1 przypadek: nie wszyscy żywi skończyli, czekamy
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            boolean dont_die = false;
            if(next_positions[1]==past_tail_positions[0]){
            //miały się zderzyć głowa ogon, nie zrobią tego
                printStuff();
                dont_die = true;
            }
            else if(next_positions[1]==past_tail_positions[1]){
                //miały się zderzyć głowa ogon, nie zrobią tego
                printStuff();
                dont_die = true;
            }
            else if(snek3 != null && next_positions[1]==past_tail_positions[2]){
                //miały się zderzyć głowa ogon, nie zrobią tego
                printStuff();
                dont_die = true;
            }
            snek2.move(dir, next);
            if(dont_die) snek2.setStatus(false);
        }
        //snake nieaktywny
        else if(s == 1){
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
        if(s == 2 && snek3!=null){
            Direction dir = findAIMove(s);
            int next = snakeGo(dir, s);
            if(!snek3.getState()) {
                next_positions[s] = new Point(
                    snek3.getSnakePart(0).getX() + dir.getX(),
                    snek3.getSnakePart(0).getY() + dir.getY()
                );
                //jedyna sytuacja, kiedy ogon ucieknie: nic tam nie ma, w pozostałych i tak snek umrze
                //ogon jest na pozycji size-1
                if(next == 0){
                    past_tail_positions[2] = new Point(
                        snek3.getSnakePart(getSnakeSize(2)-1).getX(),
                        snek3.getSnakePart(getSnakeSize(2)-1).getY()
                    );
                }
            }
            //snek jest martwy, już nigdzie nie pójdzie, umarł w poprzednim kroku
            else {
                next_positions[s] = new Point(-1,-1);
                past_tail_positions[s] = new Point(-1, -1);
            }
            //1 przypadek: nie wszyscy żywi skończyli, czekamy
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            boolean dont_die = false;
            if(snek3 != null && next_positions[2]==past_tail_positions[0]){
                //miały się zderzyć głowa ogon, nie zrobią tego
                printStuff();
                dont_die = true;
            }
            else if(snek3 != null && snek2 != null && next_positions[2]==past_tail_positions[1]){
                //miały się zderzyć głowa ogon, nie zrobią tego
                printStuff();
                dont_die = true;
            }
            else if(snek3 != null && next_positions[2]==past_tail_positions[2]){
                //miały się zderzyć głowa ogon, nie zrobią tego
                printStuff();
                dont_die = true;
            }
            snek3.move(dir, next);
            if(dont_die) snek3.setStatus(false);
        }
        //snake nieaktywny
        else if(s == 2){
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    //znajduje najbliższy owocek
    private int findClosest(int x, int y){
        int distance = 10000;
        int curr=-1;
        for(int i = 10; i<15; i++){
            int tmp = Math.abs(x-getItem(i).getX());
            tmp += Math.abs(y-getItem(i).getY());
            if(distance>tmp){
                distance = tmp;
                curr = i;
            }
        }
        return curr;
    }

    private Direction findAIMove(int s){
        int currx=0;
        int curry=0;
        int closest = 0;
        Direction dir;
        Direction prev = Direction.RIGHT;
        if(s==1){
            currx = snek2.getSnakePart(0).getX();
            curry = snek2.getSnakePart(0).getY();
            prev = snek2.getPrev();
        }
        else if(s==2){
            currx = snek3.getSnakePart(0).getX();
            curry = snek3.getSnakePart(0).getY();
            prev = snek3.getPrev();
        }
        closest = findClosest(currx, curry);
        int distance = Math.abs(currx-getItem(closest).getX());
        distance += Math.abs(curry-getItem(closest).getY());
        
        //sprawdzamy 4 przypadki:
        //W PRAWO, ale tylko, jeśli nie szedł wcześniej w lewo:
        if(prev != Direction.LEFT){
            dir = Direction.RIGHT;
            //jest tam owocek
            if(isTaken(currx + dir.getX(), curry + dir.getY()) == 1){
                return dir;
            }
            //zbliżamy się do owocka
            else if(isTaken(currx + dir.getX(), curry + dir.getY()) == 0){
                int tmp = Math.abs(currx+dir.getX()-getItem(closest).getX());
                tmp += Math.abs(curry + dir.getY()-getItem(closest).getY());
                if(distance>tmp){
                    return dir;
                }
            }
        }
        //W DÓŁ, ale tylko, jeśli nie szliśmy wcześniej w górę
        if(prev != Direction.UP){
            dir = Direction.DOWN;
            //owocek
            if(isTaken(currx + dir.getX(), curry + dir.getY()) == 1){
                return dir;
            }
            //bliżej owocka
            else if(isTaken(currx + dir.getX(), curry + dir.getY()) == 0){
                int tmp = Math.abs(currx+dir.getX()-getItem(closest).getX());
                tmp += Math.abs(curry + dir.getY()-getItem(closest).getY());
                if(distance>tmp){
                    return dir;
                }
            }
        }
        //W LEWO, ale tylko, jeśli nie szliśmy wcześniej w prawo
        if(prev != Direction.RIGHT){
            dir = Direction.LEFT;
            //owocek
            if(isTaken(currx + dir.getX(), curry + dir.getY()) == 1){
                return dir;
            }
            //zbliżamy się do owocka
            else if(isTaken(currx + dir.getX(), curry + dir.getY()) == 0){
                int tmp = Math.abs(currx+dir.getX()-getItem(closest).getX());
                tmp += Math.abs(curry + dir.getY()-getItem(closest).getY());
                if(distance>tmp){
                    return dir;
                }
            }
        }
        //W GÓRĘ, ale tylko, jeśli wcześniej nie szliśmy w dół:
        if(prev != Direction.DOWN){
            dir = Direction.UP;
            //owocek
            if(isTaken(currx + dir.getX(), curry + dir.getY()) == 1){
                return dir;
            }
            //bliżej owocka lub nie zabije nas to
            else if(isTaken(currx + dir.getX(), curry + dir.getY()) == 0){
                return dir;
            }
        }
        
        //ruchy neutralne, nie zbliżające nas do owocka, ale nie mordujące nas
        if(isTaken(currx + Direction.RIGHT.getX(), curry + Direction.RIGHT.getY()) == 0 && prev != Direction.LEFT){
            return Direction.RIGHT;
        }
        else if(isTaken(currx + Direction.DOWN.getX(), curry + Direction.DOWN.getY()) == 0 && prev != Direction.UP){
            return Direction.DOWN;
        }
        else if(isTaken(currx + Direction.LEFT.getX(), curry + Direction.LEFT.getY()) == 0 && prev != Direction.RIGHT){
            return Direction.LEFT;
        }
        //i tak zginiemy, domyślnie w ten sam kierunek, co wcześniej
        return prev;
    }

    private Direction checkBest(int dist, int closest){
        int tmp = 0;
        if(closest == 0){
            if(isTaken(forg.getX() + Direction.RIGHT.getX(), forg.getY() + Direction.RIGHT.getY()) == 0){
                tmp = Math.abs(snek1.getSnakePart(0).getX()-forg.getX() + Direction.RIGHT.getX());
                tmp += Math.abs(snek1.getSnakePart(0).getY() - forg.getY() + Direction.RIGHT.getY());
                if(tmp<dist) return Direction.RIGHT;
            }
            if(isTaken(forg.getX() + Direction.DOWN.getX(), forg.getY() + Direction.DOWN.getY()) == 0){
                tmp = Math.abs(snek1.getSnakePart(0).getX()-forg.getX() + Direction.DOWN.getX());
                tmp += Math.abs(snek1.getSnakePart(0).getY() - forg.getY() + Direction.DOWN.getY());
                if(tmp<dist) return Direction.DOWN;
            }
            if(isTaken(forg.getX() + Direction.LEFT.getX(), forg.getY() + Direction.LEFT.getY()) == 0){
                tmp = Math.abs(snek1.getSnakePart(0).getX()-forg.getX() + Direction.LEFT.getX());
                tmp += Math.abs(snek1.getSnakePart(0).getY() - forg.getY() + Direction.LEFT.getY());
                if(tmp<dist) return Direction.LEFT;
            }
            if(isTaken(forg.getX() + Direction.UP.getX(), forg.getY() + Direction.UP.getY()) == 0){
                return Direction.UP;
            }
            else if (isTaken(forg.getX() + Direction.RIGHT.getX(), forg.getY() + Direction.RIGHT.getY()) == 0){
                return Direction.RIGHT;
            }
            else if(isTaken(forg.getX() + Direction.DOWN.getX(), forg.getY() + Direction.DOWN.getY()) == 0){
                return Direction.DOWN;
            }
            else if(isTaken(forg.getX() + Direction.LEFT.getX(), forg.getY() + Direction.LEFT.getY()) == 0){
                return Direction.LEFT;
            }
            //żaba otoczona z czterech stron obiektami, i tak umrze, zmieniamy status i umieramy
            else{
                forg.setStatus(true);
                return Direction.RIGHT;
            }
        }
        else if(closest == 1){
            if(isTaken(forg.getX() + Direction.RIGHT.getX(), forg.getY() + Direction.RIGHT.getY()) == 0){
                tmp = Math.abs(snek2.getSnakePart(0).getX()-forg.getX() + Direction.RIGHT.getX());
                tmp += Math.abs(snek2.getSnakePart(0).getY() - forg.getY() + Direction.RIGHT.getY());
                if(tmp<dist) return Direction.RIGHT;
            }
            if(isTaken(forg.getX() + Direction.DOWN.getX(), forg.getY() + Direction.DOWN.getY()) == 0){
                tmp = Math.abs(snek2.getSnakePart(0).getX()-forg.getX() + Direction.DOWN.getX());
                tmp += Math.abs(snek2.getSnakePart(0).getY() - forg.getY() + Direction.DOWN.getY());
                if(tmp<dist) return Direction.DOWN;
            }
            if(isTaken(forg.getX() + Direction.LEFT.getX(), forg.getY() + Direction.LEFT.getY()) == 0){
                tmp = Math.abs(snek2.getSnakePart(0).getX()-forg.getX() + Direction.LEFT.getX());
                tmp += Math.abs(snek2.getSnakePart(0).getY() - forg.getY() + Direction.LEFT.getY());
                if(tmp<dist) return Direction.LEFT;
            }
            if(isTaken(forg.getX() + Direction.UP.getX(), forg.getY() + Direction.UP.getY()) == 0){
                return Direction.UP;
            }
            else if (isTaken(forg.getX() + Direction.RIGHT.getX(), forg.getY() + Direction.RIGHT.getY()) == 0){
                return Direction.RIGHT;
            }
            else if(isTaken(forg.getX() + Direction.DOWN.getX(), forg.getY() + Direction.DOWN.getY()) == 0){
                return Direction.DOWN;
            }
            else if(isTaken(forg.getX() + Direction.LEFT.getX(), forg.getY() + Direction.LEFT.getY()) == 0){
                return Direction.LEFT;
            }
            //żaba otoczona z czterech stron obiektami, i tak umrze, zmieniamy status i umieramy
            else{
                forg.setStatus(true);
                return Direction.RIGHT;
            }
        }
        else{
            if(isTaken(forg.getX() + Direction.RIGHT.getX(), forg.getY() + Direction.RIGHT.getY()) == 0){
                tmp = Math.abs(snek3.getSnakePart(0).getX()-forg.getX() + Direction.RIGHT.getX());
                tmp += Math.abs(snek3.getSnakePart(0).getY() - forg.getY() + Direction.RIGHT.getY());
                if(tmp<dist) return Direction.RIGHT;
            }
            if(isTaken(forg.getX() + Direction.DOWN.getX(), forg.getY() + Direction.DOWN.getY()) == 0){
                tmp = Math.abs(snek3.getSnakePart(0).getX()-forg.getX() + Direction.DOWN.getX());
                tmp += Math.abs(snek3.getSnakePart(0).getY() - forg.getY() + Direction.DOWN.getY());
                if(tmp<dist) return Direction.DOWN;
            }
            if(isTaken(forg.getX() + Direction.LEFT.getX(), forg.getY() + Direction.LEFT.getY()) == 0){
                tmp = Math.abs(snek3.getSnakePart(0).getX()-forg.getX() + Direction.LEFT.getX());
                tmp += Math.abs(snek3.getSnakePart(0).getY() - forg.getY() + Direction.LEFT.getY());
                if(tmp<dist) return Direction.LEFT;
            }
            if(isTaken(forg.getX() + Direction.UP.getX(), forg.getY() + Direction.UP.getY()) == 0){
                return Direction.UP;
            }
            else if (isTaken(forg.getX() + Direction.RIGHT.getX(), forg.getY() + Direction.RIGHT.getY()) == 0){
                return Direction.RIGHT;
            }
            else if(isTaken(forg.getX() + Direction.DOWN.getX(), forg.getY() + Direction.DOWN.getY()) == 0){
                return Direction.DOWN;
            }
            else if(isTaken(forg.getX() + Direction.LEFT.getX(), forg.getY() + Direction.LEFT.getY()) == 0){
                return Direction.LEFT;
            }
            //żaba otoczona z czterech stron obiektami, i tak umrze, zmieniamy status i umieramy
            else{
                forg.setStatus(true);
                return Direction.RIGHT;
            }
        }
    }

    private void updateFrog(){
        //żaba została zjedzona
        if(forg.getStatus()){
            generateFrog();
            return;
        }
        int distance = 1000;
        int tmp = 0;
        int closest = 0;
        //szukamy najbliższego sneka
        tmp = Math.abs(snek1.getSnakePart(0).getX()-forg.getX());
        tmp += Math.abs(snek1.getSnakePart(0).getY() - forg.getY());
        if(distance>tmp) {
            distance = tmp;
            closest = 0;
        }

        if(snek2 != null){
            tmp = Math.abs(snek2.getSnakePart(0).getX()-forg.getX());
            tmp += Math.abs(snek2.getSnakePart(0).getY() - forg.getY());
        }
        if(distance>tmp) {
            distance = tmp;
            closest = 1;
        }
        if(snek3!=null){
            tmp = Math.abs(snek3.getSnakePart(0).getX()-forg.getX());
            tmp += Math.abs(snek3.getSnakePart(0).getY() - forg.getY());
        }
        if(distance>tmp) {
            distance = tmp;
            closest = 2;
        }

        Direction dir = checkBest(distance, closest);
        if(!forg.getStatus()){
            forg.setX(forg.getX()+dir.getX());
            forg.setY(forg.getY()+dir.getY());
        }
        

    }

    public Frog getFrog(){
        return forg;
    }

    private void checkHeadToHead(){
        if(next_positions[0] == next_positions[1]){
            //zaraz zderzą się głowami, pora je zabić
            snek1.setStatus(true);
            if(snek2!= null) snek2.setStatus(true);
        }
        if(next_positions[0] == next_positions[2]){
            snek1.setStatus(true);
            if(snek3!=null) snek3.setStatus(true);
        }
        if(next_positions[1] == next_positions[2]){
            if(snek2!=null) snek2.setStatus(true);
            if(snek3!=null) snek3.setStatus(true);
        }
    }

    private void printStuff(){
        for (int i=0; i<3; i++) {
            System.out.println("Snek " + i);
            System.out.println("Head " + next_positions[i]);
            System.out.println("Tail " + past_tail_positions[i]);
        }
    }


    public void go(Direction dir){
        //do sneka gracza


        //jeśli gracz już nie żyje, nie idziemy dalej
        // if(snek1.getState()){
        //     return;
        // }
        new_dir = dir;
        startMove();
        

        //po updatowaniu sprawdzamy, czy któryś nie umarł na darmo albo nie umarł a powinien
        //sprawdzamy, czy ktoś nie zderzył się głowami - dwie głowy mają to samo value
        checkHeadToHead();
        //sam koniec - resetujemy aktywne węże na -2, -2
        for(int j = 0; j<3; j++){
            next_positions[j] = new Point(-2,-2);
        }
        for(int j = 0; j<3; j++){
            past_tail_positions[j] = new Point(-2,-2);
        }
    }

    public synchronized void waitForSignal() throws InterruptedException {
        while (!shouldWork) wait();
    }

    public synchronized void startMove() {
        shouldWork = true;
        notifyAll();
    }

    public void finishMove() throws Exception {
        finished_barrier.await();  // Workers wait here until all finish
        shouldWork = false;  // Reset only after all threads sync
    }
}
