package tetraword;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
 
 
 public class PlaySound {    
	 
	 //audioStream en public static pour partager ses variables avec le thread
     public static AudioInputStream audioStream = null;
     public static SourceDataLine line = null;
     private AudioFormat audioFormat = null;
 
     public PlaySound(File f) throws UnsupportedAudioFileException, IOException{
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
 
     
 
     public static void main(String [] args) throws Exception{
	    	int i =0; 
	          PlaySound wp = new PlaySound(new File("bin/tetraword/audio/test.wav"));
	          wp.open();
	          wp.play();
    	 while(i==0){

	          if(ThreadPlaySound.endMusic){
	        	  wp = new PlaySound(new File("bin/tetraword/audio/test.wav"));
	        	  wp.open();
	        	  wp.play();
	        	  ThreadPlaySound.endMusic = false;
	          }
    	 }
	          
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
    	

     
