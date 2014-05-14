package tetraword;

import java.awt.Color;
import java.awt.Font;
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
	
	// Interface
	private ImageIcon ii, imgBonus, btSound, btMute, btBreak;
    private JLabel picture, bt_sound, bt_break, pause, gameover, endgame;
    private JLabel printBonus = new JLabel(); 
    private JLabel printWord = new JLabel(); 
    private JLabel labelBonus = new JLabel();
    private PrintText printScore, printLevel, printLine;
    
    // Son
    private PlaySound sound;
	private PlaySound soundRight = new PlaySound("right");
	private PlaySound soundWrong = new PlaySound("wrong");
    
    // Dimensions du plateau
	final int BoardWidth = 10;
    final int BoardHeight = 20;
    final int BoardTop = 64;
    final int BoardLeft = 120;
    final int GameWidth = 279;
    final int GameHeight = 560;
    
    // Configuration du jeu
    private String configUniv;
    
    // Evenements sur le plateau
    Timer timer;
    boolean isStarted = false; // le jeu est-il actif
    boolean isPaused = false; // le jeu est-il en pause
    private boolean isSound = true; // le son est-il active ?
    int speed = 1000; // vitesse de chute des pieces
    int score = 0; // score
    int level = 1; // niveau
    
    // Gestion des Tetrominoes                        
    Tetrominoes[] board; // tableau contenant toutes formes presentes sur le plateau en fonction des coordonnees
    int[][][] bricks; // tableau qui contient l'id, la lettre et le clic pour chaque brique selon ses coordonnees dans le board
    int lastShapeId = -4; // dernier id attribue a une brique
    // Tetrominoes qui tombe
    Shape curPiece; // forme de la piece qui tombe 
    int curX = 0; // position de la piece qui tombe
    int curY = 0; // position de la piece qui tombe
    boolean isFallingFinished = false; // la piece a-t-elle fini de tomber ?
    
    // Gestion de la saisie des lettres
    String inputLetters = ""; // lettres saisies au clic par l'utilisateur
    String bestAnagram = ""; // meilleur anagramme possible sur la ligne actuelle
    // Anagramme
    int curLine = -1; // ligne sur laquelle l'utilisateur clique
    int difficulty = 30; // difficulte des anagrammes
    int numLinesRemoved = 0; // nombre de lignes supprimees avec le mode anagramme
    
    // Bonus
	private Timer timerBonus;
	private int elapsedTime = 0; // Temps ecoule depuis le debut du timer
	private Bonus curBonus; // Bonus actuellement affiche sur le plateau
	private Bonuses activeBonus; // Bonus en cours
	// Worddle
	private int[] previousCoords; // Coordonnees de la derniere lettre cliquee en mode worddle
	int[][][] curWorddle; // Mot actuellement saisi en mode worddle
						  // (0,0) = x, (1,0) = y, (2,_) = lettre, validation
	int scoreWorddle = 0; // Score a ajouter a la fin du mode worddle

	public Board(GameFrame parent) throws UnsupportedAudioFileException, IOException {
       setFocusable(true);
       
       // Chargement de l'interface graphique
       buildInterface();

       //Musique
       sound = new PlaySound(configUniv);
       sound.loop();
       
       // Timer pour la chute des pieces
       timer = new Timer(speed, this); // vitesse de descente des pieces
       timer.start();
       
       // Gestion des tetrominoes
       // Tableau de sauvegarde des Tetrominoes
       board = new Tetrominoes[BoardWidth * BoardHeight];   
       // Tableau de sauvegarde des briques
       bricks = new int[BoardWidth][BoardHeight][3]; // 0 = id; 1 = lettre; 2 = click   
       // Piece qui tombe
       curPiece = new Shape();
       
       // Bonus
       curBonus = new Bonus();
       timerBonus = new Timer(speed, new ActionListener() {
    	   @Override
    	   public void actionPerformed(ActionEvent ae) {
    		   elapsedTime += timerBonus.getDelay();
    		   
    		   switch (activeBonus) {
    		   	case NoBonus:
	    			// Suppression du bonus affiche au bout de 5 secondes
	    			if(elapsedTime >= 5000) {
	        		   picture.remove(labelBonus);
	        		   curBonus = new Bonus();
	        		   
	    			   timerBonus.stop();
	    			   elapsedTime = 0;
	    			   printBonus();
	    			}
	    			break;
	    		case Worddle:
	    			// Desactivation du mode worddle au bout de 30 secondes
	    			if (elapsedTime >= 30000) {
	    	   			System.out.println("Worddle off");
	        			System.out.println("Suppression des briques Worddle");
	        			addPoints(scoreWorddle);
	        			System.out.println("Score total du worddle : " + scoreWorddle);
	        			scoreWorddle = 0;
	    				removeBricksWorddle();
	        			activeBonus = Bonuses.NoBonus;
	    				timerBonus.stop();
	    				elapsedTime = 0;
	    				printBonus();
	       			}
	    			break;
	    		case BonusSpeed:
	    			// Retour a la vitesse normale au bout de 10 secondes
	    			if(elapsedTime >= 10000) {
	     			   System.out.println("Bonus speed off");
	     			   updateSpeed();
	     			   activeBonus = Bonuses.NoBonus;
	     			   timerBonus.stop();
	     			   elapsedTime = 0;
	     			   printBonus();
	    			}
	    			break;
	    		case MalusSpeed:
	    			// Retour a la vitesse normale au bout de 10 secondes
	    			if(elapsedTime >= 10000) {
	    				System.out.println("Bonus speed off");
	    				updateSpeed();
	    				activeBonus = Bonuses.NoBonus;
	    				timerBonus.stop();
	    				elapsedTime = 0;
	    				printBonus();
	    			}
	    			break;
	    		default:
    				activeBonus = Bonuses.NoBonus;
    				timerBonus.stop();
    				elapsedTime = 0;
    				printBonus();
	    			break;
    		   }
    	   }
       });
       activeBonus = Bonuses.NoBonus;
       // Worddle
       curWorddle = new int[BoardWidth][BoardHeight][2]; // Pour l'indice 2 : 0 = lettre; 1 = validation
       previousCoords = new int[2];
       
       addKeyListener(new TAdapter());
       addMouseListener(new MouseManager());
       
       setDifficulty();
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
            breakMenu();
    		System.out.println("Pause");
        } else {
            timer.start();
            if(isSound) sound.loop();
            pause.setVisible(false);
            picture.setVisible(true);
        }
        repaint();
    }
    
	//Menu pause
    public void breakMenu() {
    	sound.stop();
    	
    	ImageIcon bgBreak = new ImageIcon(this.getClass().getResource("/pictures/pause.jpg"));            	 	
	    pause = new JLabel(new ImageIcon(bgBreak.getImage()));
	    pause.setSize(525, 700);
	    add(pause);
	    pause.setVisible(true);  
	    picture.setVisible(false);
	    
        
        // Bouton Continuer
        final MenuButton bt_continue = new MenuButton("Reprendre le jeu", 200);
        pause.add(bt_continue);
        
        // Affichage la page du jeu
        bt_continue.addMouseListener(new MouseAdapter() {
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
    public void gameOver() {
    	clearBoard();
    	sound.stop();
    	PlaySound soundGameOver = new PlaySound("gameover");
		if(isSound) soundGameOver.play();
		
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
    
	//Menu jeu termine
    public void endGame() {
    	clearBoard();    	
    	sound.stop();
    	timer.stop();
    	timerBonus.stop();
    	curPiece.setShape(Tetrominoes.NoShape);
    	
    	PlaySound soundEndGame = new PlaySound("gagne");
		if(isSound) soundEndGame.play();
		
	    picture.setVisible(false);

    	ImageIcon bgBreak = new ImageIcon(this.getClass().getResource("/pictures/gagne.jpg"));         
   	 	
    	endgame = new JLabel(new ImageIcon(bgBreak.getImage()));
    	endgame.setSize(525, 700);
	    add(endgame);
	    endgame.setVisible(true);
        
        MenuButton.buttonNewGame(endgame, 280);
        MenuButton.buttonMenu(endgame,360);        
        MenuButton.buttonClose(endgame, 440);
    }
    
    public void buildInterface(){
    	Properties options = new Properties();     	
    	// Creation d'une instance de File pour le fichier de config
    	File fichierConfig = new File("src/conf/conf.properties"); 
    	
    	// Chargement du fichier de configuration
    	try {
    		options.load(new FileInputStream(fichierConfig));
    	} 
    	catch(IOException e) {
    		System.out.println("Echec chargement");
    	}

   	    configUniv = options.getProperty("Univers");
   	    System.out.println(configUniv);
   	 	ii = new ImageIcon(this.getClass().getResource("/pictures/"+configUniv+".jpg"));   	 	
        picture = new JLabel(new ImageIcon(ii.getImage()));
        picture.setSize(525, 700);
        add(picture);
   	 	
        // Boutons gestion du son
   		btSound = new ImageIcon(this.getClass().getResource("/pictures/sound_"+configUniv+".png"));
   		btMute = new ImageIcon(this.getClass().getResource("/pictures/mute_"+configUniv+".png"));
   		btBreak = new ImageIcon(this.getClass().getResource("/pictures/break_"+configUniv+".png")); 

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
	        		bt_sound.setIcon(btMute);
	        		System.out.println("Et je coupe le son");
	        		isSound = false;
        		}
        		else{
        			sound.loop();
        			bt_sound.setIcon(btSound);
	        		System.out.println("Et je remets le son");
	        		isSound = true;
        		}
        	}
        });
        
        Font font = new Font("Arial",Font.BOLD,22);
        Font font_level = new Font("Arial",Font.BOLD,22);
        
        // On affiche le score
        printScore = new PrintText(Integer.toString(0), 40, 55, font, Color.white, picture);
        // On affiche le level
        printLevel = new PrintText(Integer.toString(1), 290, 593, font_level, Color.white, picture);        
        // On affiche le nombre de ligne
        printLine = new PrintText(Integer.toString(0), 40, 140, font, Color.black, picture);    
    }
    
    private void printBonus(){    
		if(activeBonus == Bonuses.NoBonus || activeBonus == Bonuses.BonusScore || activeBonus ==  Bonuses.MalusScore ){
			printBonus.setIcon(null);
		}
		else{
	    	imgBonus = new ImageIcon(this.getClass().getResource("/pictures/bonus/"+activeBonus+".png"));
			printBonus.setIcon(imgBonus);
			printBonus.setBounds(440, 175, 30, 30); 
		    picture.add(printBonus);
		}
	}

	// Dessine tous les objets
    @Override
    public void paint(Graphics g) {   	
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
		g.drawString(letter, x + 11, y + 19);
	
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
	    
	    bonusCollision();
	
	    return true;
	}

	private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1))
                break;
            --newY;
        }
        addPoints(5);

        pieceDropped();
    }

    private void oneLineDown() {        
    	if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped();
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

	private void pieceDropped() {
	    for (int i = 0; i < 4; ++i) {
	        int x = curX + curPiece.x(i);
	        int y = curY - curPiece.y(i);
	        
	        board[(y * BoardWidth) + x] = curPiece.getShape();
	        bricks[x][y][0] = curPiece.getId(i);
	        bricks[x][y][1] = curPiece.getLetter(i);
	        bricks[x][y][2] = curPiece.getClick(i);
	    }
	    
	    if (!isFallingFinished) {
	    	newPiece();
	    	addPoints(5);
	    }
	}

	private void bonusCollision(){    	
		if (curBonus.getBonus() != Bonuses.NoBonus) {
	    	// Verifie la collision entre la piece qui tombe et le bonus
	    	for(int i = 0; i < 4; i++) {
		    	//System.out.println("Piece : (" + (curX + curPiece.x(i)) + ";" + (curY + curPiece.y(i)) + ")");
		        //System.out.println("Bonus : (" + curBonus.x() + ";" + curBonus.y() + ")");
		        if((curX + curPiece.x(i)) == curBonus.x() && (curY + curPiece.y(i)) == curBonus.y()) {
		        	System.out.println(curBonus.getBonus());
		        	picture.remove(labelBonus);
		        	applyBonus(curBonus.getBonus());
		            curBonus.setBonus(Bonuses.NoBonus);
		            break;
		        }
	        }
	    	
	    	// Verifie la collision entre les pieces en bas et le bonus
	    	for (int i = 0; i < BoardHeight; ++i) {
	            for (int j = 0; j < BoardWidth; ++j) {
	            	Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
	                if (shape != Tetrominoes.NoShape && curBonus.x() == j && curBonus.y() == i) {
	                	 //curBonus = new Bonus();
	                	curBonus.setBonus(Bonuses.NoBonus);
	                }
	            }
	        }
	    } else {
	    	Random r = new Random();
	    	int x = Math.abs(r.nextInt()) % 10; // TODO Changer la frequence d'apparition des bonus
	    	if(x == 1 && activeBonus == Bonuses.NoBonus) {
	    		newBonus();
	    	}
	    }
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
		boolean valid = validateWordAnagram(inputLetters);
		if(valid && isLineFull(y)) {
	    	if(isSound) soundRight.play();
			++numLinesRemoved;
			printLine.setText(Integer.toString(numLinesRemoved));
			scoreAnagram(inputLetters);
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
		else if(!valid && isLineFull(y)) {
	    	if(isSound) soundWrong.play();
	        for (int k = y; k < BoardHeight - 1; ++k) {
	        	for (int j = 0; j < BoardWidth; ++j) {
	        		// Les lettres ne sont plus considerees comme deja cliquees
	        		bricks[j][k][2] = 0;
	        	}
	        }
	    }
	    
	    inputLetters = "";
	    curLine = -1;
	    
	    printWord.setText(inputLetters);
		printWord.setBounds(300,-25, 300, 100);     	
		picture.add(printWord);
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
	    activeBonus = Bonuses.NoBonus;
	}

	private void clearBoard() {
    	score = 0;
        for (int i = 0; i < BoardHeight * BoardWidth; ++i) {
            board[i] = Tetrominoes.NoShape;
        }
        clearCurWorddle();
        curBonus.setBonus(Bonuses.NoBonus);
        activeBonus = Bonuses.NoBonus;
        curLine = -1;
    }

    private void newPiece() {
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
    
    private void newBonus() {
    	curBonus.setRandomBonus();
    	curBonus.setRandomX();
    	curBonus.setRandomY();
    	while(shapeAt(curBonus.x(), curBonus.y()) != Tetrominoes.NoShape) {
    		curBonus.setRandomX();
        	curBonus.setRandomY();
    	}
    	
    	activeBonus = Bonuses.NoBonus;
		timerBonus.start();
    }
    
    private void setScore(int points) {
    	score = points;
    	updateScore();
    }
    
    private void addPoints(int points) {
    	score += points;
    	updateScore();
    }
    
    private void updateScore() {
    	if(level != score/100 + 1) {
    		updateSpeed();
    	}
        level = score/100 + 1;
        if (score < 0) {
        	score = 0;
        } else if(score >= 1000) {
        	System.out.println("FELICITATIONS ! Vous avez gagne un Mars !");
        	endGame();
        }
        printScore.setText(Integer.toString(score)); 	
		printLevel.setText(Integer.toString(level));
    }
    
    private void setSpeed(int newSpeed) {
    	if(newSpeed < 50) {
    		speed = 50;
    	} else {
    		speed = newSpeed;
    	}
    	//System.out.println("vitesse : " + speed);
        timer.setDelay(speed);
    }
    
    private void updateSpeed() {
    	setSpeed(1000 - (level * 90));
    }

    private void setDifficulty(){
       	Properties options = new Properties();     	
    	// Creation d'une instance de File pour le fichier de config
    	File fichierConfig = new File("src/conf/conf.properties"); 
    	
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
    
    private void applyBonus(Bonuses bonusType) {  	        
		switch (bonusType) {
			case NoBonus:
				break;
			case Worddle:
				inputLetters = "";
				activeBonus = Bonuses.Worddle;
				timerBonus.start();
				break;
			case BonusSpeed:
				activeBonus = Bonuses.BonusSpeed;
				setSpeed(speed/2);
				timerBonus.start();
				break;
			case MalusSpeed:
				activeBonus = Bonuses.MalusSpeed;
				setSpeed(speed*2);
				timerBonus.start();
				break;
			case BonusScore:
				activeBonus = Bonuses.BonusScore;
				scoreBonus();
				break;
			case MalusScore:
				activeBonus = Bonuses.MalusScore;
				scoreBonus();
				break;
	    }
		printBonus();
	}

	private void anagram (int x, int y) {        
	    // Conversion des pixels (recuperes au clic) en nombre de cases
	    // x appartient a [0,9]
	    // y appartient a [19,0]
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
				
				// Affichage des lettres saisies par l'utilisateur
				printWord.setText(inputLetters);
		    	printWord.setBounds(300,-25, 300, 100);     	
		    	picture.add(printWord);
			} else {
				System.err.println("La lettre a deja ete utilisee");
			}
		}
		else {
			bestAnagram = "";
			System.err.println("La ligne n'est pas pleine");
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
				
				// Verifie si la brique n'est pas deja utilisee dans le mot
				if(curWorddle[newX][newY][1] == 0) {
					inputLetters += Character.toLowerCase(letterAt(newX,newY));
					curWorddle[newX][newY][1] = 1;
					previousCoords[0] = newX;
					previousCoords[1] = newY;
					
					// Affichage des lettres saisies par l'utilisateur
	    			printWord.setText(inputLetters);
	    	    	printWord.setBounds(300,-25, 300, 100);
	    			//System.out.println("Les lettres saisies jusqu'a l'instant sont " + inputLetters);  	
	    	    	picture.add(printWord);
				} else {
					System.err.println("La lettre a deja ete utilisee pour ce mot");
				}
			} else {
				// Nouveau mot
				inputLetters = "";
				clearCurWorddle();
				inputLetters += Character.toLowerCase(letterAt(newX,newY));
				curWorddle[newX][newY][1] = 1;
				previousCoords[0] = newX;
				previousCoords[1] = newY;
				
				// Affichage des lettres saisies par l'utilisateur
				printWord.setText(inputLetters);
		    	printWord.setBounds(300,-25, 300, 100);
				//System.out.println("Les lettres saisies jusqu'a l'instant sont " + inputLetters);  	
		    	picture.add(printWord);
			}
		} else {
			// Nouveau mot
			inputLetters = "";
			clearCurWorddle();
			inputLetters += Character.toLowerCase(letterAt(newX,newY));
			curWorddle[newX][newY][1] = 1;
			previousCoords[0] = newX;
			previousCoords[1] = newY;
			
			// Affichage des lettres saisies par l'utilisateur
			printWord.setText(inputLetters);
	    	printWord.setBounds(300,-25, 300, 100);
			//System.out.println("Les lettres saisies jusqu'a l'instant sont " + inputLetters);  	
	    	picture.add(printWord);
		}
	}

	private void clearCurWorddle() {
		for (int i = 0; i < BoardWidth; ++i) {
	        for (int j = 0; j < BoardHeight; ++j) {
	        	curWorddle[i][j][1] = 0;
	        }
		}
	}

	private void scoreAnagram(String word) {
		if(word != null && !word.isEmpty()) {
			// Augmentation du score
			//System.out.println("Nombre de lettres saisies : " + inputLetters.length());
			//System.out.println("Longueur du meilleur anagramme : " + bestAnagram.length());
			System.out.println("Score anagramme : " + (int)((double)inputLetters.length() / (double)bestAnagram.length() * 30));
			addPoints((int)((double)inputLetters.length() / (double)bestAnagram.length() * 30));
		}
	}

	private void scoreBonus() {
		Random r = new Random();
	    int x = (Math.abs(r.nextInt()) % 5 + 1) * 10;
	    if(activeBonus == Bonuses.BonusScore) {
	    	System.out.println("Bonus de " + x + " points");
	        addPoints(x);
	    } else if (activeBonus == Bonuses.MalusScore) {
	    	System.out.println("Malus de " + x + " points");
	        addPoints(-x);
	    }
	    activeBonus = Bonuses.NoBonus;
	}

	private void scoreWorddle(String word) {
		if(word != null && !word.isEmpty()) {
			int oldScoreWorddle = scoreWorddle;
			// Calcul du score
			char letter;
			for(int i = 0; i < inputLetters.length(); ++i) {
				letter = inputLetters.charAt(i);
				if(letter == 'k' || letter == 'w' || letter == 'x' ||
					letter == 'y' || letter == 'z') {
					scoreWorddle += 10;
				} else if (letter == 'j' || letter == 'q') {
					scoreWorddle += 8;
				} else if (letter == 'f' || letter == 'h' || letter == 'v') {
					scoreWorddle += 4;
				} else if (letter == 'b' || letter == 'c' || letter == 'p') {
					scoreWorddle += 3;
				} else if (letter == 'd' || letter == 'm' || letter == 'g') {
					scoreWorddle += 2;
				} else {
					scoreWorddle += 1;
				}
			}
			
			if(inputLetters.length() > 5 && inputLetters.length() < 8) {
				scoreWorddle *= 2;
			} else if (inputLetters.length() >= 8 && inputLetters.length() < 11) {
	    		scoreWorddle *= 3;
			} else if (inputLetters.length() >= 11) {
				scoreWorddle *= 4;
			}
			
			// Augmentation du score
			System.out.println("Score mot worddle : " + (scoreWorddle - oldScoreWorddle));
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
				 if(validWord) {
			    	System.out.println("L'anagramme est correct");
				 }
				 if(!validWord){
			    	System.out.println("L'anagramme est incorrect");
				 }
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
	    		scoreWorddle(word);
	    		if(isSound)  soundRight.play();
	    		System.out.println("Le mot est correct");
	    	} else {
	    		if(isSound) soundWrong.play();
	    		System.out.println("Le mot est incorrect");
	    	}
		}
		
		clearCurWorddle();
		inputLetters = "";
		
		printWord.setText(inputLetters);
    	printWord.setBounds(300,-25, 300, 100);     	
    	picture.add(printWord);
    	
		return validWord;
    }
    
    class MouseManager implements MouseListener
    {
		@Override
    	public void mouseClicked(MouseEvent e)
        {
	    	// Verifie si on clique dans l'espace du jeu
	    	if(e.getX() >= BoardLeft && e.getX() <= (BoardLeft+GameWidth) && e.getY() >= BoardTop && e.getY() <= (BoardTop+GameHeight)) {
	    		//System.out.println("Clic dans le bidule");
	    		if(activeBonus == Bonuses.Worddle) { 
	    			worddleBonus(e.getX(), e.getY()); 
	    			} 
	    		else { 
	    			anagram(e.getX(), e.getY()); 
	    			}
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
    		
    		if (keycode == 'c' || keycode == 'C') {
    			setScore(900);
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
	    			if(activeBonus == Bonuses.Worddle) { 
	    				//System.out.println("Entree en mode Worddle"); 
	    				validateWordWorddle(inputLetters); 
	    			} 
	    			else if(curLine != -1) { 
	    				//System.out.println("Entree en mode Anagramme"); 
	    				removeLine(curLine); 
	    			} 
	    			break;
             }
         }
     }
}
