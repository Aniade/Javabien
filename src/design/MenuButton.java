package design;

import java.awt.Color;

@SuppressWarnings("serial")
public class MenuButton extends Button{
	public MenuButton(String name, Color color_bg, Color color_typo, int posY, boolean fill) {
		super(name, color_bg, color_typo);
		setBounds(140, posY, 250, 53);
		setContentAreaFilled(fill);
	}
}