package views;

import AdventureModel.AdventureGame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * LoadView Class
 *
 * loads serialized adventure games.
 */
public class LoadView {

    private AdventureGameView adventureGameView;
    private Label selectGameLabel;
    private Button selectGameButton;
    private Button closeWindowButton;

    private ListView<String> GameList;
    private String filename = null;

    /**
     * LoadView Constructor
     *
     * @param adventureGameView the adventure game view
     */
    public LoadView(AdventureGameView adventureGameView){
        this.adventureGameView = adventureGameView;
        selectGameLabel = new Label(String.format(""));

        GameList = new ListView<>(); //to hold all the file names

        final Stage dialog = new Stage(); //dialogue box
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(adventureGameView.stage);

        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));
        dialogVbox.setStyle("-fx-background-color: #FFFACD;");
        selectGameLabel.setId("CurrentGame"); // DO NOT MODIFY ID
        GameList.setId("GameList");  // DO NOT MODIFY ID
        GameList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        getFiles(GameList); //get files for file selector
        selectGameButton = new Button("Change Game");
        selectGameButton.setId("ChangeGame"); // DO NOT MODIFY ID
        AdventureGameView.makeButtonAccessible(selectGameButton, "select game", "This is the button to select a game", "Use this button to indicate a game file you would like to load.");

        closeWindowButton = new Button("Close Window");
        closeWindowButton.setId("closeWindowButton"); // DO NOT MODIFY ID
        closeWindowButton.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");
        closeWindowButton.setPrefSize(200, 50);
        closeWindowButton.setFont(new Font(16));
        closeWindowButton.setOnAction(e -> dialog.close());
        AdventureGameView.makeButtonAccessible(closeWindowButton, "close window", "This is a button to close the load game window", "Use this button to close the load game window.");

        //on selection, do something
        selectGameButton.setOnAction(e -> {
            try {
                selectGame(selectGameLabel, GameList);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox selectGameBox = new VBox(10, selectGameLabel, GameList, selectGameButton);

        // Default styles which can be modified
        GameList.setPrefHeight(100);
        selectGameLabel.setStyle("-fx-text-fill: #000000");
        selectGameLabel.setFont(new Font(16));
        selectGameButton.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");
        selectGameButton.setPrefSize(200, 50);
        selectGameButton.setFont(new Font(16));
        selectGameBox.setAlignment(Pos.CENTER);
        dialogVbox.getChildren().add(selectGameBox);
        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Get Files to display in the on screen ListView
     *
     * @param listView the ListView containing all the .ser files in the Games/Saved directory.
     */
    private void getFiles(ListView<String> listView) {
        // get all files in directory
        File dir = new File("Games"+File.separator+"Saved"+File.separator);
        ArrayList<String> files = new ArrayList<>(Arrays.asList(dir.list()));

        // only add files that end with ser
        for (String fileName: files) {
            if (fileName.endsWith(".ser"))
                listView.getItems().add(fileName);
        }
    }

    /**
     * selectGame tries to load a game, if it exists.
     *
     * @param selectGameLabel the label to use to print errors and or successes to the user.
     * @param GameList the ListView to populate
     */
    private void selectGame(Label selectGameLabel, ListView<String> GameList) throws IOException {
        //saved games will be in the Games/Saved folder!
        String fileName = GameList.getSelectionModel().getSelectedItem();

        // try to load game
        try {
            this.adventureGameView.model = loadGame("Games"+File.separator+"Saved"+File.separator+fileName);
            selectGameLabel.setText(fileName);
        }
        // start new game
        catch (Exception e) {
            this.adventureGameView.model  = new AdventureGame("TinyGame");
            selectGameLabel.setText("TinyGame");
        }
        finally {
            this.adventureGameView.stopArticulation();
        }

        // update screen
        this.adventureGameView.updateScene("");
        this.adventureGameView.updateItems();
    }

    /**
     * Load the Game from a file
     *
     * @param GameFile file to load
     * @return loaded AdventureGame Model
     * @throws IOException in the case of a file I/O error
     * @throws ClassNotFoundException in the case that the class is not found
     */
    public AdventureGame loadGame(String GameFile) throws IOException, ClassNotFoundException {
        // Reading the object from a file
        FileInputStream file = null;
        ObjectInputStream in = null;
        try {
            file = new FileInputStream(GameFile);
            in = new ObjectInputStream(file);
            return (AdventureGame) in.readObject();
        } finally {
            if (in != null) {
                in.close();
                file.close();
            }
        }
    }

}
