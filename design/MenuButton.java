package design;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
    
	public MenuButton(String name, Color color_bg, Color color_typo, int posY, boolean fill) {
		super(name, color_bg, color_typo);
		setBounds(140, posY, 250, 53);
		setContentAreaFilled(fill);
	}
	
	public static void buttonClose(JLabel label, int posY){
	    // Bouton Quitter
	    final MenuButton bt_exit = new MenuButton("Quitter", blue, black, posY, false);
	    label.add(bt_exit);
	    // On quitte le jeu
	    bt_exit.addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mouseEntered(MouseEvent e) {
	    		bt_exit.SetForegroundandFill(white, true);
	    	}
	    	@Override
	    	public void mouseExited(MouseEvent e) {
	    		bt_exit.SetForegroundandFill(black, false);
	    	}
	    	@Override
	    	public void mouseClicked(MouseEvent e) {
	    		System.exit(0);
	    	}
	    });
	}
	
	public static void buttonMenu(final JLabel label, int posY){
		final MenuButton bt_menu = new MenuButton("Menu principal", blue, black, posY, false);
	    label.add(bt_menu);
	    bt_menu.addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mouseEntered(MouseEvent e) {
	    		bt_menu.SetForegroundandFill(white, true);
	    	}
	    	@Override
	    	public void mouseExited(MouseEvent e) {
	    		bt_menu.SetForegroundandFill(black, false);
	    	}
	    	@Override
	    	public void mouseClicked(MouseEvent e) {
	            MainMenu menu = new MainMenu();
	            //Ouvrir au m�me endroit que le menu
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
        final MenuButton bt_start = new MenuButton("Nouvelle Partie", blue, black, posY, false);
        label.add(bt_start);
        /*On affiche la page du jeu*/
        bt_start.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_start.SetForegroundandFill(white, true);
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {
        		bt_start.SetForegroundandFill(black, false);
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
                //On ouvre une fenetre avec une nouvelle partie
                GameFrame game = new GameFrame();
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
