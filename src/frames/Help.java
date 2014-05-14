package frames;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import design.MenuButton;

@SuppressWarnings("serial")
public class Help extends JFrame {
	private JPanel panel;
    private ImageIcon ii;
    private JLabel picture;
    
    public Help(){
    	panel = new JPanel();
    	panel.setLayout(null);
    	panel.setOpaque(false);
    	panel.setSize(400,700);
        getContentPane().add(panel);

        //Retour au menu
        MenuButton menu = new MenuButton("Retour au menu", 610);
        panel.add(menu);
        menu.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
            	MainMenu menu = new MainMenu();
                menu.setLocationRelativeTo(null);
                menu.setVisible(true);
                dispose();
        	}
        });
        
     	//Add background       
        ii = new ImageIcon(this.getClass().getResource("/pictures/aide.jpg"));
        picture = new JLabel(new ImageIcon(ii.getImage()));
        add(picture);  
        
        
        setTitle("Aide");        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(525,700);
        setResizable(false);
        setVisible(true);
                
    }
    
}