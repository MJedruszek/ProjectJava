package snek;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * <p>Klasa odpowiedzialna za przechowywanie planszy: snakex3, żaba, owoce i kamienie. Zna stan planszy, mówi
 * snake'om i żabie, gdzie powinny się przemieścić, i trzyma algorytmy umożliwiające AI sterowanie. To tutaj
 * zaimplementowane są wątki, które umożliwiają równoległe działanie programu</p>
 */
public class Board {
    /**
     * <p>Lista przedmiotów nieruchomych (owoce i kamienie). Od 0 do 9 są kamienie, od 10 do 14 są owoce</p>
     */
    private List<Item> items;
    /**
     * <p>Szerokość planszy</p>
     */
    private int width;
    /**
     * <p>Wysokość planszy</p>
     */
    private int height;
    /**
     * <p>Snake sterowany przez gracza, aktywny w każdym z trybów</p>
     */
    private PlayerSnake snek1;
    /**
     * <p>Snake sterowany przez prosty algorytm, aktywny w dwu- i trzyosobowym trybie rozgrywki</p>
     */
    private AISnake snek2;
    /**
     * <p>Snake sterowany przez prosty algorytm, aktywny tylko w trzyosobowym trybie rozgrywki</p>
     */
    private AISnake snek3;
    /**
     * <p>Poprzedni kierunek, w którym podążał snake gracza</p>
     */
    private Direction prev_dir;
    /**
     * <p>Nowy kierunek, w którym będzie podążał snake gracza</p>
     */
    private Direction new_dir;
    /**
     * <p>Wątki odpowiedzialne za obsługę snake'ów i żaby. t[0] to snake gracza, t[1] to snakeAI nr 2, t[2] to snakeAI nr 3,
     * a t[3] to żaba</p>
     */
    private Thread[] t;
    /**
     * <p>Żaba, sterowana prostym algorytmem. Jej zadaniem jest uciekanie od snake'ów, a po zjedzeniu da 3 punkty</p>
     */
    private Frog forg;
    /**
     * <p>Bariera, umożliwiająca synchronizację między wątkami przed postawieniem ruchu</p>
     */
    private CyclicBarrier barrier;
    /**
     * <p>Bariera, umożliwiająca synchronizacją między wątkami w oczekiwaniu na polecenie wykonania ruchu</p>
     */
    private CyclicBarrier finished_barrier;
    /**
     * <p>Zmienna pomocnicza, umożliwiająca sterowanie pracą wątków</p>
     */
    private boolean shouldWork;

    /**
     * <p>Funkcja umożliwiająca sprawdzenie, czy dane miejsce na planszy jest zajęte i, jeśli tak, to przez co</p>
     * @param x Położenie obiektu na osi X
     * @param y Położenie obiektu na osi Y
     * @return 0, jeśli puste; 1, jeśli owoc; 2, jeśli kamień, snake lub koniec planszy; 3, jeśli żaba
     */
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

    /**
     * <p>Pozwala znaleźć owoc o zadanej pozycji</p>
     * @param x Położenie poszukiwanego owocu na osi X
     * @param y Położenie poszukiwanego owocu na osi Y
     * @return Pozycja owocu w wektorze items, jeśli znaleziono; -1, jeśli nie znaleziono
     */
    private int findFruitByPosition(int x, int y){
        for(int i = 10; i<15; i++){
            if(items.get(i).getX() == x && items.get(i).getY() == y){
                return i;
            }
        }
        return -1;
    }

    /**
     * <p>Poruszanie określonym snake'iem, poprzez wydanie mu odpowiedniego polecenia. Oprócz tego
     * funkcja automatycznie zmienia pozycję owoca, który został przed chwilą zjedzony przez snake'a
     * i informuje żabę o konieczności zmiany miejsca przez zmianę jej statusu</p>
     * @param dir Kierunek, w którym teraz powienien pójść snake
     * @param s Który snake powinien się poruszyć: 0 - gracz, 1 - snakeAI nr 2, 2 - snakeAI nr 3
     * @return Wynik przejścia na nową pozycję, czyli co zwróci funkcja isTaken dla nowej pozycji głowy
     */
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

