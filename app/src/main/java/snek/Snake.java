package snek;

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
    //jeśli jesteś martwy, zwracasz false (więc na początku, po sprawdzeniu martwości, zwracamy dalej false)
    //jeśli żyjesz, zwracasz true
    abstract boolean move(Direction dir, Direction prev_dir);
    
    //wywoływana w move, na koniec, jeśli snek jest żywy: powiększa sneka, jeśli trzeba, i przesuwa party w określonym kierunku
    abstract void update(Direction dir);

    //do przemieszczenia kawałka sneka na pozycję tego o 1 przed nim (zaczynamy od końca), głowa jest poruszana w update
    abstract void movePart(int pos);

    //wywoływana, żeby sprawdzić kolizję ze snekiem
    abstract boolean checkIfHit(int x, int y);

    abstract SnakePart getSnakePart(int i);

    abstract int getSnakeSize();
}
