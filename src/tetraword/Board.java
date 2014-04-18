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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

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
    String inputLetters = "";
    int bestAnagram = 0;
    double difficulty = 0.75;
    int curLine;
    ArrayList<Integer> inputIds = new ArrayList<Integer>();
    

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
    char letterAt(int x, int y){ return letters[(y * BoardWidth) + x]; }
    int idAt(int x, int y) { return ids[(y * BoardWidth) + x]; }
    
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
    	//System.out.println("Les lettres à la ligne " + y + " sont " + sb);
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
    	Properties options = new Properties(); 
    	
    	// Création d'une instance de File pour le fichier de config
    	File fichierConfig = new File("conf/conf.properties"); 

    	//Chargement du fichier de configuration
    	try {
    	options.load(new FileInputStream(fichierConfig));
    	} 
    	catch(IOException e) {
    		System.out.println("Echec chargement");
    	}
    	
    	/* Récupérer une propriété d'un fichier de configurations           */
   	 	String configUniv = options.getProperty("Univers"); 
   	 	
    	switch (configUniv)
 	   	{
 	   	  case "western":
 	        // Ajout d'une image de fond
 	        ii = new ImageIcon(this.getClass().getResource("pictures/cowboys.jpg"));
 	   	    break;
 	   	  case "kungfu":
 	        // Ajout d'une image de fond
 	        ii = new ImageIcon(this.getClass().getResource("pictures/ninjas.jpg"));
 	   	    break; 
 	   	  case "pirate":
 	         // Ajout d'une image de fond
 	         ii = new ImageIcon(this.getClass().getResource("pictures/pirates.jpg"));
 	   	    break; 
 	   	  case "batman":
 	         // Ajout d'une image de fond
 	         ii = new ImageIcon(this.getClass().getResource("pictures/batman.jpg"));
 	   	    break; 
 	   	  default:
 	         // Ajout d'une image de fond
 	         ii = new ImageIcon(this.getClass().getResource("pictures/pirates.jpg"));         
 	   	}
   	 	

        picture = new JLabel(new ImageIcon(ii.getImage()));
        picture.setSize(525, 700);
        add(picture);
        
        // Ajout d'une image de fond
        /*ii = new ImageIcon(this.getClass().getResource("pictures/pirates.jpg"));
        picture = new JLabel(new ImageIcon(ii.getImage()));
        picture.setSize(525, 700);
        add(picture);*/
        
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
                               boardTop + i * squareHeight(),
                               shape, letter);
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
        
        /* TODO Verifier si des lignes sont pleines dans le jeu :
		    for (int i = BoardHeight - 1; i >= 0; --i) {
		    	if(isLineFull(i)) {
		    		// Faire quelque chose
		    	}
		    }
		*/

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
    
    private boolean isWordCorrect() {
    	boolean validWord = false;
    	
		// TODO On envoie toutes les lettres de la ligne pour trouver le meilleur anagramme
		// bestAnagram =
    	
    	// Verifie si le mot n'est pas vide
    	if(inputLetters != "" || inputLetters != null) {
    		// Verifie si le mot respecte la difficulte
    		if(inputLetters.length() >= (int)difficulty * bestAnagram) {
    			// TODO Verifier si le mot est dans le dictionnaire
    			validWord = true;
    		}
    	}
    	return validWord;
    }
    
    private boolean isLineFull(int y) {
    	boolean lineIsFull = true;
    	
    	// On parcourt toutes les lignes du jeu
    	//for (int i = BoardHeight - 1; i >= 0; --i) { 
            // On parcourt toutes les colonnes du jeu
            for (int j = 0; j < BoardWidth; ++j) {
            	// Si on trouve des cases vides, la ligne n'est pas pleine
                if (shapeAt(j, y) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }
        //}
    	return lineIsFull;
    }
    
    private void removeLine(int y) {
		int numFullLines = 0;
		
    	if(isWordCorrect() && isLineFull(y)) {
            
        	// Parcourir toutes les pieces
            for (int k = y; k < BoardHeight - 1; ++k) {
            	for (int j = 0; j < BoardWidth; ++j) {
                    ++numFullLines;
            		// Suppression de la ligne
            		board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                	letters[(k * BoardWidth) + j] = letterAt(j, k + 1);
                	ids[(k * BoardWidth) + j] = idAt(j, k + 1);
                	
                	// TODO Augmentation du score
            	}
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

    /*private void removeFullLines()
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
                
                // Suppression des lignes pleines
                for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j) {
                    	board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                    	//letters[(k * BoardWidth) + j] = letterAt(j, k + 1);
                    	//ids[(k * BoardWidth) + j] = idAt(j, k + 1);
                    }
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            //statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
	}*/

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
    
    private void anagram (int x, int y) {
        int boardTop = 58;
        int boardLeft = 120;
        
        // Conversion des pixels (recuperes au clic) en nombre de cases
        // x appartient a [0,9]
        // y appartient a [19,0]
    	int newX = (x - boardLeft) / squareWidth();
    	int newY = (BoardHeight - 1) - ((y - boardTop) / squareHeight());
    	
    	// Verifie si la ligne est pleine
    	if(isLineFull(newY)) {
    		// Verifie si l'utilisateur fait un anagramme sur une nouvelle ligne
    		if(curLine != newY) {
    			System.out.println("Saisie sur une nouvelle ligne");
    			inputLetters = "";
    			inputIds.clear();
    		}
    		curLine = newY;
    		// On vérifie que l'utilisateur clique sur la piece pour la premiere fois
    		int curId = idAt(newX,newY);
    		boolean firstTime = true;
    		for(int i = 0; i < inputIds.size(); i++) {
    			if(inputIds.get(i) == curId) {
    				firstTime = false;
    				System.err.println("La lettre a deja ete utilisee");
    				break;
    			}
    	    }
    		if(firstTime) {
        		inputLetters += letterAt(newX,newY);
        		inputIds.add(idAt(newX,newY));
        		//System.out.println("La lettre a (" + newX + "," + newY + ") est " + letterAt(newX, newY));
        		System.out.println("Les lettres saisies jusqu'a l'instant sont " + inputLetters);
    		}
    	}
    	else {
    		// TODO Ecrire un message pour dire que la ligne n'est pas pleine
    		bestAnagram = -1;
    		System.err.println("La ligne n'est pas pleine");
    	}
    }
    
    /*private void worddle(int x, int y) {
    	
    }*/
    
    class MouseManager implements MouseListener
    {
		@Override
    	public void mouseClicked(MouseEvent e)
        {
			anagram(e.getX(), e.getY());
        } 
		@Override
		public void mouseEntered(MouseEvent arg0) { /* Auto-generated method stub */ }
		@Override
		public void mouseExited(MouseEvent arg0) { /* Auto-generated method stub */ }
		@Override
		public void mousePressed(MouseEvent arg0) { /* Auto-generated method stub */ }
		@Override
		public void mouseReleased(MouseEvent arg0) { /* Auto-generated method stub */ }
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
	    			dropDown();
	    			break;
	    		/*case KeyEvent.VK_UP:
					tryMove(curPiece.rotateLeft(), curX, curY);
					break;*/
	    		case KeyEvent.VK_UP:
	    			tryMove(curPiece.rotateRight(), curX, curY);
	    			break;
	    		case KeyEvent.VK_DOWN:
	    			oneLineDown();
	    			break;
	    		case KeyEvent.VK_ENTER:
	    			removeLine(curLine);
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