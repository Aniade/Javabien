package tetraword;

import java.awt.Color;
//import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import tetraword.Shape.Tetrominoes;

@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener {
	
    private ImageIcon ii;
    private JLabel picture;
    
	final int BoardWidth = 10;
    final int BoardHeight = 20;

    Timer timer;
    boolean isFallingFinished = false; // la piece a-t-elle fini de tomber ?
    boolean isStarted = false;
    boolean isPaused = false;
    int numLinesRemoved = 0; // compte le nombre de lignes supprimees
    int curX = 0; // position actuelle de la piece qui tombe
    int curY = 0; // position actuelle de la piece qui tombe
    //JLabel statusbar;
    Shape curPiece; // piece qui tombe                                 
    Tetrominoes[] board;
    char[] letters;
    int[] ids;
    
    int lastShapeId = -1;
    

    public Board(GameFrame parent) {
       setFocusable(true);
       curPiece = new Shape();
       timer = new Timer(400, this);
       timer.start(); 

       /*statusbar =  parent.getStatusBar();*/
       board = new Tetrominoes[BoardWidth * BoardHeight];
       letters = new char[BoardWidth * BoardHeight];
       ids = new int[BoardWidth * BoardHeight];
       addKeyListener(new TAdapter());
       addMouseListener(new MouseManager());
       clearBoard();  
    }
    
    // Verifie si la piece a fini de tomber
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }


    //int squareWidth() { return (int) getSize().getWidth() / BoardWidth; }
    //int squareHeight() { return (int) getSize().getHeight() / BoardHeight; }
    int squareWidth() { return (int) 279 / BoardWidth; }
    int squareHeight() { return (int) 570 / BoardHeight; }
    Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }
    
    char letterAt(int x, int y){ 
    	//System.out.println("Lettre à (" + x + ", " + y + ") = " + letters[(y * BoardWidth) + x]);
    	return letters[(y * BoardWidth) + x];
    }
    int idAt(int x, int y) {
    	//System.out.println("Id à (" + x + ", " + y + ") = " + ids[(y * BoardWidth) + x]);
    	return ids[(y * BoardWidth) + x];
    }
    
    String lettersAt(int y) {
    	StringBuilder sb = new StringBuilder();;
    	sb.append(letterAt(0, y));
		int previousShapeId = idAt(0, y);
		char previousShapeLetter = letterAt(0, y);
		Tetrominoes previousShape = shapeAt(0, y);
    	for(int i=1; i<BoardWidth; i++) {
    		if(previousShapeId != idAt(i,y) ||
    		   previousShapeLetter != letterAt(i,y) ||
    		   previousShape != shapeAt(i,y))
    		{
    			if(letterAt(i,y) == '\0' || shapeAt(i,y) == Tetrominoes.NoShape) {
    				sb.append("-");
    			} else {
    				sb.append(letterAt(i, y));
    			}
    		}
    		previousShapeId = idAt(i, y);
    		previousShapeLetter = letterAt(i, y);
    		previousShape = shapeAt(i, y);
    	}
    	System.out.println("Les lettres à la ligne " + y + " sont " + sb);
		return sb.toString();
    }

    public void start()
    {
        if (isPaused)
            return;

        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();

        newPiece();
        timer.start();
    }

    private void pause()
    {
        if (!isStarted)
            return;

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            //statusbar.setText("paused");
        } else {
            timer.start();
            //statusbar.setText(String.valueOf(numLinesRemoved));
        }
        repaint();
    }
    
    // Dessine tous les objets
    public void paint(Graphics g)
    { 
        
        // Ajout d'une image de fond
        ii = new ImageIcon(this.getClass().getResource("pictures/pirates.jpg"));
        picture = new JLabel(new ImageIcon(ii.getImage()));
        picture.setSize(525, 700);
        add(picture);
        
    	super.paint(g);

        //Dimension size = getSize();
        int boardTop = 58;
        int boardLeft = 120;

        // Dessine toutes les formes, ou ce qu'il en reste, deja tombees
        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                char letter = letterAt(j, BoardHeight - i - 1);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, boardLeft + j * squareWidth(),
                               boardTop + i * squareHeight(), shape, letter);
            }
        }

        // Dessine la piece en train de tomber
        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, boardLeft + x * squareWidth(),
                           boardTop + (BoardHeight - y - 1) * squareHeight(),
                           curPiece.getShape(), curPiece.getLetter());
            }
        }
    }

    private void dropDown()
    {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1))
                break;
            --newY;
        }
        pieceDropped();
    }

    private void oneLineDown()
    {
        if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped();
    }


    private void clearBoard()
    {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i) {
            board[i] = Tetrominoes.NoShape;
			/*letters[i] = '\0';*/
        }
    }

    private void pieceDropped()
    {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
            letters[(y * BoardWidth) + x] = curPiece.getLetter();
            ids[(y * BoardWidth) + x] = curPiece.getId();
        }
        
        // TODO 
        removeFullLines();

        if (!isFallingFinished)
            newPiece();
    }

    private void newPiece()
    {
        curPiece.setRandomShape();
        curPiece.setRandomLetter();
        this.lastShapeId += 1;
        curPiece.setId(lastShapeId);
        curX = BoardWidth / 2 + 1;
        curY = BoardHeight - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            //statusbar.setText("game over");
        }
        
    }

    private boolean tryMove(Shape newPiece, int newX, int newY)
    {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    private void removeFullLines()
    {
        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; --i) {
        	//
        	//System.out.println(lettersAt(19));
        	//
        	
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
            	lettersAt(0);
            	lettersAt(1);
            	lettersAt(2);
            	lettersAt(3);
                
                // Suppression des lignes pleines
                /*for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j) {
                    	board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                    	//letters[(k * BoardWidth) + j] = letterAt(j, k + 1);
                    	//ids[(k * BoardWidth) + j] = idAt(j, k + 1);
                    }
                }*/
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            //statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
     }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape, char letter)
    {
        Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102), 
            new Color(102, 204, 102), new Color(102, 102, 204), 
            new Color(204, 204, 102), new Color(204, 102, 204), 
            new Color(102, 204, 204), new Color(218, 170, 0)
        };


        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                         x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                         x + squareWidth() - 1, y + 1);
        
        // Ecriture de la lettre
        String s = Character.toString(letter);
	    g.setColor(color.darker());    
		g.drawString(s, x + 10, y + 18);

    }
    
    private void clickManager (int x, int y) {
        int boardTop = 58;
        int boardLeft = 120;
        
    	int newX = -(boardLeft - x) / squareWidth();
    	// TODO int newY = (boardTop - y) / squareHeight();
    	// Quand newY devrait être = à 0, il vaut 19
    	
    	//System.out.println("y = " + newY);
    }
    
    /*private String lettersClicked(int x, int y) {
		return null;
    }*/
    
    class MouseManager implements MouseListener
    {
		@Override
    	public void mouseClicked(MouseEvent e)
        {
			clickManager(e.getX(), e.getY());
			/*int xPx = e.getX();
			int yPx = e.getY();
			
			int x = (boardLeft-xPx) / squareWidth();
			System.out.println(y);*/
			
			/*if (shapeAt(x, y) != Tetrominoes.NoShape) {
				System.out.println("Clic sur une piece");
			}*/
        } 

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
    }

    class TAdapter extends KeyAdapter {
         public void keyPressed(KeyEvent e) {

             if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {  
                 return;
             }

             int keycode = e.getKeyCode();

             if (keycode == 'p' || keycode == 'P') {
                 pause();
                 return;
             }

             if (isPaused)
                 return;

             switch (keycode) {
             case KeyEvent.VK_LEFT:
                 tryMove(curPiece, curX - 1, curY);
                 break;
             case KeyEvent.VK_RIGHT:
                 tryMove(curPiece, curX + 1, curY);
                 break;
             case KeyEvent.VK_SPACE:
                 tryMove(curPiece.rotateRight(), curX, curY);
                 break;
             /*case KeyEvent.VK_UP:
                 tryMove(curPiece.rotateLeft(), curX, curY);
                 break;*/
             case KeyEvent.VK_UP:
                 dropDown();
                 break;
             case KeyEvent.VK_DOWN:
            	 oneLineDown();
            	 break;
             /*case 'd':
                 oneLineDown();
                 break;
             case 'D':
                 oneLineDown();
                 break;*/
             }

         }
     }
}