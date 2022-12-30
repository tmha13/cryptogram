import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javafx.application.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CryptogramGUIView extends Application implements Observer {
		private CryptogramController ctr;
		private CryptogramModel model;
		private ArrayList<TextField> tfs;
		private Label footNote = new Label();
	
		@Override
		public void start(Stage stage) {
			stage.setTitle("Cryptogram");
			BorderPane window = new BorderPane();
			GridPane gameBoard = new GridPane();
			GridPane buttonBoard = new GridPane();
			GridPane freqBoard = new GridPane();
			
			model = new CryptogramModel();
			ctr = new CryptogramController(model);
			tfs = new ArrayList<>();
			model.addObserver(this);
			String encrypted = ctr.getEncryptedQuote();
			
			// BUTTON CODE
			Button np = new Button("New Puzzle");
			np.setOnMouseClicked(e -> this.start(stage));
			Button hint = new Button("Hint");
			hint.setOnMouseClicked(e -> ctr.oneHint());
			CheckBox cb = new CheckBox("Show Hints");
			cb.setOnMouseClicked(e -> {
				if(cb.isSelected()) 
					freqBoard.setVisible(true);
				else freqBoard.setVisible(false);
			});
			buttonBoard.add(np, 0, 0);
			buttonBoard.add(hint, 0, 1);
			buttonBoard.add(cb, 0, 2);
			
			// FREQUENCY CODE
			int[] freq = ctr.getFreq();
			int alphabet = 65;
			for(int i = 0; i < freq.length / 2; i++) {
				char left = (char) alphabet;
				char right = (char) (alphabet + 13);
				Label lb1 = new Label(left + " ");
				Label lb2 = new Label(freq[i] + "     ");
				Label lb3 = new Label(right + " ");
				Label lb4 = new Label(freq[i+13] + " ");
				freqBoard.add(lb1, 0, i);
				freqBoard.add(lb2, 1, i);
				freqBoard.add(lb3, 2, i);
				freqBoard.add(lb4, 3, i);
				alphabet++;
			}
			freqBoard.setVisible(false);
			buttonBoard.add(freqBoard, 0, 3);
			
			
			// VBOX CODE 
			int[] lines = Util.quoteToLines(30, encrypted);
			int lineCount = 1;
			int col = 0;
			int row = 0;
			for(int i = 0; i < encrypted.length(); i++) {
				char current = encrypted.charAt(i);
				
				// VBox for progress
				VBox vb1 = new VBox();
				TextField tf = new TextField();
				tf.setPrefColumnCount(1);
				if(!Util.isUpperChar(current)) {
					tf.setText(Character.toString(current));
					tf.setDisable(true);
				}
				
				// Add event handler to each textfield
				tf.setOnKeyPressed(e -> {
					String keyPressed = e.getText();
					
					if(e.getCode().equals(KeyCode.BACK_SPACE) && tf.getText().length() == 1) {
						ctr.unguess(tf.getText().charAt(0));
						tf.clear();
						tf.setEditable(true);
					}
					
					// Keypressed on "full" textfield, or keypressed isnt a guessing attempt. consume it
					else if(tf.getText().length() >= 1
					|| keyPressed.length() == 0) {
						e.consume();
						footNote.setText("Event consumed");
					}
					
					// Attempt to make a normal guess, replace all similar character with guess attempt
					else {
						char temp = Character.toUpperCase(keyPressed.charAt(0));
						tf.setText(Character.toString(temp));
						tf.setEditable(false);
						ctr.makeReplacement(current,
								Character.toUpperCase(keyPressed.charAt(0)));
						footNote.setText("Making replacement");
					}
				});
				tfs.add(tf);
				vb1.getChildren().add(tf);
				
				// VBox for encrypted
				VBox vb2 = new VBox();
				Label lb = new Label(Character.toString(current));
				vb2.getChildren().add(lb);
				vb2.setAlignment(Pos.CENTER);
				
				// Add both of them to gameboard
				gameBoard.add(vb1, col, row);
				gameBoard.add(vb2, col, row+1);
				col++;
				
				// Increment and adjustment position
				if(col == (lines[lineCount]-lines[lineCount-1])) {
					col = 0;
					row += 2;
					lineCount++;
				}
			}//VBOX CODE END
			
			window.setCenter(gameBoard);
			window.setBottom(footNote);
			window.setRight(buttonBoard);
			
			Scene scene = new Scene(window, 900, 400);
			stage.setScene(scene);
			stage.show();
		}

		@Override
		public void update(Observable o, Object arg) {
			ArrayList<Integer> changes = (ArrayList<Integer>) arg;
			
			// Get the first value out, since this is the character to replace
			int intRep = changes.remove(0);
			
			// Handle Unguess Command
			if(intRep == -1) {
				for(int i: changes) {
					tfs.get(i).clear();
					tfs.get(i).setEditable(true);
				}
				
				return;
			}
			
			
			char rep = (char) intRep;
			
			// Loop through the rest and update each index position in changes
			for(int i : changes) {
				tfs.get(i).setEditable(false);
				tfs.get(i).setText(Character.toString(rep));
			}
			
			// Check if the game is over and alert if it is
			if(ctr.isGameOver()) {
				Alert a = new Alert(AlertType.INFORMATION);
				a.setContentText("You won");
				a.showAndWait();
			}
		}
}
