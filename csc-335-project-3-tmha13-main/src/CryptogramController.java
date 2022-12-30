import java.util.Arrays;
import java.util.Random;

public class CryptogramController {
	private CryptogramModel model;
	private String answer;
	
	public CryptogramController (CryptogramModel model) {
		this.model = model;
		answer = this.model.getAnswer();
	}
	
	// Check if the game is over by comparing the true answer
	// and the current progress
	// Return true if they're similar, false otherwise
	public boolean isGameOver() {
		if(answer.equals(getUsersProgress())) return true;
		return false;
	}
	
	// Make replacement to the current progress
	// by calling setReplacement Function from Model
	// @letterToReplace is the encrypted character that will be replace
	// @replacementLetter is what @letterToReplace is replaced to
	// This function also checks if the parameters are valid inputs
	public void makeReplacement(char letterToReplace, char replacementLetter) {
		if(!CryptogramModel.isUpperChar(letterToReplace)
		|| !CryptogramModel.isUpperChar(replacementLetter))
			System.err.println("Please use uppercase alphabet letter only!");
		
		else this.model.setReplacement(letterToReplace, replacementLetter);
	}
	
	public void unguess(char letterToRemove) {
		this.model.unguess(letterToRemove);
	}
	
	// Return a fully encrypted String of answer
	public String getEncryptedQuote() {
		return this.model.getEncryptedString();
	}
	
	// Return the current progress of user's attempt to guess
	public String getUsersProgress() {
		return this.model.getDecryptedString();
	}
	
	// Reveal one correct mapping and update the progress
	// @return the correct mapping to the random value generated here
	public void oneHint() {
		Random rand = new Random();
		// Get a random char from fully encrypted string
		// Make sure it's not a special character
		// When randNum reaches the end of String, warp to index 0 instead
		int randNum = rand.nextInt(answer.length());
		char randomKey = this.getEncryptedQuote().charAt(randNum);
		while(!isUpperChar(randomKey)) {
			randNum = (randNum == answer.length()-1) ? 0 : randNum+1; 
			randomKey = this.getEncryptedQuote().charAt(randNum);
		}
					
		// Get the correct decryption to the above char
		char correctValue = this.model.getCorrectDecryption(randomKey);
		
		// If the hint is something the user have guessed
		// Take the next character from encrypted string and its decryption for hint
		// Repeat until we get something the user have not already guessed
		while (this.model.userDecryptionExist(randomKey, correctValue)) {
			do {
				randNum = (randNum == answer.length()-1) ? 0 : randNum+1;
				randomKey = this.getEncryptedQuote().charAt(randNum);
			} while(!isUpperChar(randomKey));
			correctValue = this.model.getCorrectDecryption(randomKey);
		} 
		
		// Add the correct hint as a user decryption attempt
		makeReplacement(randomKey, correctValue);
	}
	
	public int[] getFreq() {
		String encQuote = getEncryptedQuote();
		
		// Create an array to store frequency of each character
		int[] countArr = new int[26];
		Arrays.fill(countArr, 0);
		
		// Increment the array's approprite element by 1
		// everytime the corresponding character appers in encQuote
		for(int i = 0; i < encQuote.length(); i++) {
			char c = encQuote.charAt(i);
			
			if(!isUpperChar(c)) continue;
			countArr[c-65]++;
		}
		
		return countArr;
	}
	
	// Check if a character is a uppercase character and not a symbol
	// based on its ASCII value
	// return false if not an uppercase character, true otherwise
	public static boolean isUpperChar(char c) {
		if(c < 65 || c > 90) return false;
		return true;
	}
}
