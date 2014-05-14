package tetraword;

import java.util.Random;
import java.lang.Math;

public class Shape {

	// Tetrominoes liste les 7 formes possibles des tetrominos + une forme vide appelee NoShape
    enum Tetrominoes { NoShape, ZShape, SShape, LineShape, 
               		   TShape, SquareShape, LShape, MirroredLShape };

    private Tetrominoes pieceShape;
    private Brick[] bricks;
    private int[][][] coordsTable;
    
    
    // Constructeur
    public Shape() {
        //coords = new int[4][2];
        bricks = new Brick[4];
        bricks[0] = new Brick();
        bricks[1] = new Brick();
        bricks[2] = new Brick();
        bricks[3] = new Brick();
        setShape(Tetrominoes.NoShape);
    }
    
    public void setShape(Tetrominoes shape) {
    	// Le tableau de coordonnees contient les coordonnees possibles des tetrominos
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
                //coords[i][j] = coordsTable[shape.ordinal()][i][j];
                bricks[i].brickCoords[j] = coordsTable[shape.ordinal()][i][j];
            }
        }
        pieceShape = shape;
    }
    
    public void setIds(int id) {
    	for(int i = 0; i < 4; i++) {
    		bricks[i].setId(id + i);
    	}
    }
    private void setX(int index, int x) { bricks[index].brickCoords[0] = x; }
    private void setY(int index, int y) { bricks[index].brickCoords[1] = y; }
    public int x(int index) { return bricks[index].brickCoords[0]; }
    public int y(int index) { return bricks[index].brickCoords[1]; }
    public Tetrominoes getShape()  { return pieceShape; }
    public int getId(int index) { return bricks[index].getId(); }
    public int[] getIds() {
    	int[] ids = new int[4];
    	for(int i = 0; i < 4; i++) {
    		ids[i] = bricks[i].getId();
    	}
    	return ids;
    }
    public char getLetter(int index) { return bricks[index].getLetter(); }
    public char[] getLetters() {
    	char[] letters = new char[4];
    	for(int i = 0; i < 4; i++) {
    		letters[i] = bricks[i].getLetter();
    	}
		return letters;
    }
    public int getClick(int index) { return bricks[index].getClick(); }
    public int[] getClicks() {
    	int[] clicks = new int[4];
    	for(int i = 0; i < 4; i++) {
    		clicks[i] = bricks[i].getClick();
    	}
		return clicks;
    }
    
    public void setRandomShape()
    {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        Tetrominoes[] values = Tetrominoes.values(); 
        setShape(values[x]);
    }

	public void setRandomLetters() {
		for(int i = 0; i < 4; i++) {
			bricks[i].setRandomLetter();
		}
	}

    public int minX()
    {
      int m = bricks[0].brickCoords[0];
      for (int i=0; i < 4; i++) {
    	  m = Math.min(m, bricks[i].brickCoords[0]);
      }
      return m;
    }


    public int minY() 
    {
    	int m = bricks[0].brickCoords[1];
    	for (int i=0; i < 4; i++) {
    		m = Math.min(m, bricks[i].brickCoords[1]);
    	}
    	return m;
    }

    public Shape rotateLeft() 
    {
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
            result.bricks[i].brickLetter = bricks[i].brickLetter;
            result.bricks[i].brickId = bricks[i].brickId;
        }
        return result;
    }

    public Shape rotateRight()
    {
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
            result.bricks[i].brickLetter = bricks[i].brickLetter;
            result.bricks[i].brickId = bricks[i].brickId;
        }
        return result;
    }
}