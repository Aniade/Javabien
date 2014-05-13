package design;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import tetraword.GameFrame;
import tetraword.MainMenu;


@SuppressWarnings("serial")
public class MenuButton extends Button{
    /*Couleur des bouton menu*/
    private static Color blue =new Color(46,49,146);
    private static Color black =new Color(0,0,0);
    private static Color white =new Color(255,255,255);
    private static Color whiteOpacity =new Color(255,255,255,170);
    
	public MenuButton(String name, int posY) {
		super(name, whiteOpacity, black);
		setOpaque(false); 
		setBounds(140, posY, 250, 53);
		setContentAreaFilled(true);
	    addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mouseEntered(MouseEvent e) {
	    		setOpaque(true); 
	    		setForeground(white);
	    		setBackground(blue);
	    	}
	    	@Override
	    	public void mouseExited(MouseEvent e) {
	    		setOpaque(false); 
	    		setBackground(whiteOpacity);
	    		setForeground(black);
	    	}
	    });
	}
	   
	/*Gestion de l'opacite du bouton, empeche de repeindre le background avec les anciennes couleur */
	 @Override
	 protected void paintComponent(final Graphics g) {
	    g.setColor(getBackground());
	    g.fillRect(0, 0, getWidth(), getHeight());
	    super.paintComponent(g);
	 }
	

	public static void buttonClose(JLabel label, int posY){
	    // Bouton Quitter
	    final MenuButton bt_exit = new MenuButton("Quitter", posY);
	    label.add(bt_exit);
	    // On quitte le jeu
	    bt_exit.addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mouseClicked(MouseEvent e) {
	    		System.exit(0);
	    	}
	    });
	}

	public static void buttonMenu(final JLabel label, int posY){
		final MenuButton bt_menu = new MenuButton("Menu principal", posY);
	    label.add(bt_menu);
	    bt_menu.addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mouseClicked(MouseEvent e) {
	            MainMenu menu = new MainMenu();
	            //Ouvrir au meme endroit que le menu
	            menu.setLocationRelativeTo(null);
	            menu.setVisible(true);
	            Window window = SwingUtilities.windowForComponent(label);
	        	if (window instanceof JFrame) {
	        		JFrame frame = (JFrame) window;
	        		frame.setVisible(false);
	        		frame.dispose();
	        	}  
	    	}
	    });
	}

	public static void buttonNewGame(final JLabel label, int posY){
		//Bouton Nouvelle Partie
        final MenuButton bt_start = new MenuButton("Nouvelle Partie", posY);
        label.add(bt_start);
        /*On affiche la page du jeu*/
        bt_start.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
                //On ouvre une fenetre avec une nouvelle partie
        		GameFrame game = null;
				try {
					game = new GameFrame();
				} catch (UnsupportedAudioFileException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                game.setLocationRelativeTo(null);
                game.setVisible(true);
                //On ferme l'ancienne fenetre
                Window window = SwingUtilities.windowForComponent(label);
            	if (window instanceof JFrame) {
            		JFrame frame = (JFrame) window;
            		frame.setVisible(false);
            		frame.dispose();
            	}
        	}
        });
	}

}