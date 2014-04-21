package tetraword;

import java.io.File;
import java.io.IOException;
 
 import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
 
 
 public class PlaySound {    
	 
	 //audioStream en public static pour partager ses variables avec le thread
     public static AudioInputStream audioStream = null;
     public static SourceDataLine line = null;
     private AudioFormat audioFormat = null;
 
     public PlaySound(File f) throws Exception{
         audioStream = AudioSystem.getAudioInputStream(f);
         audioFormat = audioStream.getFormat();
         DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
 
         try {
         line = (SourceDataLine) AudioSystem.getLine(info);
         } catch (LineUnavailableException e) {
		     e.printStackTrace();
         }		
     }
 
     //Ouverture du flux audio
     public boolean open(){
 
             try {
            	 line.open(audioFormat);
             } catch (Exception e) {
            	//pour le debugage
                 e.printStackTrace();
                 return false;
             }
         return true;
     }
 
     //Fermeture du flux audio
     public void close(){
         line.close();
     }
 
     //On joue le son
     public void play(){
    	 /*On lance le thread ce qui a pour effet de lancer la méthode run, le thread se lance en parallèle mais renvoie tout de suite 
    	  * la main au programme qui continue*/
    	 ThreadPlaySound t = new ThreadPlaySound();
    	 t.start();	
    	 System.out.println("test");
     }
 
     //On arrête le son
     public void stop(){
    	 line.stop();
     }
 
     
   /*  public static void Playlist() throws Exception{
    	 
 	    String Nom = null ;
 	    
 	    String tabMusique[]= new String [5];
 	    PlaySound wp;
 	   
 	    tabMusique[0]= "bin/tetraword/audio/testi.wav" ;
 	    tabMusique[1]= "bin/tetraword/audio/Tambourin.wav" ;
 	    tabMusique[2]= "bin/tetraword/audio/testi.wav" ;
 	    tabMusique[3]= "bin/tetraword/audio/testi.wav" ;
 	    tabMusique[4]= "bin/tetraword/audio/testi.wav" ;
 	   
 	   int  i = 0 , j =0;
 	       
 	    while (i!=100){	     	   
 	           wp = new PlaySound(new File(tabMusique[i]));
 	           i++ ;    	   
 	   
 	       if(i==5) i=0 ; // RAZ compteur playtist
 	   
 	           wp.open();
 	           System.out.println("zik open");
 	           wp.play();
 	           System.out.println("zik play / i ="+i);
 	   
 	       while(ThreadPlaySound.endMusic == false) 
 	         {
 	    	   
 	    	//   Thread.sleep(1000); 	
 	         } 
 	   
 	       ThreadPlaySound.endMusic = false ;
 	   
 	           //Thread.sleep(1000); 	        	 
 	   
 	           wp.stop();
 	           System.out.println("zik stop");
 	           wp.close();
 	           System.out.println("zik close");
 	     }
     }
     */
 
     public static void main(String [] args) throws Exception{
	    	 
	          PlaySound wp = new PlaySound(new File("bin/tetraword/audio/testi.wav"));
	          wp.open();
	          wp.play();

	          
	          /*
	             wp.stop();
	             wp.close();*/
	         System.out.println("debug0");

	 
    		Dictionary dictionary = new Dictionary();
    		boolean valide;
    		String wordValide = "PETASSE";
    		wordValide = wordValide.toLowerCase();
    		//dictionary.allAnagrams(wordValide);
    		valide = dictionary.validateWord(wordValide);
    		System.out.println(wordValide + " et le resultat est : "+valide); 
    		String s="connefeasse";
    		char tab[] = s.toCharArray(); 
    		String st = dictionary.bestAnagram(tab,s.length());
    		System.out.println(st); 
    	   
    	    }   
     
 
 }
    	

     
