package snek;


import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MenuPanel extends JPanel implements ActionListener{
    private JButton onePlayer;
    private JButton twoPlayers;
    private JButton threePlayers;
    private JButton quit;
    private GroupLayout bLayout;
    private GroupLayout.SequentialGroup hGroup;
    private GroupLayout.SequentialGroup vGroup;
    private JLabel buttonsTitle;
    private GameController g_Controller;
    private JLabel label;

    public MenuPanel(GameController g){
        g_Controller = g;
        label = new JLabel("<html>High scores: </html>", SwingConstants.CENTER);
        onePlayer = new JButton("one player");
        onePlayer.addActionListener(this);
        twoPlayers = new JButton("two players");
        twoPlayers.addActionListener(this);
        threePlayers = new JButton("three players");
        threePlayers.addActionListener(this);
        quit = new JButton("quit game");
        quit.addActionListener(this);
        bLayout = new GroupLayout(this);
        buttonsTitle = new JLabel("Choose difficulty level:", SwingConstants.CENTER);

        this.add(onePlayer);
        this.add(twoPlayers);
        this.add(threePlayers);
        this.add(quit);
        this.setLayout(bLayout);
        this.add(buttonsTitle);

        //tworzy dziury między przyciskami
        bLayout.setAutoCreateGaps(true);
        //tworzy dziury między kontenerami
        bLayout.setAutoCreateContainerGaps(true);

        hGroup = bLayout.createSequentialGroup();

        hGroup.addGroup(bLayout.createParallelGroup().addComponent(onePlayer,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        hGroup.addGroup(bLayout.createParallelGroup(Alignment.TRAILING).
            addComponent(buttonsTitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).
            addComponent(twoPlayers,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).
            addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).
            addComponent(quit,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        hGroup.addGroup(bLayout.createParallelGroup().addComponent(threePlayers,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        

        bLayout.setHorizontalGroup(hGroup);

        vGroup = bLayout.createSequentialGroup();
        vGroup.addGroup(bLayout.createParallelGroup(Alignment.BASELINE).
            addComponent(buttonsTitle,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        vGroup.addGroup(bLayout.createParallelGroup(Alignment.BASELINE).
            addComponent(onePlayer,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(twoPlayers,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(threePlayers,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        vGroup.addGroup(bLayout.createParallelGroup(Alignment.BASELINE).
            addComponent(label,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).
            addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                     GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        vGroup.addGroup(bLayout.createParallelGroup(Alignment.BASELINE).
            addComponent(quit,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        
        bLayout.setVerticalGroup(vGroup);

        //obsługa funkcji przycisków
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == onePlayer){
            g_Controller.setState(GameState.ONE_PLAYER);
        }
        else if(e.getSource() == twoPlayers){
            g_Controller.setState(GameState.TWO_PLAYER);
        }
        else if(e.getSource() == threePlayers){
            g_Controller.setState(GameState.THREE_PLAYER);
        }
        else if(e.getSource() == quit){
            System.exit(0);
        }
    }

    public void displayScores(List<Integer> l){
        String s = new String();
        int size = l.size();
        if(size>10) size = 10;
        s = "<html>High scores: <br>";
        if(!l.isEmpty()){
            for(int i = 0; i<size; i++){
                s = s + "<br>";
                s = s + l.get(i).toString();
            }
        }
        s = s + "</html>";
        label.setText(s);
    }
}