    /**
     * <p>Funkcja odpowiedzialna za dodanie nowego elementu, kamienia lub owocu</p>
     * @param type Jeśli kamień true, jeśli owoc false
     */
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

    /**
     * <p>Funkcja generuje obiekty na planszy, 10 kamieni i 5 owoców</p>
     */
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

    /**
     * <p>Funkcja generuje snake'a, wyszukując dla niego dwóch pustych miejsc po sobie, co najmniej
     * trzy kratki od każdej ściany</p>
     * @param i Numer snake'a, który powinien zostać wygenerowany: 0 oznacza gracza, 1 snakeAI nr 2, 2 snakeAI nr 3
     */
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

    /**
     * <p>Funkcja szuka wolnego miejsca dla żaby</p>
     */
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

    /**
     * <p>Kontruktor klasy Board. Inicjalizuje wątki oraz wszystkie zmienne, a także układa planszę</p>
     * @param width Szerokość planszy
     * @param height Wysokość planszy
     * @param s Liczba snake'ów do wygenerowania
     */
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

    /**
     * <p>Prosty getter, na potrzeby wyświetlania</p>
     * @param i Który element chcemy pobrać
     * @return Element, który chcieliśmy pobrać
     */
    public Item getItem(int i){
        return items.get(i);
    }

    /**
     * <p>Prosty getter, na potrzeby wyświetlania</p>
     * @param i Który element snake'a chcemy pobrać
     * @param s Którego snake'a chcemy pobrać; 0 - snake gracza, 1 - snakeAI nr 2, 2 - snakeAI nr 3
     * @return Element, który chcieliśmy pobrać
     */
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

    /**
     * <p>Funkcja umożliwiająca pobranie informacji o rozmiarze snake'a</p>
     * @param i Snake, którego długość chcemy pobrać; 0 - snake gracza, 1 - snakeAI nr 2, 2 - snakeAI nr 3
     * @return długość snake'a
     */
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

    /**
     * <p>Funkcja zwracająca informację, czy snake jest martwy</p>
     * @param s Numer snake'a, o którym chcemy pobrać informację; 0 - snake gracza, 1 - snakeAI nr 2, 2 - snakeAI nr 3
     * @return True, jeśli snake jest martwy, False, jeśli jest żywy
     */
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

    /**
     * <p>Funkcja, która najpierw oblicza, co jest na kolejnym miejscu na planszy, a następnie oczekuje na
     * przyjście pozostałych snake'ów, po czym przekazuje tę informację do snake'a i każe mu się poruszyć.</p>
     * @param dir Kierunek, w którym snake się poruszy
     */
    public void updatePlayerSnake(Direction dir){
        int next = snakeGo(dir, 0); //sprawdzamy dla sneka gracza
        
        //Czekamy, aż wszyscy tu przyjdą
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        snek1.move(dir, next); //snek idzie
    }

