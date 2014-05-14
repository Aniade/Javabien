package tetraword;

import java.util.Random;

/**
 * 
 * Chaque Shape est composée de quatre briques. 
 * Les briques possèdent des coordonnées, un id et une lettre. 
 * La lettre est déterminée aléatoirement en prenant en compte la
 * fréquence d'apparition des lettres dans la langue française.
 *
 */
public class Brick {
	public int[] brickCoords;
	public int brickId;
	public char brickLetter;
	public int brickClick;
	
	Brick() {
        brickCoords = new int[2];
        brickId = -1;
        brickLetter = '\0';
        brickClick = 0;
	}
	
	protected void setCoords(int x, int y) { 
		brickCoords[0] = x;
		brickCoords[1] = y;
	}
	protected void setId(int id) { brickId = id; }
	protected void setLetter(char letter) { brickLetter = letter; }
	protected void setClick(int click) { brickClick = click; }
	
	protected void setRandomLetter() {
		Random rnd = new Random();

		char[] chars = {'E', 'A', 'I', 'N', 'O', 'R', 'S', 'T', 'U', 'L',
						'D', 'M', 'G',
						'B', 'C', 'P',
						'F', 'H', 'V',
						'J', 'Q',
						'K', 'W', 'X', 'Y', 'Z'};
		int[] probabilities = {15, 9, 8, 6, 6, 6, 6, 6, 6, 5,
								  3, 3, 2,
								  2, 2, 2,
								  2, 2, 2,
								  1, 1,
								  1, 1, 1, 1, 1};
		setLetter(chars[chars.length - 1]);
		int r = rnd.nextInt(100);
		int cdf = 0;
		for (int i = 0; i < chars.length; i++) {
		    cdf += probabilities[i];
		    if (r < cdf) {
		        setLetter(chars[i]);
		        break;
		    }
		}
	}
	
	public int getX() { return brickCoords[0]; }
	public int getY() { return brickCoords[1]; }
	public int[] getCoords() { return brickCoords; }
	public int getId() { return brickId; }
	public char getLetter() { return brickLetter; }
	public int getClick() { return brickClick; }
}