import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Random;

public class CryptogramModel extends Observable {
	//Private Variable
	private String answer;    // The correct answer read from quotes.txt
	private String encrypted; // The encrypted version of answer
	private StringBuilder decrypted; // The current progress of decrypting
	private HashMap<Character, Character> keyMap;  // Map of initial encrypt
	private HashMap<Character, Character> userMap; // Map of user attempt
	public static final Character[] alphabet = {'A','B','C','D','E','F','G','H','I','J','K',
			'L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	

	// Empty Constructor for CryptogramModel
	public CryptogramModel() {
		// Generate the answer from a random line of given file
		answer = generateAnswer();
		
		// Initialize userMap
		userMap = new HashMap<Character, Character>();
		
		// Generate keyMap
		// Key:   Alphabet characters in order
		// Value: Alphabet Character in random order
		keyMap = new HashMap<Character, Character>();
		generateMap(keyMap);

		// Create a fully encrypted string based on answer
		encrypted = "";
		for (int i = 0; i < answer.length(); i++) {
			char c = answer.charAt(i);
			
			if(!isUpperChar(c)) 
				encrypted += c;
			else encrypted += keyMap.get(c);
		}

		// Create a template for current decrypted string
		// Replace meaningful character with blank space
		// Punctuation and special symbol remains intact
		decrypted = new StringBuilder("");
		for (int i=0; i < answer.length();i++) {
			char c = answer.charAt(i);
			
			if(!isUpperChar(c)) decrypted.append(c);
			else decrypted.append(" ");
		}
	}

	// Add to the hashmap of user for current decryption attempt
	// and update the current progress appropriately
	// @encryptedChar is the character that will be replaced in encrypted
	// @replacementChar is the character that is used to replace 
	public void setReplacement(char encryptedChar, char replacementChar) {
		// Use arrayList to store the changing index that view can use
		// First element of arrayList stores the character needed to replace
		ArrayList<Integer> changes = new ArrayList<>();
		changes.add((int) replacementChar);
		userMap.put(encryptedChar, replacementChar);
		
		for(int i = 0; i < answer.length(); i++) {
			if(encrypted.charAt(i) == encryptedChar) {
				decrypted.setCharAt(i, replacementChar);
				changes.add(i);
			}
		}
		
		if(!changes.isEmpty()) {
			setChanged();
			notifyObservers(changes);
		}
	}
	
	// Replace every index with the same character with the one being backspaced on with a blank
	// This will undo a guess progress
	// @encryptedChar is the character that will be replaced in encrypted
	public void unguess(char encryptedChar) {
		
		
		ArrayList<Integer> changes = new ArrayList<>();
		changes.add(-1);
		
		for(int i = 0; i < answer.length(); i++) {
			if(decrypted.charAt(i) == encryptedChar) {
				decrypted.setCharAt(i, ' ');
				changes.add(i);
			}
		}
		
		if(!changes.isEmpty()) {
			setChanged();
			notifyObservers(changes);
		}
	}
	
	// Return the fully encrypted version of answer
	public String getEncryptedString() {
		return encrypted;
	}

	// Return the current progress in decrypting
	public String getDecryptedString() {
		return decrypted.toString();
	}
	
	// Return the correct answer
	public String getAnswer() {
		return answer;
	}
	
	// Get the initial character before it is encrypted
	// @a is the encrypted version that needs to be decrypted
	// Return the decrypted version of @a based on KeyMap
	// Return ' ' if the character can't be found in KeyMap (non-uppercase-char)
	public char getCorrectDecryption(char a) {
		for(int i = 0; i < alphabet.length; i++) {
			if(keyMap.get(alphabet[i]) == a) return alphabet[i];
		}
		// Should not happen
		return ' ';
	}
	
	// Check if the correct decryption is already attempted by the user
	// @encryptkey is the character after being encrypted based on keyMap
	// @correctValue is the supposed value of @encryptkey pre-encrypted.
	// Return true if it is already attemped, false if not
	public boolean userDecryptionExist(char encryptKey, char correctValue) {
		if(userMap.containsKey(encryptKey) && (userMap.get(encryptKey) == correctValue)) 
			return true;
		return false;
	}
	
	////////////////////////////////////////
	////////HELPER FUNCTIONS////////////////
	////////////////////////////////////////
	
	// This function generates the initial answer
	// Answer is read randomly from a line of quotes.txt
	// Return the String version of what was read
	private static String generateAnswer() {
		// Read a random line and set answer
		Random rand = new Random();
		int randomline = rand.nextInt(5); // The file has 5 lines
		StringBuffer answer = new StringBuffer();

		try(BufferedReader br = new BufferedReader(new FileReader("quotes.txt"))) {
			int i = 0;

			// Read line by line, and assign the correct line to answer
			// Correct line is determined by randomline, which is randomly generated
			for(String line; (line = br.readLine()) != null; ) {
				if(i == randomline) 
					answer.append(line.toUpperCase());
				i++;
			}
		} catch (IOException e) {
			System.out.println("Error reading file");
			return "";
		};
		
		return answer.toString();
	}
	
	// Generate the map of encryption based on a shuffles of alphabet array
	// @keyMap is the Hash Map that will store the encryption
	// @keyMap key: a character from alphabet
	// @keyMap value: a character from alphabet after being decrypted
	private static void generateMap(HashMap<Character, Character> keyMap) {
		// Create a copy of alphabet array to shuffle
		Character[] shuffle = Arrays.copyOf(alphabet, alphabet.length);
		for (int i = 0; i < alphabet.length; i++) 
			shuffle[i] = alphabet[i];
		List<Character> shufList = Arrays.asList(shuffle);
		Collections.shuffle(shufList);
		
		// Make sure no element is mapped to itself
		while(hasUnchangedElement(shufList))
			Collections.shuffle(shufList);
		
		// Complete KeyMap with 2 array pre and post shuffling
		for(int i=0; i < alphabet.length; i++) {
			keyMap.put(alphabet[i], shufList.get(i));
		}
	}
	
	// Check if a list have any element that is in similar index
	// to an alphabet array
	// @list1 is the list that will be checked
	// Return true if there's similar element in similar index, false otherwise
	private static boolean hasUnchangedElement(List<Character> list1) {
		for(int i = 0; i < list1.size(); i++)
			if(list1.get(i) == alphabet[i]) return true;
		return false;
	}
	
	// Check if a character is a uppercase character and not a symbol
	// based on its ASCII value
	// return false if not an uppercase character, true otherwise
	public static boolean isUpperChar(char c) {
		if(c < 65 || c > 90) return false;
		return true;
	}
		
}
