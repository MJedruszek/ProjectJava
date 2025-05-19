package snek;

import javax.swing.JFrame;

public class Frame extends JFrame implements Runnable{
    //okno, ustawienia początkowe
    private int WIDTH=1024+48;
    private int HEIGHT=512+64;
    private GameController g_Controller;
    private int FPS = 30;
    private MenuPanel mPanel;
    private GamePanel gPanel;
    private Thread menuThread = new Thread(this); //implementuje runnable
    //czy należy zmienić panel
    private boolean panelChanged;
    //czy panel nie został zmieniony
    private boolean hasChanged;

    public Frame(){
        g_Controller = new GameController();
        menuThread.start();
        mPanel = new MenuPanel(g_Controller);
        gPanel = new GamePanel(g_Controller, WIDTH, HEIGHT);
        this.setSize(WIDTH, HEIGHT);
        this.getContentPane().add(mPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Snek");
        this.setVisible(true);
        this.setResizable(false);
        panelChanged = false;
        hasChanged = true;
    }

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
                if(hasChanged) panelChanged = true;
                //System.out.println(g_Controller.getState());
            }
            else if(g_Controller.getState() == GameState.TWO_PLAYER){
                this.getContentPane().removeAll();
                this.getContentPane().add(gPanel);
                if(hasChanged) panelChanged = true;
                //System.out.println(g_Controller.getState());
            }
            else if(g_Controller.getState() == GameState.THREE_PLAYER){
                this.getContentPane().removeAll();
                this.getContentPane().add(gPanel);
                if(hasChanged) panelChanged = true;
                //System.out.println(g_Controller.getState());
            }
            else if(g_Controller.getState() == GameState.BEGGINING){
                //System.out.println(g_Controller.getState());
            }
            
            // AKTUALIZOWANIE POZYCJI GRACZA I PRZERYSOWYWANIE GO
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if(delta >= 1){
                update();
                //repaint();
                delta--;
            }
        }
    }

    private void update() {
        if(panelChanged){
            hasChanged = false;
            panelChanged = false;
            repaint();
        }
    }
}
