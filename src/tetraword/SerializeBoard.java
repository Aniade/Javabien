package tetraword;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeBoard {

	public static void main(String[] args) {
	    //Nous déclarons nos objets en dehors du bloc try/catch
	    ObjectInputStream ois;
	    ObjectOutputStream oos;
	    try {
	      oos = new ObjectOutputStream(
	              new BufferedOutputStream(
	                new FileOutputStream(
	                  new File("conf/game.txt"))));
	            
	      //Nous allons écrire chaque objet Game dans le fichier
	      oos.writeObject(new Tetraword());
	      //Ne pas oublier de fermer le flux !
	      oos.close();
	            
	      //On récupère maintenant les données !
	      ois = new ObjectInputStream(
	              new BufferedInputStream(
	                new FileInputStream(
	                  new File("conf/game.txt"))));
	            
	      
	    
	      ois.close();
	            
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }       
	  }
	}

