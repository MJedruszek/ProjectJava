package snek;

import javax.swing.JFrame;

public class Frame extends JFrame {
    //okno, ustawienia początkowe
    private int WIDTH=460;
    private int HEIGHT=280;

    public Frame(){
        MenuPanel panel = new MenuPanel();
        this.setSize(WIDTH, HEIGHT);
        this.add(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Snek");
        this.setVisible(true);
    }
    //łatwo
}
