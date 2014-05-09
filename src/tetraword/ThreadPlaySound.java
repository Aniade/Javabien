package tetraword;

import java.io.IOException;


//Permet a la fonction play de s'exécuter en parallele sans bloquer le reste du programme

public class ThreadPlaySound extends Thread{
	
	public static boolean endMusic = false;
	
	public ThreadPlaySound(){
	}
 
	public void run(){
		PlaySound.line.start();
		 try {
	 			byte bytes[] = new byte[1024];
	 				int bytesRead=0;
	 				while (((bytesRead = PlaySound.audioStream.read(bytes, 0, bytes.length)) != -1)) {
	 					PlaySound.line.write(bytes, 0, bytesRead);
	 				}
	 			} catch (IOException io) {
	 				io.printStackTrace();
	 				return;
	 			}
		 endMusic = true;
	}
}
