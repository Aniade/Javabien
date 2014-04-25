package tetraword;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Bonus {
	
	// Tetrominoes liste les 7 formes possibles des tetrominos + une forme vide appelee NoShape
    enum Bonuses { NoBonus, Worddle, Speed, BonusScore, MalusScore };
	
	private int[]bonusCoords;
    private Bonuses bonusType;
    //private JLabel bonusPicture;
	boolean activated;
	Timer timer;
    
	// Constructeur
    public Bonus() {
    	bonusCoords = new int[2];
    	bonusCoords[0] = 0;
    	bonusCoords[1] = 0;
        setBonus(Bonuses.NoBonus);
        //bonusPicture = null;
        activated = false;
        /*timer = new Timer();
        timer.schedule(new TimerTask() {
        	public void run() {
        		System.out.println("Destruction du bonus");
        		setBonus(Bonuses.NoBonus);
        	}
        }, 2*60*1000);*/
    }
    
    // SETTER
    private void setX(int x) { bonusCoords[0] = x; }
    private void setY(int y) { bonusCoords[1] = y; }
    public void setBonus(Bonuses bonus) { bonusType = bonus; }
    /*public void setPicture(String filename) {
    	ImageIcon iiBonus = new ImageIcon(this.getClass().getResource("pictures/bonus/" + filename + ".png"));
    	bonusPicture = new JLabel(new ImageIcon(iiBonus.getImage()));
	    //pause.setSize(525, 700);
	    //add(bonusPicture);
	    bonusPicture.setVisible(true);
    }*/

    // GETTER
    public int x() { return bonusCoords[0]; }
    public int y() { return bonusCoords[1]; }
    public Bonuses getBonus() { return bonusType; }
    //public JLabel getPicture() { return bonusPicture; }
    

    public void setRandomBonus()
    {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 3 + 1;
        Bonuses[] values = Bonuses.values(); 
        setBonus(values[x]);
        //setPicture(values[x].toString());
    }
    
    public void setRandomX()
    {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 10;
        System.out.println("Random x : " + x);
        setX(x);
    }
    
    public void setRandomY()
    {
        Random r = new Random();
        int y = Math.abs(r.nextInt()) % 20;
        System.out.println("Random y : " + y);
        setY(y);
    }
}
