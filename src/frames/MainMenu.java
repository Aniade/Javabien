package frames;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import design.MenuButton;

@SuppressWarnings("serial")
public class MainMenu extends JFrame {

	private JPanel panel;
    private ImageIcon ii;
    private JLabel picture;
	
	
    public MainMenu() {
    	panel = new JPanel();
    	panel.setLayout(null);
    	panel.setOpaque(false);
    	panel.setSize(400,500);
        getContentPane().add(panel);  
        
        
        //Bouton Nouvelle Partie
        final MenuButton bt_start = new MenuButton("Nouvelle Partie", 175);
        panel.add(bt_start);
        /*On affiche la page du jeu*/
        bt_start.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
                System.out.println("New Game - button is working");
                GameFrame game = null;
				try {
					game = new GameFrame();
				} catch (UnsupportedAudioFileException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                //Ouvrir au même endroit que le menu
                game.setLocationRelativeTo(null);
                game.setVisible(true);
                dispose(); 
        	}
        });
        
        //Bouton Continue
        final MenuButton bt_continue = new MenuButton("Continue", 237);
        panel.add(bt_continue);

        //Bouton Aide
        final MenuButton bt_help = new MenuButton("Aide", 307);
        panel.add(bt_help);
        bt_help.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
                Help help = new Help();
                //Ouvrir au même endroit que le menu
                help.setLocationRelativeTo(null);
                help.setVisible(true);
                dispose();  
        	}
        });

        
        //Bouton Préférences
        final MenuButton bt_preference = new MenuButton("Préférences", 373);
        panel.add(bt_preference);
        bt_preference.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
                Preference preference = new Preference();
                //Ouvrir au même endroit que le menu
                preference.setLocationRelativeTo(null);
                preference.setVisible(true);
                dispose();  
        	}
        });
        
        //Bouton Quitter
        final MenuButton bt_exit = new MenuButton("Quitter", 447);
        panel.add(bt_exit);
        /*On quitte le jeu*/
        bt_exit.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		System.exit(0);
        	}
        });
        
     	//Add background       
        ii = new ImageIcon(this.getClass().getResource("/pictures/bg_accueil.jpg"));
        picture = new JLabel(new ImageIcon(ii.getImage()));
        add(picture);   
        
        setTitle("Tetraword");        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(525,700);
        setResizable(false);
        setVisible(true);
   }
}