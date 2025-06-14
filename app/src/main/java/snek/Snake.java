package snek;

import java.awt.Color;
/**
 * Interfejs do snake'ów gracza i AI
 */
public interface Snake {
    //funkcje do poruszania - kierunek i zmiana pozycji głowy
    //update pozycji - urośnij, przesuń party
    //czy ktoś ode mnie oberwał?

    /**
     * <p>Poruszy odpowiednio snake'iem tak, jak nakazuje mu informacja od boarda. Obsługuje także zmianę stanu na
     * martwy oraz powiększanie snake'a</p>
     * @param dir Kierunek, w którym poruszy się snake
     * @param status Co jest na kolejnym polu? 0 - nic, 1 - owoc, 2 - przeszkoda, 3 - żaba
     */
    abstract void move(Direction dir, int status);
    
    /**
     * Funkcja ma za zadanie przesunąć wszystkie elementy z wektora snake'a do przodu
     * @param dir Kierunek, w którym ma przesunąć się głowa
     */
    abstract void update(Direction dir);

    /**
     * Funkcja dodaje segmenty na koniec snake'a
     * @param num Liczba segmentów, które należy dodać
     */
    abstract void addPart(int num);

    /**
     * Funkcja przesuwa segment o jedną pozycję do przodu; jeśli jest to głowa, nie jest tutaj przesuwana
     * @param pos Pozycja segmentu, który chcemy przesunąć
     */
    abstract void movePart(int pos);

    /**
     * Funkcja sprawdza, czy na tym miejscu jest któryś z segmentów snake'a
     * @param x Pozycja sprawdzanego pola na osi X
     * @param y Pozycja sprawdzanego pola na osi Y
     * @return True, jeśli to pole jest zajęte przez snake'a, false, jeśli nie
     */
    abstract boolean checkIfHit(int x, int y);

    /**
     * Prosty getter, do pobierania pojedynczego segmentu
     * @param i Numer segmentu, który chcemy pobrać
     * @return Segment, który chcieliśmy pobrać
     */
    abstract SnakePart getSnakePart(int i);

    /**
     * Prosty getter, do pobierania rozmiaru snake'a
     * @return Rozmiar snake'a
     */
    abstract int getSnakeSize();

    /**
     * Prosty getter, do pobierania stanu snake'a
     * @return True, jeśli snake jest martwy, false, jeśli nie
     */
    abstract boolean getState();

    /**
     * Prosty getter koloru snake'a, z rozróżnieniem na głowę i ciało
     * @param head True, jeśli chcemy pobrać informację o głowie, false, jeśli o ciele
     * @return Kolor konkretnego segmentu
     */
    abstract Color getColor(boolean head);

}
