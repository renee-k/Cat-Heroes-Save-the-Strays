package views;

import AdventureModel.AdventureGame;
import AdventureModel.AdventureObject;
import AdventureModel.Player;
import AdventureModel.Room;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.scene.input.KeyEvent; //you will need these!
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.event.EventHandler; //you will need this too!
import javafx.scene.AccessibleRole;
import AdventureModel.Settings;

import java.io.File;
import java.util.ArrayList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import powerups.GUISource;
import powerups.Level2Decorator;
import powerups.Level3Decorator;
import powerups.Level4Decorator;

/**
 * AdventureGameView Class
 *
 * visualizes the adventure game model.
 */
public class AdventureGameView implements GUISource {

    AdventureGame model; //model of the game
    Stage stage; //stage on which all is rendered
    Button saveButton, loadButton, helpButton, settingsButton; //buttons
    Boolean helpToggle = false; //is help on display?

    GridPane gridPane = new GridPane(); //to hold images and buttons
    Label roomDescLabel = new Label(); //to hold room description and/or instructions
    VBox objectsInRoom = new VBox(); //to hold room items
    VBox objectsInInventory = new VBox(); //to hold inventory items
    ImageView roomImageView; //to hold room image
    TextField inputTextField; //for user input

    private MediaPlayer mediaPlayer; //to play audio
    private boolean mediaPlaying; //to know if the audio is playing
    private Level3Decorator level3Powerup = new Level3Decorator(this);
    private Label level3Progress = new Label();

    /**
     * AdventureGameView Constructor
     *
     * initializes attributes.
     *
     * @param model the model of the adventure
     * @param stage the stage of the adventure
     */
    public AdventureGameView(AdventureGame model, Stage stage) {
        this.model = model;
        this.stage = stage;
        Settings.getSettings();
        initUI();
    }

