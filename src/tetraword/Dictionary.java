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
	//Tableau d'entier qui stocke la position à laquelle la première lettre du word change
	private int[] position = new int[27];
	/*Collection pour pouvoir trier les words*/
	private TreeSet<String> tmpList = new TreeSet<String>();
	/*Chemin du fichier dictionnaire.txt*/
	private String file;
	
	
	/* Constructeur de la class qui ajoute le fichier texte dictionnaire dans un TreeSet
	 *  et dans un tableau 1D*/
	
	public Dictionary() {
    	Properties options = new Properties(); 
    	
    	// Création d'une instance de File pour le fichier de config
    	File fichierConfig = new File("conf/conf.properties"); 

    	//Chargement du fichier de configuration
    	try {
    	options.load(new FileInputStream(fichierConfig));
    	} 
    	catch(IOException e) {
    		System.out.println("Echec chargement");
    	}
    	
    	/* Récupérer une propriété d'un fichier de configurations */
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
        while ((word = in.readLine()) != null) 
        	{    					
     				tmpList.add(replaceString(word.toLowerCase())); 				
       		}
       		
       		
       	in.close();
       	
       	//On fait un tableau qui a pour taille le nombre de mot du dictionnaire
		wordList = new String[tmpList.size()];  
		
		//On remplit le tableau avec les mots du dictionnaire
        while(tmpList.isEmpty()==false)
        	{
        		word=(String)tmpList.first();
        		wordList[temp]=word;
        		tmpList.remove(word);
        		temp++;
        	}      
       
        findPosition();
        
		} 
	//Si le fichier texte n'est pas trouvé
    catch (IOException e) {
    	System.out.println("le fichier : "+ file +" n'a pas ete trouve");
    }

}

//Méthode qui enlève les accents
public String replaceString(String word){
		word=word.replace('à','a');
		word=word.replace('â','a');
		word=word.replace('ä','a');
		word=word.replace('é','e');
		word=word.replace('è','e');
		word=word.replace('ê','e');
		word=word.replace('ë','e');
		word=word.replace('î','i');
		word=word.replace('ï','i');
		word=word.replace('ò','o');
		word=word.replace('ô','o');
		word=word.replace('ö','o');
		word=word.replace('ù','u');
		word=word.replace('û','u');
		word=word.replace('ü','u');
		word=word.replace('ç','c');
		return(word);	
	
	}

/* Accelere la recherche de mot dans le tableau en trouvant la position de début de chaque lettre 
*/
public void findPosition(){
	char temp='a';
	int temp2=0;
	for(int i=0;i<wordList.length;i++)
		{		
		 if(temp2<26)
		 	{
		 	if(wordList[i].charAt(0)==temp)
		 		{
		 		position[temp2]=i;
		 		//On incrémente pour passer a la lettre suivante
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
	
	while(completed==false)
		{
		//Trouve par quel lettre commence le mot
		if(word.charAt(0)==temp)
			{
				first=position[compteur];
				end=position[compteur+1];
				completed=true;
			}
			temp++;
			compteur++;
		}
	/*On parcourt tout les mots commencant par la lettre du mot saisie*/
	while(find==false && first<end)
		{	//System.out.println(wordList[first]);
			//Si le mot correspond à un mot du dictionnaire on retourne true.
		  	if(wordList[first].equals(word)==true)
		 	{
		 		find=true;
		 		return(find);	
		 	}
	 		first++;
		}
	return find;
	}	



/*Tri par ordre alphabétique*/
public static char[] alphaTri(char tab[], int size){
	boolean permut;
	char tmp;
		do{
			permut=false;
		for(int i=0; i<size - 1;i++){
			if(tab[i]>tab[i+1]){
				tmp = tab[i];
				tab[i] = tab[i + 1];
				tab[i + 1] = tmp;
				permut = true;
			}
		}
	}while (permut);
	return tab;
}

/*Methode pour trouver le meilleur anagramme*/
public String bestAnagram(char tab[], int size){
	String s = "";
	//Liste avec les mots triés par ordre alphabétique
	//String[] triList = wordList;
	//On trie le tableau de caractère par ordre alphabétique
	tab = alphaTri(tab, size);
	//On le convertit en string
	String saisie = new String(tab);
	//System.out.println(saisie); 
	
	boolean find = false;
	int cpt=0;
		while( cpt != position[26]){
			/*Si le mot du dico fait la même taille que la saisie*/
			if(wordList[cpt].length() == size){
				//On convertir les string en tableau de caractère
				char tableau[] = wordList[cpt].toCharArray();
				//On trie les mots du dico par ordre alphabétique
				tableau = alphaTri(tableau, wordList[cpt].length());
				String word = new String(tableau);
					/*Si le mot du dico contient exacetement les mêmes lettres alors on a un anagramme \o/*/
					if(saisie.compareTo(word) == 0){
						find = true;
						return wordList[cpt];
					}
				/*
				triList[cpt] = word;*/
				//System.out.println(word); 		
			}
			cpt++;
		}
		/*Si on a pas trouvé d'anagramme*/
		if(find == false){		
			int n = 1;
			while(find != true){
				cpt = 0;
				while(cpt != position[26]){
					int j = 0;
					int i = 0;
					int valid = 0;
					/*Si on peut faire un mot avec n lettre en moin de la saisie*/
					if(wordList[cpt].length() == size - n){
						//System.out.println(wordList[cpt]);
						//On convertir les string en tableau de caractère
						char tableau[] = wordList[cpt].toCharArray();
						//On trie les mots du dico par ordre alphabétique
						tableau = alphaTri(tableau, wordList[cpt].length());
						String word = new String(tableau);
						while(i<word.length()+n){
							if(tab[i] == tableau[j]){
								j++;
								i++;
								valid++; 
								/*Si valide est égale au bon nombre de lettre alors on a trouvé un des mots le plus long à partir de la saisie*/
								if(valid == size-n){
									//System.out.println("The word is :"+wordList[cpt]); 	
									find = true; 
									return wordList[cpt];
									
								}
							} 							
							else{
								i++;	
							}		
						}						
						/*
						triList[cpt] = word;*/
						//System.out.println(word); 					
					}
					cpt++;
				}
				//Si on a toujours pas fait de mot on incrémente n, pour voir si on peut faire un mot en enlevant n lettre de la saisie 
				n++;
			}
		}

	return s;
}



public static void main(String[] args){	
	Dictionary dictionary = new Dictionary();
	boolean valide;
	String wordValide = "PETASSE";
	wordValide = wordValide.toLowerCase();
	//dictionary.allAnagrams(wordValide);
	valide = dictionary.validateWord(wordValide);
	System.out.println(wordValide + " et le resultat est : "+valide); 
	String s="aaeirstvxz";
	char tab[] = s.toCharArray(); 
	String st = dictionary.bestAnagram(tab,s.length());
	System.out.println(st); 

	
}

}
