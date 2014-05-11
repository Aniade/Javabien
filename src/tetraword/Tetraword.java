package tetraword;


import javax.swing.JFrame;



@SuppressWarnings("serial")
public class Tetraword extends JFrame{
	


	
    public Tetraword() {
    	MainMenu menu = new MainMenu();
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);
            	
   }
    
    




     public static void main(String[] args) {
         Tetraword tetraword = new Tetraword();
         tetraword.setLocationRelativeTo(null);
     }
     
 }