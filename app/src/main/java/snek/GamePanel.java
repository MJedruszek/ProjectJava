package snek;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements KeyListener, Runnable{
    private GameController g_Controller; 
    private int width;
    private int height;   
    private Thread gameThread = new Thread(this); //implementuje runnable
    private int FPS = 8;
    private boolean something_changed;
    private boolean pauza;
    private Direction dir;
    private Font f;


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
                g.setColor(Color.RED);
            }
            g.fillRect(16+x*16, 16*y+16, 15, 15);
        }
        if(g_Controller.getBoard().getSnakeStatus(0)){
            g.setColor(Color.WHITE);
            g.drawString("Press \"p\" to play again", 20, 40);
            g.drawString("Score: " + g_Controller.getBoard().getSnakeSize(0), 20,80);
            g.setColor(Color.RED);
            if(pauza && g_Controller.getState() != GameState.END){
                g_Controller.setState(GameState.END);
            }
        }
        else{
            g.setColor(Color.cyan);
        }
        
        for(int i = 0; i<g_Controller.getBoard().getSnakeSize(0); i++){
            int x = g_Controller.getBoard().getSnakePart(i).getX();
            int y = g_Controller.getBoard().getSnakePart(i).getY();
            g.fillRect(16+x*16, 16*y+16, 15, 15);
            g.setColor(Color.GREEN);
        }

        if(pauza && !g_Controller.getBoard().getSnakeStatus(0)){
            g.setColor(Color.magenta);
            g.fillRect(16+16, 16+16, 30, 30);
        }

        g.dispose();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            //pętla do boarda:
            
            //update - aktualizacja stanu planszy i powiedzieć snekom AI, co się zmieniło
            //snakeGo dla wszystkich zakolejkowanych kierunków,
            // żeby zabrać ze sobą ten case, kiedy dwa węże wejdą w to samo miejsce
            //oraz ten, kiedy wchodzimy w cudzy ogon 
            //+ snakeGov2 który sprawdzi, na jakiej pozycji znajdą się zaraz sneki, do sprawdzenia później (gdzie głowa, gdzie ogon)
            // AKTUALIZOWANIE POZYCJI GRACZA I PRZERYSOWYWANIE GO
            //display - powiedz wszystkim, że coś się zmieniło -> move
            
            //do while - current time <= endOfFrame
            //najpierw - czekamy na akcję AI i użytkownika, potem dajemy im info
            //Oczekujemy na to, gdzie snek się chce poruszyć
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if(delta >= 1){
                //listen subThreads
                //dodatkowy while - czeka na kolejne przejśćie
                if(!g_Controller.getBoard().getSnakeStatus(0) && !pauza){
                g_Controller.getBoard().updatePlayerSnake(dir);
                something_changed = true;
            }
                update();
                delta--;
            }
        }
    }



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
