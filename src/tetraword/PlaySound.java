package tetraword;

import java.net.URL;
import java.applet.Applet;
import java.applet.AudioClip;
 
public class PlaySound {
	private URL url;
	private AudioClip sound;
	    
	public PlaySound(String title) {
		url = PlaySound.class.getResource("/audio/"+title+".wav");
		sound = Applet.newAudioClip(url);
	}
	
	//Lecture de la musique
	public void play(){	
		sound.play();
	}
	
	//Lecture en boucle de la musique
	public void loop(){	
		sound.loop();
	}
	
	//Stopper la musique
	public void stop(){	
		sound.stop();
	}
		
	public static void main(String[] args) {
	    PlaySound p = new PlaySound("wrong");
	    p.play();
	    PlaySound p1 = new PlaySound("kungfu");
	    p1.play();
	    p1.stop();
	}
}