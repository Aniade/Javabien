package tetraword;

import java.awt.Color;
import java.awt.Font;
//import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import design.MenuButton;
import tetraword.Bonus.Bonuses;
import tetraword.Shape.Tetrominoes;

@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener {

    private ImageIcon ii;
    private ImageIcon btSound;
    private ImageIcon btMute;
    private ImageIcon btBreak;
    private JLabel picture;
    private JLabel bt_sound;
    private JLabel bt_break;
    private JLabel pause;
    private JLabel gameover;
    private JLabel printWord = new JLabel(); 
    private JLabel printScore;
    private JLabel printLevel; 
    private JLabel printLine; 
    private JLabel labelBonus = new JLabel(); 
    private ImageIcon bonusImg;
    private boolean getBonus = false;
    
	final int BoardWidth = 10;
    final int BoardHeight = 20;
    final int BoardTop = 59;
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
    
    int score;
    int level;
    
    LinkedList<Bonus> bonuses;
	private Bonus curBonus;
	private Bonuses[] bonus;

    public Board(GameFrame parent) {
       setFocusable(true);
       // Chargement de l'interface graphique
       buildInterface();
       // Adapation de la difficulte en fonction de preferences de l'utilisateur
       setDifficulty();
       curPiece = new Shape();
       curBonus = new Bonus();
       curLine = -1;
       timer = new Timer(800, this); // vitesse de descente des pieces
       timer.start();

       /*statusbar =  parent.getStatusBar();*/
       board = new Tetrominoes[BoardWidth * BoardHeight];
       bricks = new int[BoardWidth][BoardHeight][3]; // 0 = id; 1 = lettre; 2 = click
       bonus = new Bonuses[BoardWidth * BoardHeight];
       score = 0;
       level = 1;
       bonuses = new LinkedList<Bonus>();
       addKeyListener(new TAdapter());
       addMouseListener(new MouseManager());
       clearBoard();
       
       newBonus();
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
    	ImageIcon bgBreak = new ImageIcon(this.getClass().getResource("pictures/bg_accueil.jpg"));         
	   	 	
	    pause = new JLabel(new ImageIcon(bgBreak.getImage()));
	    pause.setSize(525, 700);
	    add(pause);
	    pause.setVisible(true);  
	    picture.setVisible(false);
	    
        /*Couleur des bouton menu*/
        final Color blue =new Color(46,49,146);
        final Color black =new Color(0,0,0);
        final Color white =new Color(255,255,255);
        
        //Bouton Continuer
        final MenuButton bt_continue = new MenuButton("Reprendre le jeu", blue, black, 200, false);
        pause.add(bt_continue);
        /*On affiche la page du jeu*/
        bt_continue.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_continue.SetForegroundandFill(white, true);
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {
        		bt_continue.SetForegroundandFill(black, false);
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
                pause(); 
        	}
        });
        
        //Bouton Nouvelle Partie
        final MenuButton bt_start = new MenuButton("Nouvelle Partie", blue, black, 280, false);
        pause.add(bt_start);
        /*On affiche la page du jeu*/
        bt_start.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_start.SetForegroundandFill(white, true);
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {
        		bt_start.SetForegroundandFill(black, false);
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
                //On ouvre une fenetre avec une nouvelle partie
                GameFrame game = new GameFrame();
                game.setLocationRelativeTo(null);
                game.setVisible(true);
                //On ferme l'ancienne fenetre
                Window window = SwingUtilities.windowForComponent(pause);
            	if (window instanceof JFrame) {
            		JFrame frame = (JFrame) window;
            		frame.setVisible(false);
            		frame.dispose();
            	}
        	}
        });
        
        //Bouton Menu principal
        final MenuButton bt_menu = new MenuButton("Menu principal", blue, black, 360, false);
        pause.add(bt_menu);
        bt_menu.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_menu.SetForegroundandFill(white, true);
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {
        		bt_menu.SetForegroundandFill(black, false);
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
                MainMenu menu = new MainMenu();
                //Ouvrir au même endroit que le menu
                menu.setLocationRelativeTo(null);
                menu.setVisible(true);
                Window window = SwingUtilities.windowForComponent(pause);
            	if (window instanceof JFrame) {
            		JFrame frame = (JFrame) window;
            		frame.setVisible(false);
            		frame.dispose();
            	}  
        	}
        });
        
        // Bouton Quitter
        final MenuButton bt_exit = new MenuButton("Quitter", blue, black, 440, false);
        pause.add(bt_exit);
        // On quitte le jeu
        bt_exit.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_exit.SetForegroundandFill(white, true);
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {
        		bt_exit.SetForegroundandFill(black, false);
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		System.exit(0);
        	}
        });
    }
    
    public void gameOver(){
    	clearBoard();
	    picture.setVisible(false);

    	ImageIcon bgBreak = new ImageIcon(this.getClass().getResource("pictures/gameover.jpg"));         
   	 	
    	gameover = new JLabel(new ImageIcon(bgBreak.getImage()));
    	gameover.setSize(525, 700);
	    add(gameover);
	    gameover.setVisible(true);



        /*Couleur des bouton menu*/
        final Color blue =new Color(46,49,146);
        final Color black =new Color(0,0,0);
        final Color white =new Color(255,255,255);
        
        
        //Bouton Nouvelle Partie
        final MenuButton bt_start = new MenuButton("Nouvelle Partie", blue, black, 280, false);
        gameover.add(bt_start);
        /*On affiche la page du jeu*/
        bt_start.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_start.SetForegroundandFill(white, true);
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {
        		bt_start.SetForegroundandFill(black, false);
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
                //On ouvre une fenetre avec une nouvelle partie
                GameFrame game = new GameFrame();
                game.setLocationRelativeTo(null);
                game.setVisible(true);
                //On ferme l'ancienne fenetre
                Window window = SwingUtilities.windowForComponent(gameover);
            	if (window instanceof JFrame) {
            		JFrame frame = (JFrame) window;
            		frame.setVisible(false);
            		frame.dispose();
            	}
        	}
        });
        
        //Bouton Menu principal
        final MenuButton bt_menu = new MenuButton("Menu principal", blue, black, 360, false);
        gameover.add(bt_menu);
        bt_menu.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_menu.SetForegroundandFill(white, true);
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {
        		bt_menu.SetForegroundandFill(black, false);
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
                MainMenu menu = new MainMenu();
                //Ouvrir au meme endroit que le menu
                menu.setLocationRelativeTo(null);
                menu.setVisible(true);
                Window window = SwingUtilities.windowForComponent(gameover);
            	if (window instanceof JFrame) {
            		JFrame frame = (JFrame) window;
            		frame.setVisible(false);
            		frame.dispose();
            	}  
        	}
        });
        
        // Bouton Quitter
        final MenuButton bt_exit = new MenuButton("Quitter", blue, black, 440, false);
        gameover.add(bt_exit);
        // On quitte le jeu
        bt_exit.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		bt_exit.SetForegroundandFill(white, true);
        	}
        	@Override
        	public void mouseExited(MouseEvent e) {
        		bt_exit.SetForegroundandFill(black, false);
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		System.exit(0);
        	}
        });
    	 	
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
    	
    	// Recuperer une propriete d'un fichier de configurations
   	 	String configUniv = options.getProperty("Univers"); 
   	 	
    	switch (configUniv)
 	   	{
 	   	  case "western":
 	        // Ajout d'une image de fond
 	        ii = new ImageIcon(this.getClass().getResource("pictures/cowboys.jpg"));
 	        btSound = new ImageIcon(this.getClass().getResource("pictures/sound_ninja.png"));
 	        btMute = new ImageIcon(this.getClass().getResource("pictures/mute_ninja.png"));
 	        btBreak = new ImageIcon(this.getClass().getResource("pictures/break_ninja.png"));
 	   	    break;
 	   	  case "kungfu":
 	        // Ajout d'une image de fond
 	        ii = new ImageIcon(this.getClass().getResource("pictures/ninjas.jpg"));
 	        btSound = new ImageIcon(this.getClass().getResource("pictures/sound_ninja.png"));
 	        btMute = new ImageIcon(this.getClass().getResource("pictures/mute_ninja.png"));
 	        btBreak = new ImageIcon(this.getClass().getResource("pictures/break_ninja.png"));
 	   	    break; 
 	   	  case "pirate":
 	         // Ajout d'une image de fond
 	         ii = new ImageIcon(this.getClass().getResource("pictures/pirates.jpg"));
	 	     btSound = new ImageIcon(this.getClass().getResource("pictures/sound_ninja.png"));
 	         btMute = new ImageIcon(this.getClass().getResource("pictures/mute_ninja.png"));
 	         btBreak = new ImageIcon(this.getClass().getResource("pictures/break_ninja.png"));
 	   	    break; 
 	   	  case "batman":
 	         // Ajout d'une image de fond
 	         ii = new ImageIcon(this.getClass().getResource("pictures/batman.jpg"));
	 	     btSound = new ImageIcon(this.getClass().getResource("pictures/sound_batman.png"));
	 	     btMute = new ImageIcon(this.getClass().getResource("pictures/mute_batman.png"));
	 	     btBreak = new ImageIcon(this.getClass().getResource("pictures/break_batman.png"));  
 	   	    break; 
 	   	  default:
 	         // Ajout d'une image de fond
 	         ii = new ImageIcon(this.getClass().getResource("pictures/pirates.jpg"));         
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
	        		bt_sound.setIcon(btMute);
	        		System.out.println("Et je coupe le son (mais en fé il n'en a pas :o)");
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
    	printScore = new JLabel(Integer.toString(0));
    	printScore.setBounds(40,55, 100, 100); 
    	printScore.setFont(font);
    	printScore.setForeground(Color.white);
    	picture.add(printScore);
    	
    	//On affiche le level
        printLevel = new JLabel(Integer.toString(1)); 
        printLevel.setFont(font_level);
        printLevel.setForeground(Color.white);
        printLevel.setBounds(310, 582, 300, 100);  
    	picture.add(printLevel);
        
        //On affiche le nombre de ligne
        printLine = new JLabel(Integer.toString(0)); 
        printLine.setFont(font);
        printLine.setForeground(Color.white);
        printLine.setBounds(40, 140, 300, 100);  
    	picture.add(printLine);
    }
    
    // Dessine tous les objets
    public void paint(Graphics g)
    {   
    	// TODO
    	/*Random r = new Random();
    	if(Math.abs(r.nextInt()) % 10 + 1 == 1 && curBonus == null) {
    		System.out.println("Creation d'un bonus");
    		//newBonus();
    	}*/
    	
    	super.paint(g);

        // Dimension size = getSize();    	    	
    	
    	if(!isPaused) {
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
            
            // Dessine le bonus
            if(curBonus.getBonus() != Bonuses.NoBonus) {
            	if(!getBonus){
	            	int x = curBonus.x();
	                int y = curBonus.y();
	                drawBonus(BoardLeft + x * squareWidth(),
	                           BoardTop + (BoardHeight - y - 1) * squareHeight(),
	                           curBonus.getBonus());
	            	getBonus = true;
            	}
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
            bonus[i] = Bonuses.NoBonus;
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
            bonus[(y * BoardWidth) + x] = curBonus.getBonus();
        }

        if (!isFallingFinished)
            newPiece();
        	score += 5;
            level = score/100 + 1;
            printScore.setText(Integer.toString(score)); 	
			printLevel.setText(Integer.toString(level));
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
    	System.out.println("Bonus (" + curBonus.x() + "," + curBonus.y() + ")");
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
    	        	// TODO
    	        	System.out.println("Collision");
    	        	bonuses.add(curBonus);
    	        	getBonus = false;
    	        	curBonus = new Bonus();
    	        }
            }
        	
        	// Verifie la collision entre les pieces en bas et le bonus
        	for (int i = 0; i < BoardHeight; ++i) {
                for (int j = 0; j < BoardWidth; ++j) {
                	Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                	//Bonuses bonus = bonusAt(j, BoardHeight - i - 1);
                    if (shape != Tetrominoes.NoShape && curBonus.x() == j && curBonus.y() == i) {
                    	bonuses.add(curBonus);
                    	curBonus = new Bonus();
                    }
                }
            }
        } else {
        	Random r = new Random();
        	int x = Math.abs(r.nextInt()) % 10;
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
        			//bonus[(k * BoardWidth) + j] = bonusAt(j, k + 1);
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
    
private void drawBonus(int x, int y, Bonuses bonus) {
    	
    	switch (bonus)
 	   	{
 	   	  case Score:
 	        // Ajout d'une image de fond
 	   		bonusImg = new ImageIcon(this.getClass().getResource("pictures/+50.png"));
 	   	    break;
 	   	  case Worddle:
 	        // Ajout d'une image de fond
 	   		bonusImg = new ImageIcon(this.getClass().getResource("pictures/worddle.png"));
 	   	    break; 
 	   	  case Speed:
 	         // Ajout d'une image de fond
 	   		bonusImg = new ImageIcon(this.getClass().getResource("pictures/fusee.png"));
 	   	    break; 
 	   	  default:
 	         // Ajout d'une image de fond
 	   		bonusImg = new ImageIcon(this.getClass().getResource("pictures/explosion.png"));        
 	   	}
    	
        labelBonus.setIcon(bonusImg);	
        labelBonus.setBounds(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2); 
        picture.add(labelBonus);
        System.out.println(bonus);
        
    	/*Color colors[] = { 
    		new Color(0, 0, 0), new Color(102, 51, 51), 
    		new Color(51, 102, 51), new Color(51, 51, 102)
        };


        Color color = colors[bonus.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                         x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                         x + squareWidth() - 1, y + 1);*/
    }
    
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
    
    /*private void worddle(int x, int y) {
    	
    }*/
    
    class MouseManager implements MouseListener
    {
		@Override
    	public void mouseClicked(MouseEvent e)
        {
	    	// Verifie si on clique dans l'espace du jeu
	    	if(e.getX() >= BoardLeft && e.getX() <= (BoardLeft+GameWidth) && e.getY() >= BoardTop && e.getY() <= (BoardTop+GameHeight)) {
	    		//System.out.println("Clic dans le bidule");
	    		anagram(e.getX(), e.getY());
	    	}
	    	else {
	    		//System.out.println("Clic ailleurs");
	    	}
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
