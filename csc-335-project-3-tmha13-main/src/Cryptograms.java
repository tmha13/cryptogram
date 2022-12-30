import java.util.Scanner;
import javafx.application.*;

// Author: Trinh Ha
// Csc335
// Project 4

public class Cryptograms {
	public static void main(String[] args) {
//		if(args[0].equals("-text")) {
//			CryptogramTextView.textview();
//		}
//		else {
			Application.launch(CryptogramGUIView.class, args);
			CryptogramModel observable = new CryptogramModel();
			CryptogramGUIView observer = new CryptogramGUIView();
			observable.addObserver(observer);
//		}
	}
}

