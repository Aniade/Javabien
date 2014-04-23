package tetraword;

import java.util.Random;

public class Bonus {
	
	boolean activated;
	
	// Tetrominoes liste les 7 formes possibles des tetrominos + une forme vide appelee NoShape
    enum Bonuses { NoBonus, Worddle, Speed, Score };
	
	private int[]bonusCoords;
    //private int bonusId;
    private Bonuses bonusType;
    
	// Constructeur
    public Bonus() {
    	bonusCoords = new int[2];
    	bonusCoords[0] = 0;
    	bonusCoords[1] = 0;
        setBonus(Bonuses.NoBonus);
        activated = false;
    }
    
    public void setBonus(Bonuses bonus) {
        bonusType = bonus;
    }

    private void setX(int x) { bonusCoords[0] = x; }
    private void setY(int y) { bonusCoords[1] = y; }
    public int x() { return bonusCoords[0]; }
    public int y() { return bonusCoords[1]; }
    
    public void setRandomBonus()
    {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 3 + 1;
        Bonuses[] values = Bonuses.values(); 
        setBonus(values[x]);
    }
    
    public void setRandomX()
    {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 10 + 1;
        System.out.println("Random x : " + x);
        setX(x);
    }
    
    public void setRandomY()
    {
        Random r = new Random();
        int y = Math.abs(r.nextInt()) % 20 + 1;
        System.out.println("Random y : " + y);
        setY(y);
    }
    
    public Bonuses getBonus() { return bonusType; }
}
