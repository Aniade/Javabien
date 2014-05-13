package design;

import java.awt.Color;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class Button extends JButton{ 

	public Button(String name, Color color_bg, Color color_typo){
		super(name);
		//Couleur de la typo
        setForeground(color_typo);
        setBackground(color_bg);
        //setBounds(140, 200, 250, 60);
        setFocusable(false);
	}

}