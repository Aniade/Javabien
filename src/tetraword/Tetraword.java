package tetraword;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Tetraword extends JFrame implements ActionListener {
	JLabel statusbar;
	JButton startG,exitG;
    JPanel panel;

    public Tetraword() {
    	panel = new JPanel();
        panel.setLayout(null);
        panel.setOpaque(false);
        panel.setSize(200,400);
        getContentPane().add(panel);        
        
        //Start Game Button
        startG = new JButton("Start Game");
        startG.setBounds(50, 100, 100, 30);
        startG.setFocusable(false);
        panel.add(startG);
                
        
        //Exit Game Button
        exitG = new JButton("Exit Game");
        exitG.setBounds(50,200,100,30);
        exitG.setFocusable(false);
        panel.add(exitG);
    	
        //En jeu
    	/*statusbar = new JLabel(" 0");
    	add(statusbar, BorderLayout.SOUTH);
    	Board board = new Board(this);
        add(board);
        board.start();*/
        
        startG.addActionListener(this);
        exitG.addActionListener(this);

        setTitle("Tetraword - Menu principal");
        setSize(200, 420);
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Son
        /*Sound sound = new Sound();
        sound.readAudioFile("country.mp3");*/
   }

   public JLabel getStatusBar() {
       return statusbar;
   }

    public static void main(String[] args) {
        Tetraword menu = new Tetraword();
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == startG)
        {            
        	System.out.println("New Game - button is working");
        	GameFrame game = new GameFrame();
        	game.setLocationRelativeTo(null);
            game.setVisible(true);
            this.dispose();
        }
        else if(e.getSource() == exitG)
        {
            System.exit(0);
        }
    }
}