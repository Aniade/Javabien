package tetraword;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import design.Button;



@SuppressWarnings("serial")
public class Preference extends JFrame {
	private JPanel panel;
    private ImageIcon ii;
    private JLabel picture;
    private int debutant = 0, amateur = 0,  expert = 0;
    JButton bt_expert, bt_debutant, bt_amateur, bt_kungfu, bt_cowboy, bt_batman, bt_pirate, bt_francais, bt_english;
    private int cowboy = 0,  kungfu = 0, batman = 0,  pirate = 0, francais = 0, english = 0;
    private ImageIcon select_english_bw, select_english, select_francais, select_francais_bw, select_kungfu, select_kungfu_bw, select_cowboy, select_cowboy_bw, select_pirate, select_pirate_bw, select_batman, select_batman_bw;

    
    public Preference(){  	    	
    	// création d'une instance de Properties
    	final Properties options = new Properties(); 
    	// Création d'une instance de File pour le fichier de config
    	File fichierConfig = new File("conf/conf.properties"); 

    	//Chargement du fichier de configuration
    	try {
    	options.load(new FileInputStream(fichierConfig));
    	} 
    	catch(IOException e) {
    		System.out.println("Echec chargement");
    	}
    	

    	
    	panel = new JPanel();
    	panel.setLayout(null);
    	panel.setOpaque(false);
    	panel.setSize(525,600);
        getContentPane().add(panel);
        
        Font font = new Font("Arial",Font.BOLD,18);
        Color blue =new Color(46,49,146);
          	 	
   	 	
        //Choix du niveau
		JLabel level = new JLabel("Choix du niveau");
		level.setFont(font);
		level.setForeground(blue);
		level.setBounds(180, 0, 300, 100);
		panel.add(level);

		final ImageIcon select_debutant = new ImageIcon(this.getClass().getResource("/pictures/select_debutant.png"));
        bt_debutant = new JButton(select_debutant);
        bt_debutant.setBounds(50, 80, 130, 44);
        bt_debutant.setBorder(null);
        bt_debutant.setContentAreaFilled(false);
        
	        bt_debutant.addMouseListener(new MouseAdapter() {
	        	@Override
	        	public void mouseEntered(MouseEvent e) {	        		
	        		bt_debutant.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));	
	        		}
	        	@Override
	        	public void mouseExited(MouseEvent e) {
	        		if(debutant!=1){
	        			bt_debutant.setBorder(null);
	        		}
	        	}
	        	@Override
	        	public void mouseClicked(MouseEvent e) {
	        		bt_amateur.setBorder(null);
	        		bt_expert.setBorder(null);
	        		debutant = 1;
	        		amateur = 0;
	        		expert = 0;
	        		bt_debutant.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	            	/*Enregistrer une propriété dans le fichier de configurations */
	        	    options.setProperty("Level", "debutant");
	        	    	try{
	        	    	options.store(new FileOutputStream("conf/conf.properties"), "Preferences");
	        	    	} 
	        	    	catch(IOException ex) {
	        	    		
	        	    	}
	        	}
	        });
        
               
        
		ImageIcon select_amateur = new ImageIcon(this.getClass().getResource("/pictures/select_amateur.png"));
        bt_amateur = new JButton(select_amateur);
        bt_amateur.setBorder(null);
        bt_amateur.setBounds(200, 80, 130, 44);
        bt_amateur.setContentAreaFilled(false);
        bt_amateur.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_amateur.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {
        		if(amateur!=1){
        			bt_amateur.setBorder(null);
        		}
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		amateur = 1;
        		debutant = 0;
        		expert = 0;
        		bt_expert.setBorder(null);
        		bt_debutant.setBorder(null);
        		bt_amateur.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            	/*Enregistrer une propriété dans le fichier de configurations */
        	    options.setProperty("Level", "amateur");
        	    	try{
        	    	options.store(new FileOutputStream("conf/conf.properties"), "Preferences");
        	    	} 
        	    	catch(IOException ex) {
        	    		
        	    	}
        	}
        });
        
		ImageIcon select_expert = new ImageIcon(this.getClass().getResource("/pictures/select_expert.png"));
        bt_expert = new JButton(select_expert);
        bt_expert.setBorder(null);
        bt_expert.setBounds(350, 80, 130, 44);
        bt_expert.setContentAreaFilled(false);
        bt_expert.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_expert.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {
        		if(expert!=1){
        			bt_expert.setBorder(null);
        		}
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		bt_amateur.setBorder(null);
        		bt_debutant.setBorder(null);
        		expert = 1;
        		amateur = 0;
        		debutant = 0;
        		bt_expert.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            	/*Enregistrer une propriété dans le fichier de configurations */
        	    options.setProperty("Level", "expert");
        	    	try{
        	    	options.store(new FileOutputStream("conf/conf.properties"), "Preferences");
        	    	} 
        	    	catch(IOException ex) {
        	    		
        	    	} 
        	}
        });
		
		panel.add(bt_debutant);
		panel.add(bt_amateur);
		panel.add(bt_expert);
	
		
        //Affichage des reglages
    	/* Récupérer une propriété d'un fichier de configurations           */
   	 	String configLevel = options.getProperty("Level"); // Trouve la valeur associée à la clef "Level"
   	 	
   	 	
   	 	switch (configLevel)
	   	{
	   	  case "debutant":
	   		bt_debutant.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	   		debutant = 1;
	   		amateur = 0;
	   		expert = 0;
	   	    break;
	   	  case "amateur":
	   		bt_amateur.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	   		amateur = 1;
	   		debutant = 0;
	   		expert = 0;
	   	    break; 
	   	  case "expert":
		   	bt_expert.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		   	amateur = 0;
		   	debutant = 0;
		   	expert = 1;
	   	    break; 
	   	  default:
		   	bt_debutant.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		   	debutant = 1;
		   	amateur = 0;
		   	expert = 0;            
	   	}
		
        //Choix de l'univers
		JLabel univers = new JLabel("Choix de l'univers");
		univers.setFont(font);
		univers.setForeground(blue);
		univers.setBounds(175, 120, 200, 100);
		panel.add(univers);
		
		select_pirate = new ImageIcon(this.getClass().getResource("/pictures/select_pirate.jpg"));
		select_pirate_bw = new ImageIcon(this.getClass().getResource("/pictures/select_pirate_bw.jpg"));
        bt_pirate = new JButton(select_pirate_bw);
        bt_pirate.setBounds(150, 195, 99, 99);
        bt_pirate.setFocusable(false);
        panel.add(bt_pirate);
        bt_pirate.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_pirate.setIcon(select_pirate);	
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {
        		if(pirate!=1){
        			bt_pirate.setIcon(select_pirate_bw);
        		}
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		pirate=1;
     	   		cowboy = 0;
     	   		kungfu = 0;
     	   		batman = 0;
        		bt_pirate.setIcon(select_pirate);
        		bt_cowboy.setIcon(select_cowboy_bw);
        		bt_kungfu.setIcon(select_kungfu_bw);
        		bt_batman.setIcon(select_batman_bw);
        	    options.setProperty("Univers", "pirate");
    	    	try{
    	    	options.store(new FileOutputStream("conf/conf.properties"), "Univers");
    	    	} 
    	    	catch(IOException ex) {
    	    		
    	    	} 
        	}
        });
		
        
		select_kungfu_bw = new ImageIcon(this.getClass().getResource("/pictures/select_kungfu_bw.jpg"));
		select_kungfu = new ImageIcon(this.getClass().getResource("/pictures/select_kungfu.jpg"));
        bt_kungfu = new JButton(select_kungfu_bw);
        bt_kungfu.setBounds(260, 195, 99, 99);
        bt_kungfu.setFocusable(false);
        panel.add(bt_kungfu);
        bt_kungfu.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_kungfu.setIcon(select_kungfu);	
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {	
        		if(kungfu!=1){
        			bt_kungfu.setIcon(select_kungfu_bw);
        		}
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		kungfu = 1;
     	   		cowboy = 0;
     	   		pirate = 0;
     	   		batman = 0;
        		bt_kungfu.setIcon(select_kungfu);
        		bt_pirate.setIcon(select_pirate_bw);
        		bt_cowboy.setIcon(select_cowboy_bw);
        		bt_batman.setIcon(select_batman_bw);
        	    options.setProperty("Univers", "kungfu");
    	    	try{
    	    	options.store(new FileOutputStream("conf/conf.properties"), "Univers");
    	    	} 
    	    	catch(IOException ex) {
    	    		
    	    	}       		
 
        	}
        });
		
        
        select_cowboy_bw = new ImageIcon(this.getClass().getResource("/pictures/select_cowboy_bw.jpg"));
        select_cowboy = new ImageIcon(this.getClass().getResource("/pictures/select_cowboy.jpg"));
        bt_cowboy = new JButton(select_cowboy_bw);
        bt_cowboy.setBounds(260, 305, 99, 99);
        bt_cowboy.setFocusable(false);
        panel.add(bt_cowboy);
        bt_cowboy.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_cowboy.setIcon(select_cowboy);	
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {	
        		if(cowboy!=1){
        			bt_cowboy.setIcon(select_cowboy_bw);
        		}
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		cowboy = 1;
     	   		kungfu = 0;
     	   		pirate = 0;
     	   		batman = 0;
        		bt_cowboy.setIcon(select_cowboy);
        		bt_pirate.setIcon(select_pirate_bw);
        		bt_kungfu.setIcon(select_kungfu_bw);
        		bt_batman.setIcon(select_batman_bw);
        	    options.setProperty("Univers", "western");
    	    	try{
    	    	options.store(new FileOutputStream("conf/conf.properties"), "Univers");
    	    	} 
    	    	catch(IOException ex) {
    	    		
    	    	}
        	}
        });
		
        
        select_batman_bw = new ImageIcon(this.getClass().getResource("/pictures/select_batman_bw.jpg"));
        select_batman = new ImageIcon(this.getClass().getResource("/pictures/select_batman.jpg"));
        bt_batman = new JButton(select_batman_bw);
        bt_batman.setBounds(150, 305, 99, 99);
        bt_batman.setFocusable(false);
        panel.add(bt_batman);
        bt_batman.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_batman.setIcon(select_batman);	
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {	
        		if(batman!=1){
        			bt_batman.setIcon(select_batman_bw);
        		}
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		batman = 1;
     	   		cowboy = 0;
     	   		kungfu = 0;
     	   		pirate = 0;
        		bt_batman.setIcon(select_batman);
        		bt_pirate.setIcon(select_pirate_bw);
        		bt_cowboy.setIcon(select_cowboy_bw);
        		bt_kungfu.setIcon(select_kungfu_bw);
        	    options.setProperty("Univers", "batman");
    	    	try{
    	    	options.store(new FileOutputStream("conf/conf.properties"), "Univers");
    	    	} 
    	    	catch(IOException ex) {
    	    		
    	    	}
        	}
        });
        
        
        //Affichage des reglages
     	/* Récupérer une propriété d'un fichier de configuration  */
    	 String configUniv = options.getProperty("Univers"); // Trouve la valeur associée à la clef "Univers"
    	 	
    	 	
    	switch (configUniv)
 	   	{
 	   	  case "western":
 	   		bt_cowboy.setIcon(select_cowboy);	
 	   		cowboy = 1;
 	   		kungfu = 0;
 	   		pirate = 0;
 	   		batman = 0;
 	   	    break;
 	   	  case "kungfu":
 	   		bt_kungfu.setIcon(select_kungfu);	
 	   		cowboy = 0;
 	   		kungfu = 1;
 	   		pirate = 0;
 	   		batman = 0;
 	   	    break; 
 	   	  case "pirate":
 	   		bt_pirate.setIcon(select_pirate);	
 	   		cowboy = 0;
 	   		kungfu = 0;
 	   		pirate = 1;
 	   		batman = 0;
 	   	    break; 
 	   	  case "batman":
 	   		bt_batman.setIcon(select_batman);	
 	   		cowboy = 0;
 	   		kungfu = 0;
 	   		pirate = 0;
 	   		batman = 1;
 	   	    break; 
 	   	  default:
 	   		bt_cowboy.setIcon(select_cowboy);	
 	   		cowboy = 0;
 	   		kungfu = 0;
 	   		pirate = 0;
 	   		batman = 0;           
 	   	}
		
        
        //Choix du dictionnaire
		JLabel dictionary = new JLabel("Choix du dictionnaire");
		dictionary.setFont(font);
		dictionary.setForeground(blue);
		dictionary.setBounds(165, 390, 200, 100);
		panel.add(dictionary);
		
		select_francais = new ImageIcon(this.getClass().getResource("/pictures/select_francais.jpg"));
		select_francais_bw = new ImageIcon(this.getClass().getResource("/pictures/select_francais_bw.jpg"));
        bt_francais = new JButton(select_francais_bw);
        bt_francais.setBounds(195, 460, 52, 33);
        bt_francais.setFocusable(false);
        panel.add(bt_francais);
        bt_francais.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_francais.setIcon(select_francais);	
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {	
        		if(francais != 1){
        			bt_francais.setIcon(select_francais_bw);
        		}
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		francais = 1;
     	   		english = 0;
     	   		bt_francais.setIcon(select_francais);	
        		bt_english.setIcon(select_english_bw);
        	    options.setProperty("Dictionary", "francais");
    	    	try{
    	    	options.store(new FileOutputStream("conf/conf.properties"), "Dictionary");
    	    	} 
    	    	catch(IOException ex) {
    	    		
    	    	}
 
        	}
        });
        

        
        
		select_english_bw = new ImageIcon(this.getClass().getResource("/pictures/select_english_bw.jpg"));
		select_english = new ImageIcon(this.getClass().getResource("/pictures/select_english.jpg"));
        bt_english = new JButton(select_english_bw);
        bt_english.setBounds(265, 460, 52, 33);
        bt_english.setFocusable(false);
        panel.add(bt_english);
        bt_english.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_english.setIcon(select_english);	
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {	
        		if(english !=1){
        			bt_english.setIcon(select_english_bw);
        		}
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		francais = 0;
     	   		english = 1;
     	   		bt_francais.setIcon(select_francais_bw);	
        		bt_english.setIcon(select_english);
        	    options.setProperty("Dictionary", "anglais");
    	    	try{
    	    	options.store(new FileOutputStream("conf/conf.properties"), "Dictionary");
    	    	} 
    	    	catch(IOException ex) {
    	    		
    	    	}
        	}
        });
        
        //Affichage des reglages
    	/* Récupérer une propriété d'un fichier de configurations           */
   	 	String configDico = options.getProperty("Dictionary"); // Trouve la valeur associée à la clef "Dictionary"

   	 	   	 	
   	 	switch (configDico)
	   	{
	   	  case "francais":
   	   		bt_francais.setIcon(select_francais);	
      		bt_english.setIcon(select_english_bw);
	   		francais = 1;
	   		english = 0;
	   	    break;
	   	  case "anglais":
   	   		bt_francais.setIcon(select_francais_bw);	
      		bt_english.setIcon(select_english);
	   		francais = 0;
	   		english = 1;
	   	    break; 
	   	  default:
	   	   	bt_francais.setIcon(select_francais);	
	      	bt_english.setIcon(select_english_bw);
	   		francais = 1;
	   		english = 0;           
	   	}
        
        //Enregistrer
        Button enregistrer = new Button("Enregistrer", blue, Color.white);
        enregistrer.setBounds(200, 515, 110, 40);
        panel.add(enregistrer);
        enregistrer.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
            	MainMenu menu = new MainMenu();
                menu.setLocationRelativeTo(null);
                dispose();  
        	}
        });
        
     	//Add background       
        ii = new ImageIcon(this.getClass().getResource("/pictures/bg_preference.jpg"));
        picture = new JLabel(new ImageIcon(ii.getImage()));
        add(picture);  
        
        setTitle("Préférences");        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(525,700);
        setResizable(false);
        setVisible(true);
                
    }
    
}
