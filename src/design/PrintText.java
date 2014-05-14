package design;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;

/**
 * 
 * Hérite de JLabel, permet de pouvoir afficher du texte avec son label, 
 * sa position, sa police et sa couleur.
 *
 */
@SuppressWarnings("serial")
public class PrintText  extends JLabel {

	public PrintText(String label, int posX, int posY, Font font, Color foreground, JLabel parent){
		super(label);
    	new JLabel(label);
    	setBounds(posX,posY, 100, 100); 
    	setFont(font);
    	setForeground(foreground);
    	parent.add(this);
	}
}