    /**
     * <p>Resetuje planszę i snake'i po rozpoczęciu nowej rozgrywki</p>
     * @param i Ile snake'ów należy włączyć
     */
    public void reset(int i){
        prev_dir = Direction.RIGHT;
        barrier = new CyclicBarrier(3);
        generateAllItems();
        generateSnek(0);
        generateFrog();
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

    /**
     * <p>Funkcja zwracająca kolor danego segmentu snake'a, do wyświetlania</p>
     * @param i Snake, którego kolor chcemy pobrać; 0 - snake gracza, 1 - snakeAI nr 2, 2 - snakeAI nr 3
     * @param head True, jeśli chodzi nam o kolor głowy, false, jeśli o kolor ciała
     * @return Kolor danego segmentu
     */
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

    /**
     * <p>Funkcja odpowiedzialna za włączenie funkcji obliczającej kolejny ruch, odczekanie, aż pozostałe nadążą i
     * poruszenie snake'iem. Jeśli snake jest nieaktywny, włączamy od razu barierę, aby wątki nie wisiały
     * czekając na nieaktywny wątek</p>
     * @param s Numer snake'a, który ma się poruszyć: 1 - snakeAI nr 2, 2 - snakeAI nr 3
     */
    public void updateAISnake(int s){
        if(s == 1 && snek2!=null){
            Direction dir = findAIMove(s);
            int next = snakeGo(dir, s);
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            snek2.move(dir, next);
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
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            snek3.move(dir, next);
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

    /**
     * <p>Funkcja do snake'ów AI, szukająca najbliższego owocu</p>
     * @param x Położenie głowy na osi X
     * @param y Położenie głowy na osi Y
     * @return Najbliższy znaleziony owoc
     */
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

    /**
     * <p>Szuka najlepszego możliwego ruchu dla snake'a; najpierw znajduje aktualne położenie i kierunek snake'a, 
     * następnie szuka najbliższego do niego owocu, po czym szuka kierunku, który się najbardziej opłaca. </p>
     * <p>W trakcie poszukiwania kierunku najpierw sprawdzane jest, czy kierunek umożliwia zjedzenie owocu, następnie,
     * czy zbliży węża do owocu, a na koniec, czy którykolwiek z kierunków umożliwi uniknięcie śmierci.</p>
     * @param s Numer snake'a, dla którego należy znaleźć kierunek: 1 - snakeAI nr 2, 2 - snakeAI nr 3
     * @return Kierunek, w który snake powinien się poruszyć
     */
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

    /**
     * <p>Poszukiwanie najlepszego możliwego ruchu dla żaby: jeśli pole jest puste, sprawdzamy czy pójście tam
     * umożliwia oddalenie się od snake</p>
     * @param dist Aktualna odległość od najbliższego snake'a
     * @param closest Który snake jest aktualnie najbliższy: 0 - snake gracza, 1 - snakeAI nr 2, 2 - snakeAI nr 3
     * @return Kierunek, który da żabie największe korzyści
     */
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

    /**
     * <p>Funkcja odpowiedzialna za poruszanie żabą.</p>
     * <p>Jeśli żaba została zjedzona w poprzednim ruchu, należy znaleźć jej nowe miejsce za pomocą generateFrog()</p>
     * <p>Jeśli żaba nie została zjedzona, należy znaleźć snake'a najbliższego do żaby, znaleźć opcję, która
     * najbardziej ją od niego oddali, a następnie przemieścić ją zgodnie z tym kierunkiem</p>
     */
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

    /**
     * Prosty getter, do wyświetlania żaby
     * @return Żaba 
     */
    public Frog getFrog(){
        return forg;
    }

    /**
     * <p>Ta funkcja włącza wątki poprzez wykorzystanie funkcji startMove() i przekazuje kierunek, w którym ma
     * się poruszyć snake gracza, otrzymany od GamePanelu</p>
     * @param dir Kierunek, który należy wpisać do zmiennej new_dir dla snake'a gracza
     */
    public void go(Direction dir){
        new_dir = dir;
        startMove();

    }

    /**
     * Funkcja umożliwia oczekiwanie na sygnał rozpoczęcia pracy wątkom snake'ów
     * @throws InterruptedException Wyrzucane przez wait()
     */
    public synchronized void waitForSignal() throws InterruptedException {
        while (!shouldWork) wait();
    }

    /**
     * <p>Funkcja, która umożliwia włączanie ruchu w odpowiednim momencie. Zmienia stan zmiennej shouldWork, a
     * następnie informuje o tym oczekujące wątki</p>
     */
    public synchronized void startMove() {
        shouldWork = true;
        notifyAll();
    }

    /**
     * <p>Funkcja, która jest wywoływana przez wątki po zakończeniu pracy. Kiedy zakończą swoje ruchy, 
     * czekają na zwolnienie bariery, a następnie ustawiają zmienną shouldWork na false, dzięki czemu później,
     * w kolejnej pętli run(), zaczekają na sygnał od boarda</p>
     * @throws Exception Wyrzucane przez await()
     */
    public void finishMove() throws Exception {
        finished_barrier.await();
        shouldWork = false; 
    }
}
