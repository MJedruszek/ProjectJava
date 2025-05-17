package snek;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements KeyListener{
    private GameController g_Controller;    


    public GamePanel(GameController g, int w, int h){
        g_Controller = g;
        this.setBackground(Color.BLACK);
        this.setSize(new Dimension(w,h));
    }


    public void paint(Graphics g){
        super.paint(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {
       
    }


    @Override
    public void keyPressed(KeyEvent e) {
        
    }


    @Override
    public void keyReleased(KeyEvent e) {
       
    }





}
