package snek;


import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MenuPanel extends JPanel{
    private JButton onePlayer;
    private JButton twoPlayers;
    private JButton threePlayers;
    private JButton quit;
    private GroupLayout bLayout;
    private GroupLayout.SequentialGroup hGroup;
    private GroupLayout.SequentialGroup vGroup;

    public MenuPanel(){
        onePlayer = new JButton("one player");
        twoPlayers = new JButton("two players");
        threePlayers = new JButton("three players");
        quit = new JButton("quit game");
        bLayout = new GroupLayout(this);

        this.add(onePlayer);
        this.add(twoPlayers);
        this.add(threePlayers);
        this.add(quit);
        this.setLayout(bLayout);

        //tworzy dziury między przyciskami
        bLayout.setAutoCreateGaps(true);
        //tworzy dziury między kontenerami
        bLayout.setAutoCreateContainerGaps(true);

        hGroup = bLayout.createSequentialGroup();

        hGroup.addGroup(bLayout.createParallelGroup().addComponent(onePlayer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        hGroup.addGroup(bLayout.createParallelGroup(Alignment.TRAILING).
            addComponent(twoPlayers, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).
            addComponent(quit, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        hGroup.addGroup(bLayout.createParallelGroup().addComponent(threePlayers, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        

        bLayout.setHorizontalGroup(hGroup);

        vGroup = bLayout.createSequentialGroup();
        vGroup.addGroup(bLayout.createParallelGroup(Alignment.BASELINE).
            addComponent(onePlayer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(twoPlayers, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(threePlayers, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        vGroup.addGroup(bLayout.createParallelGroup(Alignment.BASELINE).
            addComponent(quit, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        
        bLayout.setVerticalGroup(vGroup);
    }
}
