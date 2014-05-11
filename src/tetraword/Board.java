package tetraword;

import java.awt.Color;
import java.awt.Font;
//import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import design.MenuButton;
import design.PrintText;
import tetraword.Bonus.Bonuses;
import tetraword.Shape.Tetrominoes;

@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener {

	private ImageIcon ii, btSound, btMute, btBreak;
    private JLabel picture, bt_sound, bt_break, pause, gameover;
    private JLabel printWord = new JLabel(); 
    private JLabel labelBonus = new JLabel();
    private PrintText printScore, printLevel, printLine;
    
	final int BoardWidth = 10;
    final int BoardHeight = 20;
    final int BoardTop = 64;
    final int BoardLeft = 120;
    final int GameWidth = 279;
    final int GameHeight = 560;

    Timer timer;
    private boolean isSound = true; // le son est-il active ?
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
    int difficulty = 30; // difficulte des anagrammes
    
    int speed; // vitesse de chute des pieces
    int score;
    int level;
    
    //LinkedList<Bonus> bonuses;
	private Bonus curBonus; // Bonus affiche
	private Bonuses[] bonus;
	private boolean worddle;
	private int[] previousCoords;
	int[][][] curWorddle; //(0,0) = x, (1,0) = y, (2,0) = id, lettre, validation
	LinkedList<Integer> worddleIds;
	//private Timer timerBonus;

	private PlaySound sound;

	public Board(GameFrame parent) throws UnsupportedAudioFileException, IOException {
       setFocusable(true);
       // Chargement de l'interface graphique
       buildInterface();
       //Musique
       sound = new PlaySound(new File("src/audio/test.wav"));
       sound.open();
       sound.play();
       
       // Adapation de la difficulte en fonction de preferences de l'utilisateur
       setDifficulty();
       curPiece = new Shape();
       curBonus = new Bonus();
       curLine = -1;
       speed = 800;
       timer = new Timer(speed, this); // vitesse de descente des pieces
       timer.start();

       /*statusbar =  parent.getStatusBar();*/
       board = new Tetrominoes[BoardWidth * BoardHeight];
       bricks = new int[BoardWidth][BoardHeight][3]; // 0 = id; 1 = lettre; 2 = click
       bonus = new Bonuses[BoardWidth * BoardHeight];
       previousCoords = new int[2];
       score = 0;
       level = 1;
       worddle = true;
       curWorddle = new int[BoardWidth][BoardHeight][2]; // Pour l'indice 2 : 1 = id; 2 = letter; 3 = validation
       clearCurWorddle();
       //bonuses = new LinkedList<Bonus>();
       addKeyListener(new TAdapter());
       addMouseListener(new MouseManager());
       clearBoard();
       
       newBonus();
       //timerBonus = new Timer(2000, this);
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
    
    int squareWidth() { return (int) GameWidth / BoardWidth; }
    int squareHeight() { return (int) GameHeight / BoardHeight; }
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
    //Bonuses bonusAt(int x, int y) { return bonus[(y * BoardWidth) + x]; }

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
            breakMenu();
    		System.out.println("Pause");
            //statusbar.setText("paused");
        } else {
            timer.start();
            pause.setVisible(false);
            picture.setVisible(true);
            //statusbar.setText(String.valueOf(numLinesRemoved));
        }
        repaint();
    }
    
	//Menu pause
    public void breakMenu(){
    	sound.stop();
    	sound.close();
    	
    	ImageIcon bgBreak = new ImageIcon(this.getClass().getResource("/pictures/bg_accueil.jpg"));            	 	
	    pause = new JLabel(new ImageIcon(bgBreak.getImage()));
	    pause.setSize(525, 700);
	    add(pause);
	    pause.setVisible(true);  
	    picture.setVisible(false);
	    
        final Color blue =new Color(46,49,146);
        
        //Bouton Continuer
        final MenuButton bt_continue = new MenuButton("Reprendre le jeu", blue, Color.black, 200, false);
        pause.add(bt_continue);
        /*On affiche la page du jeu*/
        bt_continue.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_continue.SetForegroundandFill(Color.white, true);
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {
        		bt_continue.SetForegroundandFill(Color.black, false);
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
                pause(); 
        	}
        });
        
        MenuButton.buttonNewGame(pause, 280);
        MenuButton.buttonMenu(pause,360);        
        MenuButton.buttonClose(pause, 440);
    }
    
    //Menu game Over
    public void gameOver(){
    	clearBoard();
	    picture.setVisible(false);

    	ImageIcon bgBreak = new ImageIcon(this.getClass().getResource("/pictures/gameover.jpg"));         
   	 	
    	gameover = new JLabel(new ImageIcon(bgBreak.getImage()));
    	gameover.setSize(525, 700);
	    add(gameover);
	    gameover.setVisible(true);
        
        MenuButton.buttonNewGame(gameover, 280);
        MenuButton.buttonMenu(gameover,360);        
        MenuButton.buttonClose(gameover, 440);
    	 	
    }
    
    public void buildInterface(){
    	Properties options = new Properties();     	
    	// Creation d'une instance de File pour le fichier de config
    	File fichierConfig = new File("conf/conf.properties"); 
    	
    	// Chargement du fichier de configuration
    	try {
    		options.load(new FileInputStream(fichierConfig));
    	} 
    	catch(IOException e) {
    		System.out.println("Echec chargement");
    	}

   	 	String configUniv = options.getProperty("Univers"); 
    	switch (configUniv)
 	   	{
 	   	  case "western":
 	        // Ajout d'une image de fond
 	        ii = new ImageIcon(this.getClass().getResource("/pictures/cowboys.jpg"));
 	        btSound = new ImageIcon(this.getClass().getResource("/pictures/sound_ninja.png"));
 	        btMute = new ImageIcon(this.getClass().getResource("/pictures/mute_ninja.png"));
 	        btBreak = new ImageIcon(this.getClass().getResource("/pictures/break_ninja.png"));
 	   	    break;
 	   	  case "kungfu":
 	        // Ajout d'une image de fond
 	        ii = new ImageIcon(this.getClass().getResource("/pictures/ninjas.jpg"));
 	        btSound = new ImageIcon(this.getClass().getResource("/pictures/sound_ninja.png"));
 	        btMute = new ImageIcon(this.getClass().getResource("/pictures/mute_ninja.png"));
 	        btBreak = new ImageIcon(this.getClass().getResource("/pictures/break_ninja.png"));
 	   	    break; 
 	   	  case "pirate":
 	         // Ajout d'une image de fond
 	         ii = new ImageIcon(this.getClass().getResource("/pictures/pirates.jpg"));
	 	     btSound = new ImageIcon(this.getClass().getResource("/pictures/sound_ninja.png"));
 	         btMute = new ImageIcon(this.getClass().getResource("/pictures/mute_ninja.png"));
 	         btBreak = new ImageIcon(this.getClass().getResource("/pictures/break_ninja.png"));
 	   	    break; 
 	   	  case "batman":
 	         // Ajout d'une image de fond
 	         ii = new ImageIcon(this.getClass().getResource("/pictures/batman.jpg"));
	 	     btSound = new ImageIcon(this.getClass().getResource("/pictures/sound_batman.png"));
	 	     btMute = new ImageIcon(this.getClass().getResource("/pictures/mute_batman.png"));
	 	     btBreak = new ImageIcon(this.getClass().getResource("/pictures/break_batman.png"));  
 	   	    break; 
 	   	  default:
 	         // Ajout d'une image de fond
 	         ii = new ImageIcon(this.getClass().getResource("/pictures/pirates.jpg"));         
 	   	}
   	 	
        picture = new JLabel(new ImageIcon(ii.getImage()));
        picture.setSize(525, 700);
        add(picture);
        
    	// Bouton pause
        bt_break = new JLabel(new ImageIcon(btBreak.getImage()));       
        bt_break.setBounds(430, 10, 30, 30); 
        picture.add(bt_break);
        
        bt_break.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        	   pause();
        	}
        });
    
        // Gestion du son
        bt_sound = new JLabel(new ImageIcon(btSound.getImage()));       
        bt_sound.setBounds(470, 10, 30, 30); 
        picture.add(bt_sound);
        
        bt_sound.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		if(isSound){
	   	             sound.stop();
	   	             sound.close();
	        		bt_sound.setIcon(btMute);
	        		System.out.println("Et je coupe le son");
	        		isSound = false;
        		}
        		else{
        			bt_sound.setIcon(btSound);
	        		System.out.println("Et je remets le son");
	        		isSound = true;
        		}
        	}
        });
        
        
        Font font = new Font("Arial",Font.BOLD,22);
        Font font_level = new Font("Arial",Font.BOLD,36);
        
        //On affiche le score
        printScore = new PrintText(Integer.toString(0), 40, 55, font, Color.white, picture);
        //On affiche le level
        printLevel = new PrintText(Integer.toString(1), 310, 582, font_level, Color.white, picture);        
        //On affiche le nombre de ligne
        printLine = new PrintText(Integer.toString(0), 40, 140, font, Color.white, picture);    
    }
    
    // Dessine tous les objets
    @Override
    public void paint(Graphics g)
    {   	
    	super.paint(g);    	
    	
    	if(!isPaused) {
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
            
            // Dessine le bonus
            if(curBonus.getBonus() != Bonuses.NoBonus) {
            	int x = curBonus.x();
                int y = curBonus.y();
                drawBonus(g, BoardLeft + x * squareWidth(),
                          BoardTop + (BoardHeight - y - 1) * squareHeight(),
                          curBonus.getBonus());
            }
    	}
    	
    	
    	/*Gestion Musique*/
        /*if(ThreadPlaySound.endMusic && !isPaused && isSound){
      	  try {
			sound = new PlaySound(new File("/audio/test.wav"));
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
      	  sound.open();
      	  sound.play();
      	  ThreadPlaySound.endMusic = false;
        }*/
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
            bonus[i] = Bonuses.NoBonus;
        }
        curBonus.setBonus(Bonuses.NoBonus);
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
            bonus[(y * BoardWidth) + x] = curBonus.getBonus();
        }
        
        if (!isFallingFinished) {
        	newPiece();
        	score += 5;
            level = score/100 + 1;
            printScore.setText(Integer.toString(score)); 	
			printLevel.setText(Integer.toString(level));
        }
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
            gameOver();
            //statusbar.setText("game over");
        }
    }
    
    private void newBonus()
    {
    	curBonus.setRandomBonus();
    	curBonus.setRandomX();
    	curBonus.setRandomY();
    	while(shapeAt(curBonus.x(), curBonus.y()) != Tetrominoes.NoShape) {
    		curBonus.setRandomX();
        	curBonus.setRandomY();
    	}
    	//System.out.println("Bonus (" + curBonus.x() + "," + curBonus.y() + ")");
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
        
        if (curBonus.getBonus() != Bonuses.NoBonus) {
        	// Verifie la collision entre la piece qui tombe et le bonus
        	for(int i = 0; i < 4; i++) {
    	    	//System.out.println("Piece : (" + (curX + curPiece.x(i)) + ";" + (curY + curPiece.y(i)) + ")");
    	        //System.out.println("Bonus : (" + curBonus.x() + ";" + curBonus.y() + ")");
    	        if((curX + curPiece.x(i)) == curBonus.x() && (curY + curPiece.y(i)) == curBonus.y()) {
    	        	System.out.println(curBonus.getBonus());
    	        	picture.remove(labelBonus);
    	        	applyBonus(curBonus.getBonus());
    	        	//bonuses.add(curBonus);
    	        	curBonus = new Bonus();
    	        }
            }
        	
        	// Verifie la collision entre les pieces en bas et le bonus
        	for (int i = 0; i < BoardHeight; ++i) {
                for (int j = 0; j < BoardWidth; ++j) {
                	Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                    if (shape != Tetrominoes.NoShape && curBonus.x() == j && curBonus.y() == i) {
                    	// TODO bonuses.add(curBonus);
                    	curBonus = new Bonus();
                    }
                }
            }
        } else {
        	Random r = new Random();
        	int x = Math.abs(r.nextInt()) % 10; // TODO
        	if(x == 1) {
        		newBonus();
        	}
        }
    	        
        repaint();
       
        return true;
    }
    
    private void setDifficulty(){
       	Properties options = new Properties();     	
    	// Creation d'une instance de File pour le fichier de config
    	File fichierConfig = new File("conf/conf.properties"); 
    	
    	// Chargement du fichier de configuration
    	try {
    		options.load(new FileInputStream(fichierConfig));
    	} 
    	catch(IOException e) {
    		System.out.println("Echec chargement");
    	}
    	
    	// Recuperer une propriete d'un fichier de configurations
   	 	String configLevel = options.getProperty("Level"); 
   	 	
    	switch (configLevel)
 	   	{
 	   		case "debutant":
 	   			difficulty = 30;
 	   			break;
 	   		case "amateur":
 	   			difficulty = 50;
 	   			break; 
 	   		case "expert":
 	   			difficulty = 70;
 	   			break; 
 	   		default:
 	   			difficulty = 30;       
 	   	}
    }
    
    private boolean validateWordAnagram(String word) {
	    Dictionary dictionary = new Dictionary();
	    boolean validWord = false;  

	    //On adapte inputLetters aux methodes de dictionary
	    word = word.toLowerCase();

		//On recupere les lettres de la ligne
		String inlineLetters = (lettersAt(curLine)).toLowerCase();
		char tabInlineLetters[] = inlineLetters.toCharArray();

		 // On envoie toutes les lettres de la ligne pour trouver le meilleur anagramme
		 bestAnagram = dictionary.bestAnagram(tabInlineLetters,inlineLetters.length());
    
		 // Verifie si le mot n'est pas vide
		 if(word != null && !word.isEmpty()) {
			 // Verifie si le mot respecte la difficulte
			 if(word.length() >= (int)difficulty * bestAnagram.length()/100) {
				 //Verifier si le mot est dans le dictionnaire
				 System.out.println("Meilleur anagramme : " + bestAnagram);
				 validWord = dictionary.validateWord(word);
				 if(validWord) System.out.println("Le mot est correct");
				 if(!validWord) System.out.println("Le mot est incorrect");
			 }
		 }
		 return validWord;
    }
    
    private boolean validateWordWorddle(String word) {
    	Dictionary dictionary = new Dictionary();
	    boolean validWord = false;  

	    //On adapte inputLetters aux methodes de dictionary
	    word = word.toLowerCase();
    
	    // Verifie si le mot n'est pas vide
	    if(word != null && !word.isEmpty()) {
	    	//if(word != "" || word != null) {
	    	System.out.println("Le mot n'est pas null");
	    	System.out.println(word);
	    	// Verifier si le mot est dans le dictionnaire
	    	validWord = dictionary.validateWord(word);
	    	if(validWord) {
	    		for(int i = 0; i < BoardWidth; ++i) {
	    			for(int j = 0; j < BoardHeight; ++j) {
	    				if(curWorddle[i][j][1] == 1) {
	    					bricks[i][j][2] = 1;
	    				}
	    			}
	    		}
	    		System.out.println("Le mot est correct");
	    	} else {
	    		System.out.println("Le mot est incorrect");
	    	}
		}
		 
		clearCurWorddle();
		inputLetters = "";
		 
		return validWord;
    }
    
    private boolean isLineFull(int y) {
    	boolean lineIsFull = true;
    	
        // On parcourt toutes les colonnes du jeu
        for (int j = 0; j < BoardWidth; ++j) {
        	// Si on trouve des cases vides, la ligne n'est pas pleine
            if (shapeAt(j, y) == Tetrominoes.NoShape) {
                lineIsFull = false;
                break;
            }
        }
        
        return lineIsFull;
    }
    
    private void removeLine(int y) {
    	if(validateWordAnagram(inputLetters) && isLineFull(y)) {
    		++numLinesRemoved;	
    		printLine.setText(Integer.toString(numLinesRemoved)); 
        	// Parcourir toutes les pieces
            for (int k = y; k < BoardHeight - 1; ++k) {
            	for (int j = 0; j < BoardWidth; ++j) {
            		// Suppression de la ligne
            		board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
        			bricks[j][k][0] = idAt(j, k + 1);
        			bricks[j][k][1] = letterAt(j, k + 1);
        			bricks[j][k][2] = clickAt(j, k + 1);
        			//bonus[(k * BoardWidth) + j] = bonusAt(j, k + 1);
            	}
            }
    	}
    	else if(!validateWordAnagram(inputLetters) && isLineFull(y)) {
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
    
    private void removeBricksWorddle() {
    	// Parcourir toutes les pieces
        for (int i = 0; i < BoardWidth; ++i) {
            for (int j = 0; j < BoardHeight - 1; ++j) {
            	if(bricks[i][j][2] == 1) {
            		fallColumn(i, j);
            	}
            }
        }
    	
    	clearCurWorddle();
        inputLetters = "";
        curLine = -1;
        worddle = false;
    }
    
    private void fallColumn(int x, int y) {
    	// Parcourir toutes les pieces
        for (int i = y; i < BoardHeight - 1; ++i) {
        		// Suppression de la ligne
        		board[(i * BoardWidth) + x] = shapeAt(x, i + 1);
    			bricks[x][i][0] = idAt(x, i + 1);
    			bricks[x][i][1] = letterAt(x, i + 1);
    			bricks[x][i][2] = clickAt(x, i + 1);
        }
    }
    
    private void drawBonus(Graphics g, int x, int y, Bonuses bonus) {
		Image image = (curBonus.getImage()).getImage();
		if(image != null) // Si l'image existe, ...
			g.drawImage(image, x, y, this); // ... on la dessine
		else 
			System.err.println("Impossible de charger l'image du bonus");
    }
    
    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape, String letter)
    {
        //System.out.println("draw square");
    	
    	Color colors[] = { new Color(0, 0, 0), new Color(193, 194, 193), 
            new Color(196, 0, 38), new Color(221, 98, 13), 
            new Color(255, 220, 0), new Color(51, 166, 167), 
            new Color(0, 103, 167), new Color(90, 27, 105)
        };


        
        //Font f = new Font("visitor", Font.PLAIN, 10);  // make a new font object
        //Font font = new Font("Sans-Serif", Font.PLAIN, 14);

        //Color black = new Color(0, 0, 0);
        
    	Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
        g.fillRect(x + 1, y + 1, squareWidth() - 1, squareHeight() - 1);
        g.fillRect(x + 8, y - 5, (squareWidth() / 2) - 1, squareHeight() / 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                         x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                         x + squareWidth() - 1, y + 1);
        
        // Ecriture de la lettre
	    g.setColor(color.darker());
	    //g.setFont(font);
	    //g.setFont(f); // set the objects font using setFont();
		g.drawString(letter, x + 11, y + 19);

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
    			//System.out.println("Les lettres saisies jusqu'a l'instant sont " + inputLetters);
    			printWord.setText(inputLetters);
    	    	printWord.setBounds(300,-25, 300, 100);     	
    	    	picture.add(printWord);
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
    
    private void scoreAnagram(String word) {
        printScore.setText(Integer.toString(score)); 	
		printLevel.setText(Integer.toString(level));
		
		// Augmentation du score
		System.out.println("Nombre de lettres saisies : " + inputLetters.length());
		System.out.println("Longueur du meilleur anagramme : " + bestAnagram.length());
		System.out.println("Score anagramme : " + (int)((double)inputLetters.length() / (double)bestAnagram.length() * 10));
		score += (int)((double)inputLetters.length() / (double)bestAnagram.length() * 20);
        level = score/100 + 1;
    }
    
    private void scoreWorddle(String word) {
        printScore.setText(Integer.toString(score)); 	
		printLevel.setText(Integer.toString(level));
		
		// Augmentation du score
		System.out.println("Nombre de lettres saisies : " + inputLetters.length());
		System.out.println("Longueur du meilleur anagramme : " + bestAnagram.length());
		System.out.println("Score anagramme : " + (int)((double)inputLetters.length() / (double)bestAnagram.length() * 10));
		score += (int)((double)inputLetters.length() / (double)bestAnagram.length() * 20);
        level = score/100 + 1;
    }
    
    private void applyBonus(Bonuses bonusType) {
    	switch (bonusType) {
		case NoBonus:
			break;
		case Worddle:
			inputLetters = "";
			worddle = true;
			break;
		case Speed:
			speedBonus();
			break;
		case BonusScore:
			scoreBonus();
			break;
		case MalusScore:
			scoreBonus();
			break;
    	}
    }

	private void worddleBonus(int x, int y) {
		// Conversion des pixels (recuperes au clic) en nombre de cases
    	int newX = (x - BoardLeft) / squareWidth();
    	int newY = (BoardHeight - 1) - ((y - BoardTop) / squareHeight());
    	
    	// Verifie si l'utilisateur a deja saisi une lettre
    	if(inputLetters.length() > 0) {    		
    		// Verifie si l'utilisateur clique sur une brique adjacente a la precedente
    		if(((previousCoords[0] + 1) == newX || previousCoords[0] == newX || (previousCoords[0] - 1) == newX) && 
		       ((previousCoords[1] + 1) == newY || previousCoords[1] == newY || (previousCoords[1] - 1) == newY)) {
				System.out.println("Lettre adjacente");
				
				//int curId = idAt(newX, newY);

				// Verifie si la brique n'est pas deja utilisee dans le mot
				if(curWorddle[newX][newY][1] == 0) {
					inputLetters += Character.toLowerCase(letterAt(newX,newY));
					curWorddle[newX][newY][1] = 1;
					//bricks[newX][newY][2] = 1;
					previousCoords[0] = newX;
					previousCoords[1] = newY;
					System.out.println("Les lettres saisies jusqu'a l'instant sont " + inputLetters);
				} else {
					System.err.println("La lettre a déjà été utilisée pour ce mot");
				}
			} else {
				// Nouveau mot
				System.out.println("Nouveau mot");
				inputLetters = "";
				clearCurWorddle();
				inputLetters += Character.toLowerCase(letterAt(newX,newY));
				curWorddle[newX][newY][1] = 1;
				//bricks[newX][newY][2] = 1;
				previousCoords[0] = newX;
				previousCoords[1] = newY;
				System.out.println("Les lettres saisies jusqu'a l'instant sont " + inputLetters);
			}
    	} else {
    		inputLetters += Character.toLowerCase(letterAt(newX,newY));
			bricks[newX][newY][2] = 1;
			previousCoords[0] = newX;
			previousCoords[1] = newY;
			System.out.println("Les lettres saisies jusqu'a l'instant sont " + inputLetters);
    	}
    	
    	
    	
    	
    	// Verifie si la ligne est pleine
    	/*if(isLineFull(newY)) {
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
    			//System.out.println("Les lettres saisies jusqu'a l'instant sont " + inputLetters);
    			printWord.setText(inputLetters);
    	    	printWord.setBounds(300,-25, 300, 100);     	
    	    	picture.add(printWord);
    		} else {
    			System.err.println("La lettre a deja ete utilisee");
    		}
    	}
    	else {
    		// TODO Ecrire un message pour dire que la ligne n'est pas pleine
    		bestAnagram = "";
    		System.err.println("La ligne n'est pas pleine");
    	}*/
	}
	
	private void clearCurWorddle() {
		for (int i = 0; i < BoardWidth; ++i) {
            for (int j = 0; j < BoardHeight; ++j) {
            	curWorddle[i][j][1] = 0;
            }
		}
	}

	private void scoreBonus() {
    	Random r = new Random();
        int x = (Math.abs(r.nextInt()) % 5 + 1) * 10;
        if(curBonus.getBonus() == Bonuses.BonusScore) {
            System.out.println("+" + x + " points");
    		score += x;
        }
        else {
        	System.out.println("-" + x + " points");
    		score -= x;
        }
        printScore.setText(Integer.toString(score)); 	
		printLevel.setText(Integer.toString(level));
	}

	private void speedBonus() {
    	
    }
    
    class MouseManager implements MouseListener
    {
		@Override
    	public void mouseClicked(MouseEvent e)
        {
	    	// Verifie si on clique dans l'espace du jeu
	    	if(e.getX() >= BoardLeft && e.getX() <= (BoardLeft+GameWidth) && e.getY() >= BoardTop && e.getY() <= (BoardTop+GameHeight)) {
	    		//System.out.println("Clic dans le bidule");
	    		if(!worddle)
	    			anagram(e.getX(), e.getY());
	    		else
	    			worddleBonus(e.getX(), e.getY());
	    	}
	    	/*else {
	    		//System.out.println("Clic ailleurs");
	    	}*/
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
    		
    		if (keycode == 'd' || keycode == 'D') {
    			System.out.println("Suppression des briques Worddle dans if");
    			scoreWorddle(inputLetters);
				removeBricksWorddle();
				worddle = false;
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
	    			if(worddle) {
	    				System.out.println("Entrée en mode Worddle");
	    				validateWordWorddle(inputLetters);
	    			} else if(curLine != -1) {
	    				System.out.println("Entrée en mode Anagramme");
	    				scoreAnagram(inputLetters);
	    				removeLine(curLine);
	    			}
	    			break;
	    		/*case 'd':
	    			System.out.println("Suppression des briques Worddle");
					removeBricksWorddle();
					worddle = false;
					break;
	    		case 'd':
					oneLineDown();
					break;
				case 'D':
					oneLineDown();
					break;*/
             }

         }
     }
}
