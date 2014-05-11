package tetraword;

import java.util.Random;

import javax.swing.ImageIcon;

public class Bonus {
	
	// Tetrominoes liste les 7 formes possibles des tetrominos + une forme vide appelee NoShape
    enum Bonuses { NoBonus, Worddle, BonusSpeed, MalusSpeed, BonusScore, MalusScore };
	
	private int[]bonusCoords;
    private Bonuses bonusType;
    private ImageIcon bonusImg;
	//boolean activated;
	//Timer timer;
    
	// Constructeur
    public Bonus() {
    	bonusCoords = new int[2];
    	bonusCoords[0] = 0;
    	bonusCoords[1] = 0;
        setBonus(Bonuses.NoBonus);
        bonusImg = new ImageIcon();
        //activated = false;
        
        /*timer.schedule(new TimerTask() {
        	public void run() {
        		System.out.println("Destruction du bonus");
        		setBonus(Bonuses.NoBonus);
        	}
        }, 1000);*/
    }
    
    // SETTER
    private void setX(int x) { bonusCoords[0] = x; }
    private void setY(int y) { bonusCoords[1] = y; }
    public void setBonus(Bonuses bonus) { bonusType = bonus; }
    public void setImage() { bonusImg = new ImageIcon(this.getClass().getResource("/pictures/bonus/" + (bonusType.toString()) + ".png")); }
    public void setImage(String filename) { bonusImg = new ImageIcon(this.getClass().getResource("/pictures/bonus/" + filename + ".png")); }

    // GETTER
    public int x() { return bonusCoords[0]; }
    public int y() { return bonusCoords[1]; }
    public Bonuses getBonus() { return bonusType; }
    public ImageIcon getImage() { return bonusImg; }
    

    public void setRandomBonus()
    {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 5 + 1;
        Bonuses[] values = Bonuses.values(); 
        setBonus(values[x]);
        System.out.println("chemin img : " + "/pictures/bonus/" + values[x].toString() + ".png");
        setImage(values[x].toString());
    }
    
    public void setRandomX()
    {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 10;
        //System.out.println("Random x : " + x);
        setX(x);
    }
    
    public void setRandomY()
    {
        Random r = new Random();
        int y = Math.abs(r.nextInt()) % 20;
        //System.out.println("Random y : " + y);
        setY(y);
    }
}
