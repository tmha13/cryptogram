import java.util.Scanner;

public class CryptogramTextView {
	public static void textview() {
		CryptogramModel model = new CryptogramModel();
		CryptogramController ctr = new CryptogramController(model);
		Scanner reader = new Scanner(System.in);
		String command;

		while(!ctr.isGameOver()) {
			// Get user input
			displayFormatted(ctr);
			System.out.println("Enter a command (Type help to see command)");
			command = reader.nextLine();
			// Check what kind of input they are and take appropriate action
			menuControl(ctr, command);
		}

		System.out.println("Congrats! You solved it!");
		System.out.println(ctr.getUsersProgress());
		reader.close();
	}
	
	// Contain all the menu option to request
	public static void menuControl(CryptogramController ctr, String command) {
		if(command.startsWith("replace")
				&& command.startsWith("by", 10)
				&& command.length() == 14) {
			// Case they want to attempt guessing
			char toReplace = command.charAt(8);
			char replacement = command.charAt(13);
			ctr.makeReplacement(toReplace, replacement);
		}
		else if(command.startsWith("=", 2)
				&& command.length() == 5) {
			// Case they want to attempt guessing
			// alternative format
			char toReplace = command.charAt(0);
			char replacement = command.charAt(4);
			ctr.makeReplacement(toReplace, replacement);
		}
		// show frequency
		else if(command.equals("freq")) showFreq(ctr.getFreq());
		// add 1 correct mapping
		else if(command.equals("hint")) ctr.oneHint();
		// quit game early
		else if(command.equals("exit")) {
			System.exit(0);
			return;
		}
		// display every commands possible
		else if(command.equals("help")) sendHelp();
		// Any other case, it is assumed as invalid format
		else System.err.println("Invalid command. Please try again!");
	}

	// Function to display any commands possible in our program
	public static void sendHelp() {
		System.out.println("LIST OF COMMANDS:");
		System.out.println("replace X by Y – replace letter "
				+ "X by letter Y in our attempted solution");
		System.out.println("X = Y – a shortcut for this same command");
		System.out.println("freq – Display the letter frequencies in the "
				+ "encrypted quotation (i.e., how many of letter X appear)");
		System.out.println("hint – display one correct mapping "
				+ "that has not yet been guessed");
		System.out.println("exit – Ends the game early");
		System.out.println("help – List these commands");
	}

	// Display the frequency using countArray from controller
	private static void showFreq(int[] countArr) {
		// Print out a formatted String of frequency of all letters
		int lineCheck = 0;
		for(int i = 0; i < countArr.length; i++) {
			System.out.printf("%c:%2d  ", (char)(i+65), countArr[i]);
			lineCheck++;

			if(lineCheck == 7) {
				System.out.println();
				lineCheck = 0;
			}
		}
		System.out.println();
	}
	
	// Display a quote under multiple lines format
	private static void displayFormatted(CryptogramController ctr) {
		int[] lines = Util.quoteToLines(80, ctr.getEncryptedQuote());
		String userProgress = ctr.getUsersProgress();
		String encrypted = ctr.getEncryptedQuote();

		for(int i = 0; (i + 1) < lines.length; i++) {
			String userLine = userProgress.substring(lines[i], lines[i+1]);
			System.out.println(userLine);
			String encLine = encrypted.substring(lines[i], lines[i+1]);
			System.out.println(encLine);
			System.out.println("");
		}
	}
}
