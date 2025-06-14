package snek;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
/**
 * Klasa odpowiedzialna za sterowanie oknem
 */
public class Frame extends JFrame implements Runnable{
    //okno, ustawienia początkowe
    /**
     * Szerokość planszy w pixelach
     */
    private int WIDTH=1024+48;
    /**
     * Wysokość planszy w pixelach
     */
    private int HEIGHT=512+64;
    /**
     * Kontroler, ułatwiający komunikację z panelami
     */
    private GameController g_Controller;
    /**
     * Limit FPS dla całego okna
     */
    private int FPS = 30;
    /**
     * Panel odpowiedzialny za pokazywanie menu
     */
    private MenuPanel mPanel;
    /**
     * Panel odpowiedzialny za pokazywanie gry
     */
    private GamePanel gPanel;
    /**
     * Wątek obłsugujący menu, implementuje runnable
     */
    private Thread menuThread = new Thread(this); 
    /**
     * Czy należy zmienić panel na inny, zmieniane na true przy zmianie stanu rozgrywki
     */
    private boolean panelChanged;
    /**
     * Czy panel nie został zmieniony, do płynnej zmiany paneli
     */
    private boolean hasChanged;
    /**
     * Lista najwyższych wyników, pobierana z i zapisywana do pliku
     */
    private List<Integer> highScores;
    /**
     * Nazwa pliku z wynikami
     */
    private String f;
    /**
     * <p>Wartość, przez którą zostanie przemnożona liczba segmentów snake'a: 1 dla trybu jednoosobowego,
     * 2 dla trybu dwuosobowego i 3 dla trybu trzyosobowego</p>
     */
    private int multiplier;

    /**
     * Kontruktor, inicjalizujący zmienne i funkcje frame'a
     */
    public Frame(){
        f = "snek_scores.txt";
        g_Controller = new GameController();
        menuThread.start();
        mPanel = new MenuPanel(g_Controller);
        gPanel = new GamePanel(g_Controller, WIDTH, HEIGHT);
        pack();
        this.setSize(WIDTH, HEIGHT);
        this.getContentPane().add(mPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.setTitle("Snek");
        this.setVisible(true);
        this.setResizable(false);
        panelChanged = false;
        hasChanged = true;
        highScores = new ArrayList<Integer>();
        //zapisywanie do highscores
        readFromFile(f);
        mPanel.displayScores(highScores);
        multiplier = 1;
    }

    /**
     * Dodatkowo obsługuje zmianę paneli po zmianie stanu oraz ustawia multiplier, a także zapisuje dane do pliku
     */
    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        
        while(menuThread != null){
            if(g_Controller.getState() == GameState.ONE_PLAYER){
                this.getContentPane().removeAll();
                this.getContentPane().add(gPanel);
                if(hasChanged) {
                    g_Controller.getBoard().reset(0);
                    multiplier = 1;
                    panelChanged = true;
                }

                //System.out.println(g_Controller.getState());
            }
            else if(g_Controller.getState() == GameState.TWO_PLAYER){
                this.getContentPane().removeAll();
                this.getContentPane().add(gPanel);
                if(hasChanged) {
                    g_Controller.getBoard().reset(1);
                    multiplier = 2;
                    panelChanged = true;
                }
                //System.out.println(g_Controller.getState());
            }
            else if(g_Controller.getState() == GameState.THREE_PLAYER){
                this.getContentPane().removeAll();
                this.getContentPane().add(gPanel);
                if(hasChanged) {
                    g_Controller.getBoard().reset(2);
                    multiplier = 3;
                    panelChanged = true;
                }
                //System.out.println(g_Controller.getState());
            }
            else if(g_Controller.getState() == GameState.BEGGINING){
                //System.out.println(g_Controller.getState());
                hasChanged = true;
            }
            
            // AKTUALIZOWANIE POZYCJI GRACZA I PRZERYSOWYWANIE GO
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if(delta >= 1){
                if(g_Controller.getState() == GameState.END){
                    this.getContentPane().removeAll();
                    this.getContentPane().add(mPanel);
                    highScores.add(g_Controller.getBoard().getSnakeSize(0) * multiplier);
                    g_Controller.getBoard().reset(0);
                    if(this.mPanel != null){
                        saveScores(f);
                        mPanel.displayScores(highScores);
                        g_Controller.setState(GameState.BEGGINING);
                        //zapisywanie do pliku
                    
                    }
                    panelChanged = true;
                }
                update();
                //repaint();
                delta--;
            }
        }
    }

    /**
     * Jeśli panel się zmienił, zmienia widok poprzez zmianę stanu zmiennych oraz wywołuje repaint
     */
    private void update() {
        try{
            Thread.sleep(100);
            }
        catch(InterruptedException ex){
            System.out.println("Exception");
        }
        if(panelChanged){
            hasChanged = false;
            panelChanged = false;
            repaint();
        }
    }

    /**
     * Wczytuje informacje o wynikach z pliku
     * @param filename Nazwa pliku, z którego należy pobrać dane
     */
    private void readFromFile(String filename){
        try {
            File mapFile = new File(filename);
            Scanner myReader = new Scanner(mapFile);
                        
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                    highScores.add(Integer.parseInt(data));
                }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zapisuje dane o wynikach do pliku
     * @param filename Nazwa pliku do zapisania informacji
     */
    private void saveScores(String filename){
        int size;
        if(highScores.size()>10){
            size = 10;
        }
        else{
            size = highScores.size();
        }
        Collections.sort(highScores, Collections.reverseOrder());
        try {
            FileWriter myWriter = new FileWriter(filename);
            for(int i = 0; i<size; i++){
                myWriter.write(highScores.get(i).toString() + "\n");
            }
            myWriter.close();
        } 
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