    /**
     * Show the start screen UI.
     */
    private void showStartingUI() {
        // Clear existing content
        gridPane.getChildren().clear();
        stopArticulation();

        // Load image
        ImageView imageView = new ImageView(new Image("file:images/CatIntroScreenImage.jpg"));
        imageView.setFitWidth(640);
        imageView.setFitHeight(640);
        Pane imagePane = new Pane(imageView);
        imagePane.setPrefSize(512, 512);
        imagePane.setMaxSize(512, 512);

        Button playButton = new Button("Play");
        playButton.setId("Play");
        customizeButton(playButton, 200, 50);
        makeButtonAccessible(playButton, "Play Button", "This button starts the game.", "This button starts the game. Click it to begin your adventure.");
        playButton.setOnAction(event -> setupGameUI());

        Button helpButton = new Button("Help");
        helpButton.setId("Help");
        customizeButton(helpButton, 200, 50);
        makeButtonAccessible(helpButton, "Help Button", "This button displays help information.", "This button displays help information. Click it to get assistance.");
        helpButton.setOnAction(event -> showHelpInfo());

        VBox buttonsVBox = new VBox(20); // Spacing between buttons
        buttonsVBox.getChildren().addAll(playButton, helpButton);
        buttonsVBox.setAlignment(Pos.CENTER); // Center alignment for VBox

        VBox mainVBox = new VBox(20); // Spacing between image and buttons
        mainVBox.getChildren().addAll(imagePane, buttonsVBox);
        mainVBox.setAlignment(Pos.CENTER); // Center alignment for VBox

        // Add mainVBox containing image and buttons to center of the gridPane
        gridPane.setPadding(new Insets(20));
        gridPane.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#000000"),
                new CornerRadii(0),
                new Insets(0)
        )));

        // Create a column constraints for centering
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS); // Allow column to grow
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), columnConstraints, new ColumnConstraints());

        // Add mainVBox to the middle column, centered horizontally
        GridPane.setHalignment(mainVBox, HPos.CENTER); // Center horizontally in the grid
        gridPane.add(mainVBox, 1, 1); // Add centered VBox containing image and buttons to gridPane

        renderGridPane(); // Render the gridPane after adding content
    }

    /**
     * Render the gridPane.
     */
    private void renderGridPane() {
        // Render everything
        var scene = new Scene( gridPane ,  1000, 800);
        scene.setFill(Color.HOTPINK);
        this.stage.setScene(scene);
        this.stage.setResizable(false);
        this.stage.show();
    }

    /**
     * Set up the game UI.
     */
    private void setupGameUI() {
        // Clear the existing content
        gridPane.getChildren().clear();
        stopArticulation();

        // Set up the game UI components (buttons, text fields, etc.)
        // setting up the stage
        this.stage.setTitle("Cat Heroes: Save the Strays");

        //Inventory + Room items
        objectsInInventory.setSpacing(10);
        objectsInInventory.setAlignment(Pos.TOP_CENTER);
        objectsInRoom.setSpacing(10);
        objectsInRoom.setAlignment(Pos.TOP_CENTER);

        // setting up GridPane
        gridPane.setPadding(new Insets(20));
        gridPane.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#FF69B4"),
                new CornerRadii(0),
                new Insets(0)
        )));

        // Three columns, three rows for the GridPane
        ColumnConstraints column1 = new ColumnConstraints(150);
        ColumnConstraints column2 = new ColumnConstraints(650);
        ColumnConstraints column3 = new ColumnConstraints(150);
        column3.setHgrow( Priority.SOMETIMES ); // let some columns grow to take any extra space
        column1.setHgrow( Priority.SOMETIMES );

        // Row constraints
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints( 630 );
        RowConstraints row3 = new RowConstraints();
        row1.setVgrow( Priority.SOMETIMES );
        row3.setVgrow( Priority.SOMETIMES );

        gridPane.getColumnConstraints().addAll( column1 , column2 , column1 );
        gridPane.getRowConstraints().addAll( row1 , row2 , row1 );

        // Buttons
        saveButton = new Button("Save");
        saveButton.setId("Save");
        customizeButton(saveButton, 100, 50);
        makeButtonAccessible(saveButton, "Save Button", "This button saves the game.", "This button saves the game. Click it in order to save your current progress, so you can play more later.");
        addSaveEvent();

        loadButton = new Button("Load");
        loadButton.setId("Load");
        customizeButton(loadButton, 100, 50);
        makeButtonAccessible(loadButton, "Load Button", "This button loads a game from a file.", "This button loads the game from a file. Click it in order to load a game that you saved at a prior date.");
        addLoadEvent();

        helpButton = new Button("Help");
        helpButton.setId("Help");
        customizeButton(helpButton, 100, 50);
        makeButtonAccessible(helpButton, "Help Button", "This button displays help information.", "This button displays help information. Click it to get assistance.");
        addInstructionEvent();

        settingsButton = new Button("Settings");
        settingsButton.setId("Settings");
        customizeButton(settingsButton, 100, 50);
        makeButtonAccessible(settingsButton, "Settings Button", "This button displays certain commands.", "This button displays help information. Click it to get assistance.");
        addSettingsEvent();

        HBox topButtons = new HBox();
        topButtons.getChildren().addAll(saveButton, helpButton, loadButton, settingsButton);
        topButtons.setSpacing(10);
        topButtons.setAlignment(Pos.CENTER);

        inputTextField = new TextField();
        inputTextField.setFont(new Font("Arial", 16));
        inputTextField.setFocusTraversable(true);

        inputTextField.setAccessibleRole(AccessibleRole.TEXT_AREA);
        inputTextField.setAccessibleRoleDescription("Text Entry Box");
        inputTextField.setAccessibleText("Enter commands in this box.");
        inputTextField.setAccessibleHelp("This is the area in which you can enter commands you would like to play.  Enter a command and hit return to continue.");
        addTextHandlingEvent(); //attach an event to this input field

        //labels for inventory and room items
        Label objLabel =  new Label("Objects in Room");
        objLabel.setAlignment(Pos.CENTER);
        objLabel.setStyle("-fx-text-fill: #000000;");
        objLabel.setFont(new Font("Arial", 16));

        Label invLabel =  new Label("Your Inventory");
        invLabel.setAlignment(Pos.CENTER);
        invLabel.setStyle("-fx-text-fill: #000000;");
        invLabel.setFont(new Font("Arial", 16));

        //add all the widgets to the GridPane
        gridPane.add( objLabel, 0, 0, 1, 1 );  // Add label
        gridPane.add( topButtons, 1, 0, 1, 1 );  // Add buttons
        gridPane.add( invLabel, 2, 0, 1, 1 );  // Add label

        Label commandLabel = new Label("What would you like to do?");
        commandLabel.setStyle("-fx-text-fill: #00008B;");
        commandLabel.setFont(new Font("Arial", 16));

        updateScene(""); //method displays an image and whatever text is supplied
        updateItems(); //update items shows inventory and objects in rooms

        // adding the text area and submit button to a VBox
        VBox textEntry = new VBox();
        textEntry.setStyle("-fx-background-color: #FF69B4;");
        textEntry.setPadding(new Insets(20, 20, 20, 20));
        textEntry.getChildren().addAll(commandLabel, inputTextField);
        textEntry.setSpacing(10);
        textEntry.setAlignment(Pos.CENTER);
        gridPane.add( textEntry, 0, 2, 3, 1 );
    }

    /**
     * Show Hint Info
     *
     * adds additional text in the GUI to show hint information.
     *
     * @param info the String text to be shown as a hint
     */
    public void showHintInfo(String info)
    {
        Dialog<Void> helpDialog = new Dialog<>();
        helpDialog.setTitle("Hint");
        TextArea textArea = new TextArea();
        textArea.setText(info);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        helpDialog.getDialogPane().getButtonTypes().add(closeButton);
        helpDialog.getDialogPane().setContent(textArea);

        helpDialog.setResultConverter(buttonType -> null);

        helpDialog.showAndWait();
    }
    private void showHelpInfo() {
        Dialog<Void> helpDialog = new Dialog<>();
        helpDialog.setTitle("Help");

        TextArea textArea = new TextArea();
        textArea.setText("To play this game you must move between locations and interact with objects by typing one or two word commands.\n" +
                "\n" +
                "Some commands are motion commands.  These will move you from room to room. Motion commands include:\n" +
                "\n" +
                "UP, DOWN, EAST, WEST, NORTH, SOUTH\n" +
                "\n" +
                "Not all motions are possible in every room. In addition, some rooms may have \"special\" or \"secret\" motion commands.\n" +
                "\n" +
                "There are other action commands in the game. These include:\n" +
                "\n" +
                "COMMANDS: this will print the moves that are legal in a given room.\n" +
                "\n" +
                "HELP: this will display instructions\n" +
                "\n" +
                "INVENTORY: this will print your current inventory.\n" +
                "\n" +
                "LOOK: this will print the description for the current room.\n" +
                "\n" +
                "SKIP: this will skip one question in level 2 if you have the U disk. \n" +
                "\n" +
                "TAKE <object>: this will take an object from a room and place it in your inventory. Replace <object> with the name of the object to take.  The object must be present in the room in order to take it.\n" +
                "\n" +
                "DROP <object>: this will drop an object in your inventory. Replace <object> with the name of the object to drop. The object must be in your inventory to drop it.\n" +
                "\n" +
                "Some paths may be blocked.  To unblock a path you may need a specific object to be in your inventory.\n" +
                "\n" +
                "The game is over when your player reaches the VICTORY room, or when your player DIES.");
        textArea.setEditable(false);
        textArea.setWrapText(true);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        helpDialog.getDialogPane().getButtonTypes().add(closeButton);
        helpDialog.getDialogPane().setContent(textArea);

        helpDialog.setResultConverter(buttonType -> null);

        helpDialog.showAndWait();
    }

    /**
     * Initialize the UI.
     */
    public void initUI() {
        // show the starting UI
        setupGameUI();
        showStartingUI();
    }


    /**
     * makeButtonAccessible
     *
     * @param inputButton the button to add screen reader hooks to
     * @param name ARIA name
     * @param shortString ARIA accessible text
     * @param longString ARIA accessible help text
     */
    public static void makeButtonAccessible(Button inputButton, String name, String shortString, String longString) {
        inputButton.setAccessibleRole(AccessibleRole.BUTTON);
        inputButton.setAccessibleRoleDescription(name);
        inputButton.setAccessibleText(shortString);
        inputButton.setAccessibleHelp(longString);
        inputButton.setFocusTraversable(true);
    }

    /**
     * customizeButton
     *
     * @param inputButton the button to make stylish :)
     * @param w width
     * @param h height
     */
    private void customizeButton(Button inputButton, int w, int h) {
        inputButton.setPrefSize(w, h);
        inputButton.setFont(new Font("Arial", 16));
        inputButton.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");
    }

    /**
     * addTextHandlingEvent
     * Add an event handler to the myTextField attribute
     *
     * If the user hits the ENTER Key, strip white space
     * from the input to myTextField and pass the stripped
     * string to submitEvent for processing.
     *
     * If the user hits the TAB key, move the focus
     * of the scene onto any other node in the scene
     * graph by invoking requestFocus method.
     */
    private void addTextHandlingEvent() {
        inputTextField.setOnKeyPressed(e -> {
            // take user input
            if (e.getCode() == KeyCode.ENTER) {
                String input = inputTextField.getText().strip();

                if (this.model.player.getCurrentRoom().getRoomNumber() == 36)
                    level3UpdateProgress(input);
                else
                    submitEvent(input);
                inputTextField.setText(""); // erase content after
            }
            // change focus
            else if (e.getCode() == KeyCode.TAB) {
                gridPane.requestFocus();
            }
        });
    }


    /**
     * submitEvent
     *
     * @param text the command that needs to be processed
     */
    private void submitEvent(String text) {
        this.inputTextField.setDisable(false);
        text = text.strip(); //get rid of white space
        stopArticulation(); //if speaking, stop

        if (text.equalsIgnoreCase("LOOK") || text.equalsIgnoreCase("L")) {
            String roomDesc = this.model.getPlayer().getCurrentRoom().getRoomDescription();
            String objectString = this.model.getPlayer().getCurrentRoom().getObjectString();
            if (!objectString.isEmpty()) roomDescLabel.setText(roomDesc + "\n\nObjects in this room:\n" + objectString);
            if (Settings.getSettings().getTextToSpeech()) articulateRoomDescription(); //all we want, if we are looking, is to repeat description.
            return;
        } else if (text.equalsIgnoreCase("HELP") || text.equalsIgnoreCase("H")) {
            showInstructions();
            return;
        } else if (text.equalsIgnoreCase("COMMANDS") || text.equalsIgnoreCase("C")) {

            if (this.model.player.getCurrentRoom().getRoomNumber() != 41)  showCommands(); //this is new!  We did not have this command in A1
            return;
        } else if (text.equalsIgnoreCase("SKIP")){
            usePowerup(); // Try to skip the question (in level 2)
            return;
        } else if (text.equalsIgnoreCase("HINT")){
            usePowerup(); //try to get the proper hint to player
        }

        //try to move!
        String output = this.model.interpretAction(text); //process the command!

        if (output == null || (!output.equals("GAME OVER") && !output.equals("FORCED") && !output.equals("HELP") && !output.equals("ENDING") && !output.equals("EXIT"))) {
            updateScene(output);
            updateItems();
            this.model.getPlayer().getCurrentRoom().visit();
        } else if (output.equals("GAME OVER")) {
            updateScene("");
            updateItems();
            PauseTransition pause = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(event -> {
                Platform.exit();
            });
            pause.play();
        } else if (output.equals("EXIT")) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                Platform.exit();
            });
            pause.play();
            Platform.exit();
        } else if (output.equals("ENDING")) {
            showEndingUI();
        } else if (output.equals("FORCED")) {
            // handle "FORCED" events!
            // display the image in the
            //current room and pause, then transition to
            //the forced room.
            this.inputTextField.setDisable(true);
            updateScene("");
            updateItems();
            PauseTransition pause = new PauseTransition(Duration.seconds(5));

            pause.setOnFinished(event -> {
                submitEvent("FORCED");
            });

            pause.play();
        }
    }

    /**
     * showCommands
     *
     * update the text in the GUI (within roomDescLabel)
     * to show all the moves that are possible from the
     * current room.
     */
    private void showCommands() {
        Room curr = model.player.getCurrentRoom();
        String command = curr.getCommands();
        roomDescLabel.setText(command);
    }


    /**
     * updateScene
     *
     * show the current room.
     *
     * @param textToDisplay the text to display below the image.
     */
    public void updateScene(String textToDisplay) {
        // for level 3
        if (this.model.player.getCurrentRoom().getRoomNumber() == 36) {
            usePowerup();
            return;
        }

        getRoomImage(); //get the image of the current room
        formatText(textToDisplay); //format the text to display
        roomDescLabel.setPrefWidth(600);
        roomDescLabel.setPrefHeight(800);
        roomDescLabel.setTextOverrun(OverrunStyle.CLIP);
        roomDescLabel.setWrapText(true);
        VBox roomPane = new VBox(roomImageView,roomDescLabel);
        roomPane.setPadding(new Insets(10));
        roomPane.setAlignment(Pos.TOP_CENTER);
        roomPane.setStyle("-fx-background-color: #FF69B4;");

        gridPane.add(roomPane, 1, 1);
        stage.sizeToScene();

        //finally, articulate the description
        if (Settings.getSettings().getTextToSpeech()){
         if (textToDisplay == null || textToDisplay.isBlank()) articulateRoomDescription();}
    }

    /**
     * formatText
     *
     * Format text for display.
     *
     * @param textToDisplay the text to be formatted for display.
     */
    private void formatText(String textToDisplay) {
        if (textToDisplay == null || textToDisplay.isBlank()) {
            String roomDesc = this.model.getPlayer().getCurrentRoom().getRoomDescription() + "\n";
            String objectString = this.model.getPlayer().getCurrentRoom().getObjectString();
            if (objectString != null && !objectString.isEmpty()) roomDescLabel.setText(roomDesc + "\nObjects in this room:\n" + objectString);
            else roomDescLabel.setText(roomDesc);
        } else roomDescLabel.setText(textToDisplay);
        roomDescLabel.setStyle("-fx-text-fill: #000000;");
        roomDescLabel.setFont(new Font("Arial", 16));
        roomDescLabel.setAlignment(Pos.CENTER);
    }

    /**
     * getRoomImage
     *
     * shows the image for the current room.
     */
    private void getRoomImage() {

        int roomNumber = this.model.getPlayer().getCurrentRoom().getRoomNumber();
        String roomImage = this.model.getDirectoryName() + "/room-images/" + roomNumber + ".png";

        Image roomImageFile = new Image(roomImage);
        roomImageView = new ImageView(roomImageFile);
        roomImageView.setPreserveRatio(true);

        // level 3 and 4 rooms need to be sized differently
        if (31 <= roomNumber && roomNumber != 34 && roomNumber < 50) {
            roomImageView.setFitWidth(600);
            roomImageView.setFitHeight(500);
        }
        else {
            roomImageView.setFitWidth(300);
            roomImageView.setFitHeight(400);
        }

        //set accessible text
        roomImageView.setAccessibleRole(AccessibleRole.IMAGE_VIEW);
        roomImageView.setAccessibleText(this.model.getPlayer().getCurrentRoom().getRoomDescription());
        roomImageView.setFocusTraversable(true);
    }

    /**
     * updateItems
     *
     * shows what items are in the room and in the player's inventory.
     */
    public void updateItems() {
        // first remove all children (so no duplicates)
        objectsInRoom.getChildren().clear();
        objectsInInventory.getChildren().clear();

        // in room
        for (AdventureObject o : this.model.getPlayer().getCurrentRoom().objectsInRoom) {
            String name = o.getName();
            String descriptions = "This is a " + name + " in the room.";
            ImageView imageView = new ImageView(new Image(this.model.getDirectoryName() + "/objectImages/" + name + ".jpg"));
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            imageView.setAccessibleText(descriptions);

            setRoomObjectButton(name, imageView);
        }

        // in inventory
        for (String s : this.model.getPlayer().getInventory()) {
            String descriptions = "This is a " + s + " in your inventory.";
            ImageView imageView = new ImageView(new Image(this.model.getDirectoryName() + "/objectImages/" + s + ".jpg"));
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            imageView.setAccessibleText(descriptions);

            setPlayerObjectButton(s, imageView);
        }

        ScrollPane scO = new ScrollPane(objectsInRoom);
        scO.setPadding(new Insets(10));
        scO.setStyle("-fx-background: #FF69B4; -fx-background-color:transparent;");
        scO.setFitToWidth(true);
        gridPane.add(scO,0,1);

        ScrollPane scI = new ScrollPane(objectsInInventory);
        scI.setFitToWidth(true);
        scI.setStyle("-fx-background: #FF69B4; -fx-background-color:transparent;");
        gridPane.add(scI,2,1);
    }

    @Override
    public void usePowerup() {
        // default behaviour without any power-ups

        // default behaviour for level 2 specifically
        if (String.valueOf(this.model.player.getCurrentRoom().getRoomNumber()).startsWith("2")) {
            Level2Decorator level2Decorator = new Level2Decorator(this);
            level2Decorator.usePowerup();
        }
        // default behaviour for level 3 specifically
        else if (this.model.player.getCurrentRoom().getRoomNumber() == 36) {
            ArrayList<Node> children = new ArrayList<Node>();

            // remove children in cell
            for (Node child: gridPane.getChildren()) {
                if (GridPane.getColumnIndex(child) == 1 && GridPane.getRowIndex(child) == 1)
                    children.add(child);
            }

            for (Node child: children) {
                gridPane.getChildren().remove(child);
            }

            // set up level based on number of power-ups
            level3Powerup.usePowerup();
        }
        // default behaviour for level 4 specifically
        else if (String.valueOf(this.model.player.getCurrentRoom().getRoomNumber()).startsWith("4")){
            Level4Decorator dec = new Level4Decorator(this);
            dec.usePowerup();
        }
    }

    /**
     * level3UpdateProgress
     *
     * Update the level 3 progress label based on what command the player inputs.
     */
    private void level3UpdateProgress(String input) {
        String answer = "CCGGAAGFFEEDDCGGFFEEDGGFFEEDCCGGAAGFFEEDDC";
        String currentProgress = level3Progress.getText();
        input = input.toUpperCase();
        int currentNote = currentProgress.length()-1;
        boolean correct = false;

        // check if player made or lost progress
        if (input.equals("C") || input.equals("D") || input.equals("E") || input.equals("F") ||
                input.equals("G") || input.equals("A") || input.equals("B")) {

            // correct note
            if (answer.substring(currentNote+1, currentNote+2).equals(input)) {
                stopArticulation();
                correct = true;
                level3Progress.setText(currentProgress + input);

                // play note audio
                String musicFile = "./" + this.model.getDirectoryName() + "/sounds/" + input + "-note.mp3";
                Media sound = new Media(new File(musicFile).toURI().toString());

                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
                mediaPlaying = true;
            }
        }
        // played the wrong note
        if (!correct) {
            if (!currentProgress.isEmpty()) // won't have negative progress
                level3Progress.setText(currentProgress.substring(0, currentProgress.length() - 1));
            
            stopArticulation();

            // play incorrect audio
            String musicFile = "./" + this.model.getDirectoryName() + "/sounds/" + "wrong-note.mp3";
            Media sound = new Media(new File(musicFile).toURI().toString());

            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
            mediaPlaying = true;
        }

        // check if player won the level
        if (level3Progress.getText().equals(answer)) {
            this.inputTextField.setDisable(true); // don't need to play anymore notes
            level3Progress.setTextFill(Color.GREEN);
            
            // pause for a bit so colour change and last note is visible
            PauseTransition pause = new PauseTransition(Duration.seconds(3));

            pause.setOnFinished(event -> {
                submitEvent("WIN");
            });

            pause.play();
        }
    }

    /**
     * setRoomObjectButton
     *
     * This method set up a button representing an object in the room
     * by the name of the button, image of the button, and the action
     * to take when an event happens on it.
     *
     * @param name      the name of the button
     * @param imageView the image of the object that the button represents
     */
    private void setRoomObjectButton(String name, ImageView imageView) {
        Button objectButton1 = new Button(name, imageView);
        objectButton1.setId(name);
        objectButton1.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
        makeButtonAccessible(objectButton1, "Object Button", "This button represents a " + name + " in the room.", "This button represents a " + name + " in the room. Click the button to pick it up.");
        objectsInRoom.getChildren().add(objectButton1);

        objectButton1.setOnAction(event -> {
            this.model.getPlayer().takeObject(name);
            objectsInRoom.getChildren().remove(objectButton1);
            setPlayerObjectButton(name, imageView);
            formatText("YOU HAVE TAKEN: \n" + name);
        });

    }

    /**
     * setPlayerObjectButton
     *
     * This method set up a button representing an object in the player's
     * inventory by the name of the button, image of the button,
     * and the action to take when an event happens on it.
     *
     * @param s         the name of the button
     * @param imageView the image of the object that the button represents
     */
    private void setPlayerObjectButton(String s, ImageView imageView) {
        Button objectButton = new Button(s, imageView);
        objectButton.setId(s);
        objectButton.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
        makeButtonAccessible(objectButton, "Object Button", "This button represents a " + s + " in your inventory.", "This button represents a " + s + " in your inventory. Click the button to drop it.");
        objectsInInventory.getChildren().add(objectButton);

        objectButton.setOnAction(event -> {
            this.model.getPlayer().dropObject(s);
            objectsInInventory.getChildren().remove(objectButton);
            setRoomObjectButton(s, imageView);
            formatText("YOU HAVE DROPPED: \n" + s);
        });

    }

    /**
     * Toggle the game instructions.
     *
     * If the instructions aren't displayed, the help text is displayed in the centre.
     * If the instructions are already displayed, the current room is redrawn.
     */
    public void showInstructions() {
        ArrayList<Node> children = new ArrayList<Node>();

        // remove children in cell
        for (Node child: gridPane.getChildren()) {
            if (GridPane.getColumnIndex(child) == 1 && GridPane.getRowIndex(child) == 1)
                children.add(child);
        }

        for (Node child: children) {
            gridPane.getChildren().remove(child);
        }

        // toggle instructions
        if (!helpToggle) {
            // create instructions label
            Label instructionLabel = new Label();
            instructionLabel.setText(model.getInstructions());

            // style label
            instructionLabel.setPrefWidth(500);
            instructionLabel.setPrefHeight(500);
            instructionLabel.setTextOverrun(OverrunStyle.CLIP);
            instructionLabel.setWrapText(true);
            instructionLabel.setAlignment(Pos.CENTER);
            instructionLabel.setTextFill(Color.WHITE);
            VBox instructionPane = new VBox(instructionLabel);
            instructionPane.setPadding(new Insets(10));
            instructionPane.setAlignment(Pos.TOP_CENTER);
            instructionPane.setStyle("-fx-background-color: #8A2BE2;");

            // add label to gridPane
            gridPane.add(instructionPane, 1, 1);
            stage.sizeToScene();
            helpToggle = true;
        }
        else {
            updateScene(null); // remove instructions
            helpToggle = false;
        }
    }

    /**
     * Show the end screen UI.
     */
    public void showEndingUI(){
        // Clear the existing content
        gridPane.getChildren().clear();
        articulateRoomDescription();

        //Inventory + Room items
        objectsInInventory.setSpacing(10);
        objectsInInventory.setAlignment(Pos.TOP_CENTER);
        objectsInRoom.setSpacing(10);
        objectsInRoom.setAlignment(Pos.TOP_CENTER);

        gridPane.setPadding(new Insets(20));
        gridPane.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#FF69B4"),
                new CornerRadii(0),
                new Insets(0)
        )));

        //Three columns, three rows for the GridPane
        ColumnConstraints column1 = new ColumnConstraints(150);
        ColumnConstraints column2 = new ColumnConstraints(650);
        ColumnConstraints column3 = new ColumnConstraints(150);
        column3.setHgrow( Priority.SOMETIMES ); //let some columns grow to take any extra space
        column1.setHgrow( Priority.SOMETIMES );

        // Row constraints
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints( 630 );
        RowConstraints row3 = new RowConstraints();
        row1.setVgrow( Priority.SOMETIMES );
        row3.setVgrow( Priority.SOMETIMES );

        gridPane.getColumnConstraints().addAll( column1 , column2 , column1 );
        gridPane.getRowConstraints().addAll( row1 , row2 , row1 );

        // Buttons
        saveButton = new Button("Exit"); // Instead of save button, we will have exit button.
        saveButton.setId("Exit");
        customizeButton(saveButton, 100, 50);
        makeButtonAccessible(saveButton, "Exit Button", "This button exits the game.", "This button exits the game. Click it in order to exit the entire game.");
        saveButton.setOnAction(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                Platform.exit();
            });
            pause.play();
        });

        loadButton = new Button("Load");
        loadButton.setId("Load");
        customizeButton(loadButton, 100, 50);
        makeButtonAccessible(loadButton, "Load Button", "This button loads a game from a file.", "This button loads the game from a file. Click it in order to load a game that you saved at a prior date.");
        addLoadEvent();

        helpButton = new Button("Help");
        helpButton.setId("Help");
        customizeButton(helpButton, 200, 50);
        makeButtonAccessible(helpButton, "Help Button", "This button displays help information.", "This button displays help information. Click it to get assistance.");
        addInstructionEvent();

        HBox topButtons = new HBox();
        topButtons.getChildren().addAll(saveButton, helpButton, loadButton);
        topButtons.setSpacing(10);
        topButtons.setAlignment(Pos.CENTER);

        inputTextField = new TextField();
        inputTextField.setFont(new Font("Arial", 16));
        inputTextField.setFocusTraversable(true);

        inputTextField.setAccessibleRole(AccessibleRole.TEXT_AREA);
        inputTextField.setAccessibleRoleDescription("Text Entry Box");
        inputTextField.setAccessibleText("Enter commands in this box.");
        inputTextField.setAccessibleHelp("This is the area in which you can enter commands you would like to play.  Enter a command and hit return to continue.");
        addTextHandlingEvent(); //attach an event to this input field

        //labels for inventory and room items
        Label objLabel =  new Label("Objects in Room");
        objLabel.setAlignment(Pos.CENTER);
        objLabel.setStyle("-fx-text-fill: #000000;");
        objLabel.setFont(new Font("Arial", 16));

        Label invLabel =  new Label("Your Inventory");
        invLabel.setAlignment(Pos.CENTER);
        invLabel.setStyle("-fx-text-fill: #000000;");
        invLabel.setFont(new Font("Arial", 16));

        //add all the widgets to the GridPane
        gridPane.add( objLabel, 0, 0, 1, 1 );  // Add label
        gridPane.add( topButtons, 1, 0, 1, 1 );  // Add buttons
        gridPane.add( invLabel, 2, 0, 1, 1 );  // Add label

        Label commandLabel = new Label("What would you like to do?");
        commandLabel.setStyle("-fx-text-fill: #00008B;");
        commandLabel.setFont(new Font("Arial", 16));

        String endingInstructions= "";
        int itemsCollected = this.model.getPlayer().getInventory().size();
        double collectedPercentage = (double) itemsCollected / 10; // number of objects -1 (excluding the trophy)

        endingInstructions += "Congratulations! You have successfully finished the game and saved many stray cats! " + "\n";
        endingInstructions += "Take your trophy! You deserve it! (You can click on the exit button or enter \"EXIT\" to exit the game~)" + "\n";
        endingInstructions += "You have collected "+ itemsCollected + " items and cats!" + "\n";
        endingInstructions += "Percentage of collections: " + collectedPercentage * 100 + "%" + "\n";
        endingInstructions += "You can see them on the right ->";

        updateScene(endingInstructions); //method displays an image and whatever text is supplied
        updateItems(); //update items shows inventory and objects in rooms

        // adding the text area and submit button to a VBox
        VBox textEntry = new VBox();
        textEntry.setStyle("-fx-background-color: #FF69B4;");
        textEntry.setPadding(new Insets(20, 20, 20, 20));
        textEntry.getChildren().addAll(commandLabel, inputTextField);
        textEntry.setSpacing(10);
        textEntry.setAlignment(Pos.CENTER);
        gridPane.add( textEntry, 0, 2, 3, 1 );

    }
    /**
     * This method handles the event related to the
     * help button.
     */
    public void addInstructionEvent() {
        helpButton.setOnAction(e -> {
            stopArticulation(); //if speaking, stop
            showInstructions();
        });
    }

    /**
     * This method handles the event related to the
     * save button.
     */
    public void addSaveEvent() {
        saveButton.setOnAction(e -> {
            gridPane.requestFocus();
            SaveView saveView = new SaveView(this);
        });
    }

    /**
     * This method handles the event related to the
     * settings button.
     */
    public void addSettingsEvent(){
        settingsButton.setOnAction(e -> {
            stopArticulation();
            gridPane.requestFocus();
            SettingsView settingsView = new SettingsView(this);
        });
    }

    /**
     * This method handles the event related to the
     * load button.
     */
    public void addLoadEvent() {
        loadButton.setOnAction(e -> {
            gridPane.requestFocus();
            LoadView loadView = new LoadView(this);
        });
    }


    /**
     * This method articulates Room Descriptions.
     */
    public void articulateRoomDescription() {
        String musicFile;
        String adventureName = this.model.getDirectoryName();
        String roomName = this.model.getPlayer().getCurrentRoom().getRoomName();

        if (!this.model.getPlayer().getCurrentRoom().getVisited()) musicFile = "./" + adventureName + "/sounds/" + roomName.toLowerCase() + "-long.mp3" ;
        else musicFile = "./" + adventureName + "/sounds/" + roomName.toLowerCase() + "-short.mp3" ;
        musicFile = musicFile.replace(" ","-");

        Media sound = new Media(new File(musicFile).toURI().toString());

        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        mediaPlaying = true;

    }

    /**
     * This method stops articulations.
     */
    public void stopArticulation() {
        if (mediaPlaying) {
            mediaPlayer.stop(); //shush!
            mediaPlaying = false;
        }
    }

    /**
     * Get the model from this AdventureGameView.
     *
     * @return AdventureGame model
     */
    public AdventureGame getModel() {return model;}

    /**
     * Get the gridPane from this AdventureGameView.
     *
     * @return gridPane
     */
    public GridPane getGridPane() {return gridPane;}

    /**
     * Get the stage from this AdventureGameView.
     *
     * @return stage
     */
    public Stage getStage() {return stage;}

    /**
     * Get the level3Progress label from this AdventureGameView.
     *
     * @return level 3 progress Label
     */
    public Label getLevel3ProgressLabel() {return level3Progress;}
}