package tetraword;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Help extends JFrame {
	private JPanel panel;
    private ImageIcon ii;
    private JLabel picture;
    
    public Help(){
    	panel = new JPanel();
    	panel.setLayout(null);
    	panel.setOpaque(false);
    	panel.setSize(400,500);
        add(panel);
        
     	//Add background       
        ii = new ImageIcon(this.getClass().getResource("bg_accueil.jpg"));
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
