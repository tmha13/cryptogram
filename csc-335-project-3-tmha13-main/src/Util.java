
public class Util {
	// @Param maxLine: the maximum number of character for each line
	// @Param quote: the quote that needs to be broken into lines
	// @Return an array of first index of each line
	public static int[] quoteToLines(int maxLine, String quote) {
		int[] lines = new int[(int) Math.ceil(quote.length() / maxLine) + 2];
		int count = 0;
		int i = 0;
		lines[count] = i;
		count++;

		while(i < quote.length()) {
			// Assign j as maxLineth character of the next line
			int j = i + maxLine;

			// Exit the loop if i and j is on the last line
			if(j >= quote.length()) {
				lines[count] = quote.length();
				break;
			}
			// Look for the closest whitespace
			// before the maxLineth character of encrypted
			while(quote.charAt(j)!= ' ')
				j--;

			// Now i will be the index of the start of new line
			i = j+1;

			// Store the new i to lines
			lines[count] = i;
			count++;
		}

		// Return the lines array
		return lines;
	}
	
	public static boolean isUpperChar(char c) {
		if(c < 65 || c > 90) return false;
		return true;
	}
}
