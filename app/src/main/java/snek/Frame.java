package snek;

import javax.swing.JFrame;

public class Frame extends JFrame implements Runnable{
    //okno, ustawienia początkowe
    private int WIDTH=460;
    private int HEIGHT=280;
    private GameController g_Controller;
    private int FPS = 30;
    private Thread gameThread = new Thread(this); //implementuje runnable

    public Frame(){
        g_Controller = new GameController();
        gameThread.start();
        MenuPanel panel = new MenuPanel(g_Controller);
        this.setSize(WIDTH, HEIGHT);
        this.add(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Snek");
        this.setVisible(true);
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
                //update();
                //repaint();
                System.out.println(g_Controller.getState());
                delta--;
            }
        }
    }
}
