package snek;

import java.awt.Color;

public interface Snake {
    //funkcje do poruszania - kierunek i zmiana pozycji głowy
    //update pozycji - urośnij, przesuń party
    //czy ktoś ode mnie oberwał?

    //aktualna pozycja: x, y głowy - parta o pozycji 0
    //kierunek do poruszenia się: dir
    //poprzedni kierunek: prev_dir
    //jeśli prev_dir i dir są przeciwstawne, zostaw prev_dir
    //jeśli nie, prev_dir = dir
    //czy umieram? pytam boarda
    //jeśli jesteś martwy, zmień zmienną
    //jeśli żyjesz, gitara
    //jeśli snek jest żywy: powiększa sneka,
    abstract void move(Direction dir, int status);
    
    //wywoływana w move, na koniec i przesuwa party w określonym kierunku
    abstract void update(Direction dir);

    //dodaje num partów na koniec sneka
    abstract void addPart(int num);

    //do przemieszczenia kawałka sneka na pozycję tego o 1 przed nim (zaczynamy od końca), głowa jest poruszana w update
    abstract void movePart(int pos);

    //wywoływana, żeby sprawdzić kolizję ze snekiem
    abstract boolean checkIfHit(int x, int y);

    abstract SnakePart getSnakePart(int i);

    abstract int getSnakeSize();

    abstract boolean getState();

    abstract Color getColor(boolean head);

    // abstract void setStatus(boolean dead);
}
