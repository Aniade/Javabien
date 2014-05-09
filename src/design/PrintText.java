package design;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;

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