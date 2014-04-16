package tetraword;

import java.util.Random;
import java.lang.Math;

public class Shape {
	
	// Tetrominoes liste les 7 formes possibles des tetrominos + une forme vide appelee NoShape
    enum Tetrominoes { NoShape, ZShape, SShape, LineShape, 
               		   TShape, SquareShape, LShape, MirroredLShape };

    private Tetrominoes pieceShape;
    private char pieceLetter;
    private int coords[][];
    private int[][][] coordsTable;
    
    private int id;
    
    
    // Constructeur
    public Shape() {
        coords = new int[4][2];
        setShape(Tetrominoes.NoShape);
        setLetter('\0');
    }

    public void setShape(Tetrominoes shape) {
    	// Le tableau de coordonnées contient les coordonnées possibles des tetrominos
    	coordsTable = new int[][][] {
            { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
            { { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } },
            { { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },
            { { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } },
            { { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
            { { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
            { { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },
            { { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } }
        };

        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }
        pieceShape = shape;
    }
    
    public void setLetter(char letter) { pieceLetter = letter; }
    public void setId(int id) { this.id = id; }
    private void setX(int index, int x) { coords[index][0] = x; }
    private void setY(int index, int y) { coords[index][1] = y; }
    public int x(int index) { return coords[index][0]; }
    public int y(int index) { return coords[index][1]; }
    public Tetrominoes getShape()  { return pieceShape; }
    public char getLetter() { return pieceLetter; }
    public int getId() { return id; }

    public void setRandomShape()
    {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        Tetrominoes[] values = Tetrominoes.values(); 
        setShape(values[x]);
    }
	
	public void setRandomLetter() {
		Random rnd = new Random();
		
		char[] chars = {'E', 'A', 'I', 'N', 'O', 'R', 'S', 'T', 'U', 'L',
						'D', 'M', 'G',
						'B', 'C', 'P',
						'F', 'H', 'V',
						'J', 'Q',
						'K', 'W', 'X', 'Y', 'Z'};
		double[] probabilities = {15, 9, 8, 6, 6, 6, 6, 6, 6, 5,
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

		/*Random r = new Random();
		char c = (char)((int)'0' + r.nextInt(26));
		setLetter(c);*/
	}

    public int minX()
    {
      int m = coords[0][0];
      for (int i=0; i < 4; i++) {
    	  m = Math.min(m, coords[i][0]);
      }
      return m;
    }


    public int minY() 
    {
    	int m = coords[0][1];
    	for (int i=0; i < 4; i++) {
    		m = Math.min(m, coords[i][1]);
    	}
    	return m;
    }

    public Shape rotateLeft() 
    {
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;
        result.pieceLetter = pieceLetter;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    public Shape rotateRight()
    {
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;
        result.pieceLetter = pieceLetter;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }
}