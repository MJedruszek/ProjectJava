package snek;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

/**
 * Panel odpowiedzialny za wyświetlanie gry
 */
public class GamePanel extends JPanel implements KeyListener, Runnable{
    /**
     * Kontroler, który przechowuje informacje o grze
     */
    private GameController g_Controller; 
    /**
     * Szerokość planszy w pixelach
     */
    private int width;
    /**
     * Wysokość planszy w pixelach
     */
    private int height;   
    /**
     * Wątek odpowiedzialny za wyświetlanie, implementuje runnable
     */
    private Thread gameThread = new Thread(this); 
    /**
     * Górna granica FPS
     */
    private int FPS = 8;
    /**
     * Zmienna służąca do minimalizacji aktualizacji ekranu, zmieniana na true, jeśli została wykryta jakaś zmiana
     */
    private boolean something_changed;
    /**
     * Zmienna odpowiedzialna za zapamiętanie włączenia stanu pauzy
     */
    private boolean pauza;
    /**
     * Kierunek zadany przez gracza za pomocą klawiatury
     */
    private Direction dir;
    /**
     * Czcionka do wyświetlenia informacji po zakończeniu gry
     */
    private Font f;

    /**
     * Konstruktor, służący do zainicjowania odpowiednich zmiennych, wewnętrznych i domyślnych
     * @param g Kontroler, przekazany przez Frame
     * @param w Szerokość planszy w pixelach
     * @param h Wysokość planszy w pixelach
     */
    public GamePanel(GameController g, int w, int h){
        g_Controller = g;
        this.setBackground(Color.BLACK);
        this.setSize(new Dimension(w,h));
        this.width = w;
        this.height = h;
        gameThread.start();
        something_changed = true;
        this.setFocusable(true);
        this.addKeyListener(this);
        pauza = true;
        dir = Direction.RIGHT;
        f = new Font("TimesRoman", Font.PLAIN, 30);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    /**
     * <p>Rysuje kolejno: puste pola na planszy, kamienie, jabłka, snake'i, żabę, ikonę pauzy (jeśli jest potrzebna),
     * informację o zakończeniu gry i liczbie zdobytych punktów (jeśli jest potrzebna)</p>
     */
    @Override
    public void paintComponent(Graphics g){
        //jedna kratka ma 8x8 pixeli
        g.setFont(f);
        super.paintComponent(g);
        for(int x = 0; x<width/16-3;x++){
            for(int y = 0; y<height/16-4;y++){
                g.setColor(Color.GRAY);
                g.drawRect(16+x*16, 16+y*16, 15,15);
            }
        }
        for(int i = 0; i<15; i++){
            int x = g_Controller.getBoard().getItem(i).getX();
            int y = g_Controller.getBoard().getItem(i).getY();
            if(g_Controller.getBoard().getItem(i).getType()){
                g.setColor(Color.LIGHT_GRAY);
            }
            else{
                g.setColor(Color.GREEN);
            }
            g.fillRect(16+x*16, 16*y+16, 15, 15);
        }
        
        int snakeNum = 0;
        if(g_Controller.getState() == GameState.ONE_PLAYER){
            snakeNum = 1;
        }
        else if(g_Controller.getState() == GameState.TWO_PLAYER){
            snakeNum = 2;
        }
        else if(g_Controller.getState() == GameState.THREE_PLAYER){
            snakeNum = 3;
        }
        
        for(int s = 0; s<snakeNum; s++){
            g.setColor(g_Controller.getBoard().getSnakeColor(s, true));
            for(int i = 0; i<g_Controller.getBoard().getSnakeSize(s); i++){
                int x = g_Controller.getBoard().getSnakePart(i, s).getX();
                int y = g_Controller.getBoard().getSnakePart(i, s).getY();
                g.fillRect(16+x*16, 16*y+16, 15, 15);
                g.setColor(g_Controller.getBoard().getSnakeColor(s, false));
            }
        }
        int x = g_Controller.getBoard().getFrog().getX();
        int y = g_Controller.getBoard().getFrog().getY();
        g.setColor(g_Controller.getBoard().getFrog().getColor());
        g.fillRect(16+x*16, 16*y+16, 15, 15);
        
        

        if(pauza && !g_Controller.getBoard().getSnakeStatus(0)){
            g.setColor(Color.magenta);
            g.fillRect(16+16, 16+16, 30, 30);
        }

        if(g_Controller.getBoard().getSnakeStatus(0)){
            g.setColor(Color.WHITE);
            g.drawString("Press \"p\" to play again", 20, 40);
            g.drawString("Score: " + g_Controller.getBoard().getSnakeSize(0), 20,80);
            if(pauza && g_Controller.getState() != GameState.END){
                g_Controller.setState(GameState.END);
            }
        }

        g.dispose();
    }
    /**
     * <p>Główna pętla, gdzie co 1 FPS board daje znać swoim wątkom, że powinny wykonać ruch, jeśli nie jesteśmy
     *  w stanie pauzy </p> 
     */
    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if(delta >= 1){

                if(!g_Controller.getBoard().getSnakeStatus(0) && !pauza){

                g_Controller.getBoard().go(dir);
                something_changed = true;
            }
                update();
                delta--;
            }
        }
    }


    /**
     * Jeśli coś się zmieniło oraz stan gry wskazuje na aktywną grę, repaint
     */
    private void update() {
        if(something_changed && (g_Controller.getState() == GameState.ONE_PLAYER || g_Controller.getState() == GameState.TWO_PLAYER || g_Controller.getState() == GameState.THREE_PLAYER)){
            //System.out.println("HERE");
            repaint();
            something_changed = false;
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * <p>Na wciśnięcie przycisku 'p' włączamy pauzę, wyłączenie jej jest powodowane przez wciśnięcie dowolnego
     * innego od 'p' przycisku</p>
     * <p> Wciśnięcie przycisku 'w' ustala kierunek w górę, 'a' na kierunek w lewo, 's' na kierunek w dół, a 'd'
     * na kierunek w prawo</p>
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar()=='p'){
            pauza = true;
            something_changed = true;
        }
        else{
            pauza = false;
            something_changed = true;
        }

        if(e.getKeyChar() == 'd'){
            dir = Direction.RIGHT;
        }
        else if(e.getKeyChar() == 'w'){
            dir = Direction.UP;
        }
        else if(e.getKeyChar() == 'a'){
            dir = Direction.LEFT;
        }
        else if(e.getKeyChar() == 's'){
            dir = Direction.DOWN;
        }

    }


    @Override
    public void keyReleased(KeyEvent e) {
       
    }



}
