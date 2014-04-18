package tetraword;

import java.util.Random;

import tetraword.Shape.Tetrominoes;

public class Bonus {
	boolean activated;
	
	// Tetrominoes liste les 7 formes possibles des tetrominos + une forme vide appelee NoShape
    enum Bonuses { NoBonus, Worddle };
	
	private int coords[];
	/*
	 * coords[0] = 0;
	 * coords[1] = 0;
	 */
    //private int[][][] coordsTable;
    
    private int id;
    private Bonuses bonusType;
    
	// Constructeur
    public Bonus() {
    	coords[0] = 0;
    	coords[1] = 0;
        setBonus(Bonuses.NoBonus);
    }
    
    public void setBonus(Bonuses bonus) {
        bonusType = bonus;
    }

    private void setX(int x) { coords[0] = x; }
    private void setY(int y) { coords[1] = y; }
    public int x() { return coords[0]; }
    public int y() { return coords[1]; }
    
    public void setRandomBonus()
    {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        Bonuses[] values = Bonuses.values(); 
        setBonus(values[x]);
    }
}
