public class Tetraword extends javax.swing.JPanel
implements java.awt.event.KeyListener
{
	int[][] occupied = new int[10][20];
	
	int score=0;  // score
	int lineCompleted = 0;   // number of lines completed
	int level=0;
	 
	javax.swing.JLabel scoreLabel = new javax.swing.JLabel("SCORE : 0");
	javax.swing.JLabel levelLabel = new javax.swing.JLabel("LEVEL : 0");
	
	// [sept tetrominos] [quatre rotations] [quatre carres]
	static int[][][] xRotationArray = {
		{ {0,0,1,2}, {0,0,0,1}, {2,0,1,2}, {0,1,1,1} },  // tetromino numero 0
		{ {0,0,1,1}, {1,2,0,1}, {0,0,1,1}, {1,2,0,1} },  // tetromino numero 1
		{ {1,1,0,0}, {0,1,1,2}, {1,1,0,0}, {0,1,1,2} },  // tetromino numero 2
		{ {0,1,2,2}, {0,1,0,0}, {0,0,1,2}, {1,1,0,1} },  // tetromino numero 3
		{ {1,0,1,2}, {1,0,1,1}, {0,1,1,2}, {0,0,1,0} },  // tetromino numero 4
		{ {0,1,0,1}, {0,1,0,1}, {0,1,0,1}, {0,1,0,1} },  // tetromino numero 5
		{ {0,1,2,3}, {0,0,0,0}, {0,1,2,3}, {0,0,0,0} }   // tetromino numero 6
	};
	 
	static int[][][] yRotationArray = {
		{ {0,1,0,0}, {0,1,2,2}, {0,1,1,1}, {0,0,1,2} },  // tetromino numero 0
		{ {0,1,1,2}, {0,0,1,1}, {0,1,1,2}, {0,0,1,1} },  // tetromino numero 1
		{ {0,1,1,2}, {0,0,1,1}, {0,1,1,2}, {0,0,1,1} },  // tetromino numero 2
		{ {0,0,0,1}, {0,0,1,2}, {0,1,1,1}, {0,1,2,2} },  // tetromino numero 3
		{ {0,1,1,1}, {0,1,1,2}, {0,0,1,0}, {0,1,1,2} },  // tetromino numero 4
		{ {0,0,1,1}, {0,0,1,1}, {0,0,1,1}, {0,0,1,1} },  // tetromino numero 5
		{ {0,0,0,0}, {0,1,2,3}, {0,0,0,0}, {0,1,2,3} }   // tetromino numero 6
	};
	
	public void init()
	{
		this.setPreferredSize(new java.awt.Dimension(640,480)); // Taille de la fenêtre
		this.setBackground(java.awt.Color.GREEN); // Couleur de fond        
		 
	    this.setLayout(null); // Coordonnees absolues des pixels
	    
	    scoreLabel.setBounds(300,50,100,30);  // x,y,w,h (en pixels)
	    this.add(scoreLabel);
	 
	    levelLabel.setBounds(300,100,100,30);
	    this.add(levelLabel);
	}
	 
	public void drawCell(int x,int y)
	{
		// Creation d'un carre rouge au contour noir
	    occupied[x][y] = 1;
	}
	 
	public void eraseCell(int x,int y)
	{
	    occupied[x][y] = 0;
	}
	 
	public void drawToken(int x, int y, int[] xArray, int[] yArray)
	{	
		// Creation des tetrominos
		for (int i=0;i<4;i++)
	    {
			drawCell(x+xArray[i],y+yArray[i]);
	    }   
	}
	 
	public void eraseToken(int x, int y, int[] xArray, int[] yArray)
	{
		for (int i=0;i<4;i++)
		{
			eraseCell(x+xArray[i],y+yArray[i]);
		}
	}
	 
	public void paint(java.awt.Graphics gr)
	{
		// Dessin des tetrominos
		super.paint(gr);
		 
	    for (int x=0;x<occupied.length;x++)
	    {
	    	for (int y=0;y<20;y++)
	    	{
	    		if (occupied[x][y]==1)
	    		{
	    			// Dessin carré
	    			gr.setColor(java.awt.Color.BLACK);
	    			gr.fillRect(x*24,y*24,24,24);
	    			gr.setColor(java.awt.Color.RED);
	    			gr.fillRect(x*24+1,y*24+1,22,22);
	    		}
	    		else
	    		{
	    			// Effacement carré
	    			gr.setColor(java.awt.Color.BLACK);
	    			gr.fillRect(x*24,y*24,24,24);
	    		}
	    	}
	    }
	}
	 
	public boolean isValidPosition(int x,int y, int tokenNumber, int rotationNumber)
	{
		int[] xArray = xRotationArray[tokenNumber][rotationNumber];
		int[] yArray = yRotationArray[tokenNumber][rotationNumber];
	     
		for (int i=0;i<4;i++)  // Boucle pour les quatre carres
		{
			int xCell = x+xArray[i];
			int yCell = y+yArray[i];
	 
			// Test des limites
			if (xCell<0) return false;
			if (xCell>=10) return false;
			if (yCell<0) return false;
			if (yCell>=20) return false;
	 
	      // Test d'occupation
	      if (occupied[xCell][yCell]==1) return false;
	    }
	    return true;
	  }
	 
	public void randomTokenTest()
	{
		try { Thread.sleep(1000); } catch (Exception ignore) {}
		 
		int x,y,tokenNumber,rotationNumber;
	 
		while (true)  // Boucle tant que la position est valide
		{
			x=(int) (10*Math.random());    // random x: 0 a 9
			y=(int) (20*Math.random());    // random y: 0 a 19
	 
			tokenNumber = (int) (7*Math.random());
			rotationNumber = (int) (4*Math.random());
	 
			if (isValidPosition(x,y,tokenNumber,rotationNumber)) break;
		}
	 
		int[] xArray = xRotationArray[tokenNumber][rotationNumber];
	    int[] yArray = yRotationArray[tokenNumber][rotationNumber];
	 
	    drawToken(x,y,xArray,yArray);
	    repaint();
	}
	 
	 
	public void clearCompleteRow(int[] completed)
	{
		// Effacer
		for (int i=0;i<completed.length;i++)
		{
			if (completed[i]==1)
			{
				for (int x=0;x<10;x++)
				{
					occupied[x][i]=0;
				}
			}
		}
	 
		repaint();
	}
	 
	public void shiftDown(int[] completed)
	{
		for (int row=0;row<completed.length;row++)
		{
			if (completed[row]==1)
			{
				for (int y=row;y>=1;y--)
				{
					for (int x=0;x<10;x++)
					{
						occupied[x][y] = occupied[x][y-1];
					}
				}
			}
		}
	}
	 
	public void checkRowCompletion()
	{
		int[] complete = new int[20];
		for (int y=0;y<20;y++)  // 20 lignes
		{
			int filledCell = 0;
			for (int x=0;x<10;x++)  // 10 colonnes
			{
				if (occupied[x][y]==1) filledCell++;
				if (filledCell==10) // lignes completes
				{
					complete[y]=1;
				}
			}
		}
	 
		clearCompleteRow(complete);
		 
		shiftDown(complete);   
		 
	    addScore(complete);
	}
	 
	void addScore(int[] complete)
	{
		int bonus=10;  // Score pour la premiere ligne complete
		for (int row=0;row<complete.length;row++)
		{
			if (complete[row]==1)
			{
				lineCompleted += 1;
				score+=bonus;
				bonus*=2;  // Double le bonus pour chaque ligne supplementaire
			}
		}
	 
		// Changement de niveau toutes les 3 lignes completes
		level = lineCompleted/3;  
		if (level>30) { lineCompleted=0; level=0; }  // NIVEAU MAXIMUM
		 
		scoreLabel.setText("SCORE : "+score);
		levelLabel.setText("LEVEL : "+level);
	  }
	
	boolean gameOver=false;
	public void addFallingToken()
	{
		int x=5,y=0;
		int tokenNumber, rotationNumber;
		
		tokenNumber = (int) (7*Math.random());
		rotationNumber = (int) (4*Math.random());
	 
		int[] xArray = xRotationArray[tokenNumber][rotationNumber];
		int[] yArray = yRotationArray[tokenNumber][rotationNumber];
	 
		if (!isValidPosition(x,y,tokenNumber,rotationNumber)) 
		{
			gameOver=true;
			drawToken(x,y,xArray,yArray);
			repaint();
			return;
	    }
		
		drawToken(x,y,xArray,yArray);
		repaint();
	 
	    int delay=50;  // millisecondes
	    int frame=0;
	    boolean reachFloor=false;
	    while (!reachFloor)
	    {
	      try { Thread.sleep(delay); } catch (Exception ignore) {}
	      eraseToken(x,y,xArray,yArray);
	 
	      // add keyboard control
	      if (leftPressed && isValidPosition(x-1,y,tokenNumber,rotationNumber)) x -= 1;
	      if (rightPressed && isValidPosition(x+1,y,tokenNumber,rotationNumber)) x += 1;
	      if (downPressed && isValidPosition(x,y+1,tokenNumber,rotationNumber)) y += 1;
	      if (spacePressed && isValidPosition(x,y,tokenNumber,(rotationNumber+1)%4)) 
	      {
	    	  rotationNumber = (rotationNumber+1)%4;
	    	  xArray = xRotationArray[tokenNumber][rotationNumber];
	    	  yArray = yRotationArray[tokenNumber][rotationNumber];
	    	  spacePressed=false;  
	      }
	 
	      if (frame % 30==0) y += 1;  // fall for every 30 frame
	      if (!isValidPosition(x,y,tokenNumber,rotationNumber)) // reached floor
	      {
	    	  reachFloor=true;
	    	  y -= 1;  // restore position
	      }
	      drawToken(x,y,xArray,yArray);
	      repaint();
	      frame++;
	    }
	}

	public void printGameOver()
	{
		javax.swing.JLabel gameOverLabel = new javax.swing.JLabel("GAME OVER");
		gameOverLabel.setBounds(300,300,100,30);
		add(gameOverLabel);
		repaint();
	}
	 
	 
	boolean leftPressed=false;
	boolean rightPressed=false;
	boolean downPressed=false;
	boolean spacePressed=false;
	 
	// must implements this method for KeyListener
	public void keyPressed(java.awt.event.KeyEvent event)
	{
		// System.out.println(event);
		if (event.getKeyCode()==37) // fleche gauche
		{
			leftPressed=true;
		}
		if (event.getKeyCode()==39) // fleche droite
		{
			rightPressed=true;
		}
		if (event.getKeyCode()==40) // fleche bas
		{
			downPressed=true;
	    }
		if (event.getKeyCode()==32) // barre espace
	    {
			spacePressed=true;
	    }
	 
	}
	 
	// must implements this method for KeyListener
	public void keyReleased(java.awt.event.KeyEvent event)
	{
		// System.out.println(event);
	 
		if (event.getKeyCode()==37) // fleche gauche
		{
			leftPressed=false;
		}
		if (event.getKeyCode()==39) // fleche droite
		{
			rightPressed=false;
		}
		if (event.getKeyCode()==40) // fleche bas
		{
			downPressed=false;
		}
		if (event.getKeyCode()==32) // barre espace
		{
			spacePressed=false;
		}
	 
	}
	 
	// must implements this method for KeyListener
	public void keyTyped(java.awt.event.KeyEvent event)
	{
		// System.out.println(event);
	}
	 
	public static void main(String[] args) throws Exception
	{
		javax.swing.JFrame window = new javax.swing.JFrame("Tetraword"); // Nom de la fenêtre
		window.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		 
		Tetraword tetraword = new Tetraword(); // Création d'un nouveau jeu
		tetraword.init(); // Initialisation du jeu
			 
		window.add(tetraword);
		window.pack();
		window.setVisible(true);
		 
		try { Thread.sleep(1000); } catch (Exception ignore) {}
		 
	    window.addKeyListener(tetraword);  // Ecoute les evenements clavier
	    
		tetraword.gameOver=false;
	    while (!tetraword.gameOver)
	    {
	    	tetraword.addFallingToken();
	    	tetraword.checkRowCompletion();
	    }
	    
	    tetraword.printGameOver();
	}
}
