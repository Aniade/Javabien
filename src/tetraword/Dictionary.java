package tetraword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.TreeSet;



public class Dictionary {
	private String[] wordList;
	// Tableau d'entier qui stocke la position √† laquelle la premi√®re lettre du word change
	private int[] position = new int[27];
	// Collection pour pouvoir trier les words
	private TreeSet<String> tmpList = new TreeSet<String>();
	// Chemin du fichier dictionnaire.txt
	private String file;


	// Constructeur de la classe qui ajoute le fichier texte dictionnaire dans un TreeSet et dans un tableau 1D
	public Dictionary() {
    	Properties options = new Properties(); 
    	
    	// Cr√©ation d'une instance de File pour le fichier de config
    	File fichierConfig = new File("conf/conf.properties"); 

    	//Chargement du fichier de configuration
    	try {
    		options.load(new FileInputStream(fichierConfig));
    	} 
    	catch(IOException e) {
    		System.out.println("Echec chargement");
    	}
    	
    	/* R√©cup√©rer une propri√©t√© d'un fichier de configurations */
   	 	String configDico = options.getProperty("Dictionary");
   	 	
    	switch (configDico)
 	   	{
 	   	  case "francais":
 	   		file = "dictionnaire/dictionnaire.txt";
 	   	    break;
 	   	  case "anglais":
 	   		file = "dictionnaire/dictionary.txt";
 	   	    break; 
 	   	  default:
 	   		file = "dictionnaire/dictionnaire.txt";	
 	   	}
    	
		//Lecture du fichier
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String word;
			int temp=0;
 
	        //On ajoute les mots au TreeSet en enlevant les accents
	        while ((word = in.readLine()) != null) {    					
	        	tmpList.add(replaceString(word.toLowerCase())); 				
	        }
	       		
	       	in.close();
	       	
	       	//On fait un tableau qui a pour taille le nombre de mot du dictionnaire
			wordList = new String[tmpList.size()];  
	
			//On remplit le tableau avec les mots du dictionnaire
	        while(tmpList.isEmpty()==false) {
	    		word=(String)tmpList.first();
	    		wordList[temp]=word;
	    		tmpList.remove(word);
	    		temp++;
	    	}      
	       
	        findPosition();
		}
		
