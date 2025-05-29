package snek;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements KeyListener, Runnable{
    private GameController g_Controller; 
    private int width;
    private int height;   
    private Thread gameThread = new Thread(this); //implementuje runnable
    private int FPS = 30;
    private boolean something_changed;
    private boolean pauza;
    private boolean not_pauza;


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
        pauza = false;
        not_pauza = false;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    public void paintComponent(Graphics g){
        //jedna kratka ma 8x8 pixeli
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
        g.setColor(Color.cyan);
        for(int i = 0; i<g_Controller.getBoard().getSnakeSize(0); i++){
            int x = g_Controller.getBoard().getSnakePart(i).getX();
            int y = g_Controller.getBoard().getSnakePart(i).getY();
            g.fillRect(16+x*16, 16*y+16, 15, 15);
            g.setColor(Color.GREEN);
        }

        if(pauza){
            g.setColor(Color.blue);
            g.fillRect(16+16, 16+16, 30, 30);
            pauza = false;
        }
        if(not_pauza){
            not_pauza = false;
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
            
            
            // AKTUALIZOWANIE POZYCJI GRACZA I PRZERYSOWYWANIE GO
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if(delta >= 1){
                update();
                delta--;
            }
        }
    }

    private void update() {
        //TODO:
        //dodać tutaj repainta, jeśli board o tym powie, i wtedy powiedzieć boardowi żeby zmienił swoje zdanie
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
            //System.out.println("NOT PAUZA");
            not_pauza = true;
            something_changed = true;
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
       
    }





}
