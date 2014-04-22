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
    final int BoardTop = 58;
    final int BoardLeft = 120;

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
    int[][][] bricks; // tableau qui contient l'id, la lettre et le clic pour chaque brique selon ses coordonnees dans le board
    int lastShapeId = -4; // dernier id attribue a une brique

    int curLine; // ligne sur laquelle l'utilisateur clique
    String inputLetters = ""; // lettres saisies au clic par l'utilisateur
    String bestAnagram = ""; // longueur du meilleur anagramme possible sur la ligne actuelle
    double difficulty = 0.75; // difficulte des anagrammes
    
    int score;
    int level;

    public Board(GameFrame parent) {
       setFocusable(true);
       curPiece = new Shape();
       curLine = -1;
       timer = new Timer(800, this); // vitesse de descente des pieces
       timer.start();

       /*statusbar =  parent.getStatusBar();*/
       board = new Tetrominoes[BoardWidth * BoardHeight];
       bricks = new int[BoardWidth][BoardHeight][3]; // 0 = id; 1 = lettre; 2 = click
       score = 0;
       level = 1;
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
    
    int squareWidth() { return (int) 279 / BoardWidth; }
    int squareHeight() { return (int) 570 / BoardHeight; }
    Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }
    int idAt(int x, int y) { return bricks[x][y][0]; }
    char letterAt(int x, int y){ return (char)bricks[x][y][1]; }
    int clickAt(int x, int y) { return bricks[x][y][2]; }
    String lettersAt(int y) {
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < BoardWidth; i++) {
    		sb.append(letterAt(i,y));
    	}
    	//System.out.println("Les lettres a la ligne " + y + " sont " + sb);
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
    	
    	// Creation d'une instance de File pour le fichier de config
    	File fichierConfig = new File("conf/conf.properties"); 

    	//Chargement du fichier de configuration
    	try {
    	options.load(new FileInputStream(fichierConfig));
    	} 
    	catch(IOException e) {
    		System.out.println("Echec chargement");
    	}
    	
    	// Recuperer une propriete d'un fichier de configurations
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
        
    	super.paint(g);

        //Dimension size = getSize();

        // Dessine toutes les formes, ou ce qu'il en reste, deja tombees
        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                String letter = Character.toString(letterAt(j, BoardHeight - i - 1));
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, BoardLeft + j * squareWidth(),
                               BoardTop + i * squareHeight(),
                               shape, letter);
            }
        }

        // Dessine la piece en train de tomber
        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, BoardLeft + x * squareWidth(),
                           BoardTop + (BoardHeight - y - 1) * squareHeight(),
                           curPiece.getShape(), Character.toString(curPiece.getLetter(i)));
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
        score += 5;
        level = score/100 + 1;
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
        }
    }

    private void pieceDropped()
    {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
            bricks[x][y][0] = curPiece.getId(i);
            bricks[x][y][1] = curPiece.getLetter(i);
            bricks[x][y][2] = curPiece.getClick(i);
        }
        
        /* TODO Verifier si des lignes sont pleines dans le jeu :
		    for (int i = BoardHeight - 1; i >= 0; --i) {
		    	if(isLineFull(i)) {
		    		// Prevenir l'utilisateur qu'il peut faire des mots
		    	}
		    }
		*/

        if (!isFallingFinished)
            newPiece();
        	score += 5;
            level = score/100 + 1;
        	System.out.println("Score : " + score);
        	System.out.println("Niveau : " + level);
    }

    private void newPiece()
    {
		curPiece.setRandomShape();
        curPiece.setRandomLetters();
        lastShapeId += 4;
        curPiece.setIds(lastShapeId);
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
	    Dictionary dictionary = new Dictionary();
	    boolean validWord = false;  

	    //On adapte inputLetters aux methodes de dictionary
	    inputLetters = inputLetters.toLowerCase();

		//On recupere les lettres de la ligne
		String inlineLetters = (lettersAt(curLine)).toLowerCase();
		char tabInlineLetters[] = inlineLetters.toCharArray();

		 // On envoie toutes les lettres de la ligne pour trouver le meilleur anagramme
		 bestAnagram = dictionary.bestAnagram(tabInlineLetters,inlineLetters.length());
    
		 // Verifie si le mot n'est pas vide
		 if(inputLetters != "" || inputLetters != null || inputLetters.length() != 0) {
			 // Verifie si le mot respecte la difficulte
			 if(inputLetters.length() >= (int)difficulty * bestAnagram.length()) {
				 //Verifier si le mot est dans le dictionnaire
				 System.out.println("Meilleur anagramme : " + bestAnagram);
				 validWord = dictionary.validateWord(inputLetters);
				 if(validWord) System.out.println("Le mot est correct");
				 if(!validWord) System.out.println("Le mot est incorrect");
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
    	if(isWordCorrect() && isLineFull(y)) {
    		++numLinesRemoved;
    		
    		// Augmentation du score
    		System.out.println("Nombre de lettres saisies : " + inputLetters.length());
    		System.out.println("Longueur du meilleur anagramme : " + bestAnagram.length());
    		System.out.println("Score anagramme : " + (int)((double)inputLetters.length() / (double)bestAnagram.length() * 10));
    		score += (int)((double)inputLetters.length() / (double)bestAnagram.length() * 20);
            level = score/100 + 1;
        	// Parcourir toutes les pieces
            for (int k = y; k < BoardHeight - 1; ++k) {
            	for (int j = 0; j < BoardWidth; ++j) {
            		// Suppression de la ligne
            		board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
        			bricks[j][k][0] = idAt(j, k + 1);
        			bricks[j][k][1] = letterAt(j, k + 1);
        			bricks[j][k][2] = clickAt(j, k + 1);
            	}
            }
    	}
    	else if(isLineFull(y)) {
            for (int k = y; k < BoardHeight - 1; ++k) {
            	for (int j = 0; j < BoardWidth; ++j) {
            		// Les lettres ne sont plus considerees comme deja cliquees
            		bricks[j][k][2] = 0;
            	}
            }
        }
        
        inputLetters = "";
        curLine = -1;
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

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape, String letter)
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
        /*String s = Character.toString(letter);*/
	    g.setColor(color.darker());    
		g.drawString(letter, x + 10, y + 18);

    }
    
    private void anagram (int x, int y) {        
        // Conversion des pixels (recuperes au clic) en nombre de cases
        // x appartient a [0,9]
        // y appartient a [19,0]
    	//System.out.println("(" + x + "," + y + ")");
    	int newX = (x - BoardLeft) / squareWidth();
    	int newY = (BoardHeight - 1) - ((y - BoardTop) / squareHeight());
    	
    	// Verifie si la ligne est pleine
    	if(isLineFull(newY)) {
    		// Verifie si l'utilisateur fait un anagramme sur une nouvelle ligne
    		if(curLine != newY) {
    			System.out.println("Saisie sur une nouvelle ligne");
    			inputLetters = "";
    		}
    		curLine = newY;

    		// On verifie que l'utilisateur clique sur la piece pour la premiere fois
    		if(bricks[newX][newY][2] == 0) {
    			inputLetters += Character.toLowerCase(letterAt(newX,newY));
    			bricks[newX][newY][2] = 1;
    			System.out.println("Les lettres saisies jusqu'a l'instant sont " + inputLetters);
    		} else {
    			System.err.println("La lettre a deja ete utilisee");
    		}
    	}
    	else {
    		// TODO Ecrire un message pour dire que la ligne n'est pas pleine
    		bestAnagram = "";
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
	    	// Verifie si on clique dans l'espace du jeu
	    	//if(e.getX() >= BoardLeft && e.getX() <= BoardLeft+270 && e.getY() >= BoardTop && e.getY() >= BoardTop+558) {
	    		anagram(e.getX(), e.getY());
	    	//}
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
	    			if(curLine != -1)
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