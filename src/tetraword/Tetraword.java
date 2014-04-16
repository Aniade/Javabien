package tetraword;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Tetraword extends JFrame implements ActionListener {
	
	private JPanel panel;
	/*private JLabel statusbar;*/
	private JButton bt_start, bt_continue, bt_help, bt_preference,bt_exit;
    private ImageIcon ii;
    private JLabel picture;
	
	
    public Tetraword() {
    	panel = new JPanel();
    	panel.setLayout(null);
    	panel.setOpaque(false);
    	panel.setSize(525,700);
        getContentPane().add(panel);  
        
        //Bouton Nouvelle Partie
        bt_start = new JButton("Nouvelle Partie");
        bt_start.setBounds(140, 200, 250, 30);
        bt_start.setFocusable(false);
        panel.add(bt_start);
        
        //Bouton Continuer
        bt_continue = new JButton("Continuer");
        bt_continue.setBounds(140, 250, 250, 30);
        bt_continue.setFocusable(false);
        panel.add(bt_continue);
        
        //Bouton Aide
        bt_help = new JButton("Aide");
        bt_help.setBounds(140, 300, 250, 30);
        bt_help.setFocusable(false);
        panel.add(bt_help);
        
        //Bouton Preferences
        bt_preference = new JButton("Préférences");
        bt_preference.setBounds(140, 350, 250, 30);
        bt_preference.setFocusable(false);
        panel.add(bt_preference);      
        
        //Bouton Quitter
        bt_exit = new JButton("Quitter");
        bt_exit.setBounds(140,400,250,30);
        bt_exit.setFocusable(false);
        panel.add(bt_exit);
        
     	// Ajout d'une image de fond
        ii = new ImageIcon(this.getClass().getResource("pictures/bg_accueil.jpg"));
        picture = new JLabel(new ImageIcon(ii.getImage()));
        add(picture);   

        bt_start.addActionListener(this);
        bt_exit.addActionListener(this);
        
        setTitle("Tetraword");        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(525,700);
        setResizable(false);
        setVisible(true);
   }

   /*public JLabel getStatusBar() {
       return statusbar;
   }*/

   public static void main(String[] args) {
       Tetraword menu = new Tetraword();
       menu.setLocationRelativeTo(null);
       menu.setVisible(true);
   }
   
   public void actionPerformed(ActionEvent e)
   {
       if(e.getSource() == bt_start)
       {            
       	System.out.println("Lancement d'une nouvelle partie");
       	GameFrame game = new GameFrame();
       	game.setLocationRelativeTo(null);
           game.setVisible(true);
           this.dispose();
       }
       else if(e.getSource() == bt_exit)
       {
           System.exit(0);
       }
   }
}