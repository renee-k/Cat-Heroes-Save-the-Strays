package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;

/**
 * SaveView Class
 *
 * saves serialized adventure games.
 */
public class SaveView {

    static String saveFileSuccess = "Saved Adventure Game!!";
    static String saveFileExistsError = "Error: File already exists";
    static String saveFileNotSerError = "Error: File must end with .ser";
    private Label saveFileErrorLabel = new Label("");
    private Label saveGameLabel = new Label(String.format("Enter name of file to save"));
    private TextField saveFileNameTextField = new TextField("");
    private Button saveGameButton = new Button("Save Game");
    private Button closeWindowButton = new Button("Close Window");

    private AdventureGameView adventureGameView;

    /**
     * SaveView Constructor
     *
     * @param adventureGameView the adventure game view
     */
    public SaveView(AdventureGameView adventureGameView) {
        this.adventureGameView = adventureGameView;
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(adventureGameView.stage);
        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));
        dialogVbox.setStyle("-fx-background-color: #FFFACD;");
        saveGameLabel.setId("SaveGame"); // DO NOT MODIFY ID
        saveFileErrorLabel.setId("SaveFileErrorLabel");
        saveFileNameTextField.setId("SaveFileNameTextField");
        saveGameLabel.setStyle("-fx-text-fill: #000000;");
        saveGameLabel.setFont(new Font(16));
        saveFileErrorLabel.setStyle("-fx-text-fill: #000000;");
        saveFileErrorLabel.setFont(new Font(16));
        saveFileNameTextField.setStyle("-fx-text-fill: #000000;");
        saveFileNameTextField.setFont(new Font(16));

        String gameName = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".ser";
        saveFileNameTextField.setText(gameName);

        saveGameButton = new Button("Save board");
        saveGameButton.setId("SaveBoardButton"); // DO NOT MODIFY ID
        saveGameButton.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");
        saveGameButton.setPrefSize(200, 50);
        saveGameButton.setFont(new Font(16));
        AdventureGameView.makeButtonAccessible(saveGameButton, "save game", "This is a button to save the game", "Use this button to save the current game.");
        saveGameButton.setOnAction(e -> saveGame());

        closeWindowButton = new Button("Close Window");
        closeWindowButton.setId("closeWindowButton"); // DO NOT MODIFY ID
        closeWindowButton.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");
        closeWindowButton.setPrefSize(200, 50);
        closeWindowButton.setFont(new Font(16));
        closeWindowButton.setOnAction(e -> dialog.close());
        AdventureGameView.makeButtonAccessible(closeWindowButton, "close window", "This is a button to close the save game window", "Use this button to close the save game window.");

        VBox saveGameBox = new VBox(10, saveGameLabel, saveFileNameTextField, saveGameButton, saveFileErrorLabel, closeWindowButton);
        saveGameBox.setAlignment(Pos.CENTER);

        dialogVbox.getChildren().add(saveGameBox);
        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Saves the game
     */
    private void saveGame() {
        // check if ends with .ser
        if (!saveFileNameTextField.getText().endsWith(".ser")) {
            saveFileErrorLabel.setText(saveFileNotSerError);
            return;
        }

        // pathname
        File file = new File("Games"+File.separator+"Saved"+File.separator+saveFileNameTextField.getText());
        boolean created = false;

        // check if file already exists
        try {
            created = file.createNewFile();
        }
        catch (Exception e) {

        }

        if (!created) {
            saveFileErrorLabel.setText(saveFileExistsError);
            return;
        }

        // save game
        adventureGameView.model.saveModel(file);
        saveFileErrorLabel.setText(saveFileSuccess);
    }
}