		//Si le fichier texte n'est pas trouv√©
	    catch (IOException e) {
	    	System.out.println("le fichier : "+ file +" n'a pas ete trouve");
	    }
	}

	//MÈthode qui enlËve les accents
	public String replaceString(String word){
		word=word.replace('‡','a');
		word=word.replace('‚','a');
		word=word.replace('‰','a');
		word=word.replace('È','e');
		word=word.replace('Ë','e');
		word=word.replace('Í','e');
		word=word.replace('Î','e');
		word=word.replace('Ó','i');
		word=word.replace('Ô','i');
		word=word.replace('Ú','o');
		word=word.replace('Ù','o');
		word=word.replace('ˆ','o');
		word=word.replace('˘','u');
		word=word.replace('˚','u');
		word=word.replace('¸','u');
		word=word.replace('Á','c');
		
		return(word);
	}

	// Accelere la recherche de mot dans le tableau en trouvant la position de d√©but de chaque lettre 
	public void findPosition(){
		char temp='a';
		int temp2=0;
		for(int i=0;i<wordList.length;i++) {		
			if(temp2<26) {
				if(wordList[i].charAt(0)==temp) {
			 		position[temp2]=i;
			 		//On incr√©mente pour passer a la lettre suivante
			 		temp++;
		 			temp2++; 	 			
			 	}
		 	}		 
		}
		position[26]=wordList.length;	
	}
	
	/*Methode qui valide le mot*/
	public boolean validateWord(String word){
		int first=0;
		int compteur=0;
		int end=0;
		char temp='a';
		boolean find=false;
		boolean completed=false;	
	
		while(completed==false) {
			//Trouve par quel lettre commence le mot
			if(word.charAt(0)==temp) {
				first=position[compteur];
				end=position[compteur+1];
				completed=true;
			}
			temp++;
			compteur++;
		}
		
		// On parcourt tous les mots commencant par la lettre du mot saisi
		while(find==false && first<end) {
			//System.out.println(wordList[first]);
			
			//Si le mot correspond √† un mot du dictionnaire on retourne true.
		  	if(wordList[first].equals(word)==true) {
		 		find=true;
		 		return(find);	
		 	}
		  	
		  	first++;
		}
		
		return find;
	}	
	
	/*Tri par ordre alphab√©tique*/
	public static char[] alphaTri(char tab[], int size) {
		boolean permut;
		char tmp;
		
		do{
			permut=false;
			for(int i=0; i<size - 1;i++) {
				if(tab[i]>tab[i+1]) {
					tmp = tab[i];
					tab[i] = tab[i + 1];
					tab[i + 1] = tmp;
					permut = true;
				}
			}
		} while (permut);
		
		return tab;
	}
	
	// Methode pour trouver le meilleur anagramme
	public String bestAnagram(char tab[], int size){
		//Liste avec les mots tri√©s par ordre alphab√©tique
		//String[] triList = wordList;
		//On trie le tableau de caract√®re par ordre alphab√©tique
		tab = alphaTri(tab, size);
		//On le convertit en string
		String saisie = new String(tab);
		//System.out.println(saisie); 
	
		boolean find = false;
		int cpt=0;
		
		while( cpt != position[26]){
			// Si le mot du dico fait la m√™me taille que la saisie
			if(wordList[cpt].length() == size) {
				//On convertir les string en tableau de caract√®re
				char tableau[] = wordList[cpt].toCharArray();
				//On trie les mots du dico par ordre alphab√©tique
				tableau = alphaTri(tableau, wordList[cpt].length());
				String word = new String(tableau);
				
				// Si le mot du dico contient exacetement les m√™mes lettres alors on a un anagramme \o/
				if(saisie.compareTo(word) == 0){
					//System.out.println("YOUHOUUUUUUUUU"); 
					//System.out.println(wordList[cpt]); 
					find = true;
				}
				/*
				triList[cpt] = word;*/
				//System.out.println(word); 		
			}
			cpt++;
		}
		
		/*Si on a pas trouv√© d'anagramme*/
		if(find == false){		
			int n = 1;
			while(find != true) {
				cpt = 0;
				while(cpt != position[26]){
					int j = 0;
					int i = 0;
					int valid = 0;
					/*Si on peut faire un mot avec n lettre en moin de la saisie*/
					if(wordList[cpt].length() == size - n){
						//System.out.println(wordList[cpt]);
						//On convertir les string en tableau de caract√®re
						char tableau[] = wordList[cpt].toCharArray();
						//On trie les mots du dico par ordre alphab√©tique
						tableau = alphaTri(tableau, wordList[cpt].length());
						String word = new String(tableau);
						while(i<word.length()+n) {
							if(tab[i] == tableau[j]) {
								j++;
								i++;
								valid++; 
								/*Si valide est √©gale au bon nombre de lettre alors on a trouv√© un des mots le plus long √† partir de la saisie*/
								if(valid == size-n) {
									//System.out.println("The word is :"+wordList[cpt]); 	
									find = true; 
									break;
								}
							} else{
								i++;	
							}		
						}					
						/*
						triList[cpt] = word;*/
						//System.out.println(word); 					
					}
					cpt++;
				}
				//Si on a toujours pas fait de mot on incr√©mente n, pour voir si on peut faire un mot en enlevant n lettre de la saisie 
				n++;
				//System.out.println(n); 
			}
		}

		/*D√©cider pour la valeur de retour*/	
		String s = new String(tab);
		return s;
	}
	
	public static void main(String[] args){	
		Dictionary dictionary = new Dictionary();
		//boolean valide;
		//String wordValide = "petasse";
		//dictionary.allAnagrams(wordValide);
		//valide = dictionary.validateWord(wordValide);
		//System.out.println(wordValide + " et le resultat est : "+valide); 
		String s="ahead";
		char tab[] = s.toCharArray(); 
		dictionary.bestAnagram(tab,s.length());
	}
}