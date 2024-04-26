package views;

import AdventureModel.Settings;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;

/**
 * SettingsView Class
 */
public class SettingsView {
    private AdventureGameView adventureGameView;
    private Button textToSpeechButton = new Button("text to speech");
    private Label ttsOn = new Label("text to speech is on");
    private Label ttsOff = new Label("text to speech is off");
    private VBox GameBox = new VBox(10, textToSpeechButton);
    private VBox dialogVbox = new VBox(20);
    final Stage dialog = new Stage();
    private Scene dialogScene = new Scene(dialogVbox, 400, 400);

    /**
     * SettingsView Constructor
     *
     * @param adventureGameView the adventure game view
     */
    public SettingsView(AdventureGameView adventureGameView) {
        this.adventureGameView = adventureGameView;
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(adventureGameView.stage);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));
        dialogVbox.setStyle("-fx-background-color: #e0b0ff;");

        ttsOn.setId("TextToSpeechOn");
        ttsOff.setId("TextToSpeechOff");
        ttsOn.setStyle("-fx-text-fill: #000000;");
        ttsOff.setStyle("-fx-text-fill: #000000;");
        ttsOn.setFont(new Font(16));
        ttsOff.setFont(new Font(16));

        textToSpeechButton.setId("ttsButton");
        textToSpeechButton.setStyle("-fx-background-color: #915f6d; -fx-text-fill: white;");
        textToSpeechButton.setPrefSize(200, 50);

        AdventureGameView.makeButtonAccessible(textToSpeechButton, "turn On text to Speech", "Turns on text to speech function", "Use this button to turn on text to speech function");
        textToSpeechButton.setOnAction(e -> changeTts());

        GameBox.setAlignment(Pos.CENTER);

        textToSpeechButton.setOnAction(e -> {changeTts();});

        dialogVbox.getChildren().add(GameBox);
        dialog.setScene(dialogScene);
        dialog.show();
    }


    /**
     * changeTts
     *
     * toggles text to speech
     */
    public void changeTts(){
        if (Settings.getSettings().getTextToSpeech()){

            PauseTransition pause = new PauseTransition(Duration.seconds(2));

            dialogVbox.getChildren().add(ttsOff);
            Settings.getSettings().turnOffTextToSpeech();

            pause.setOnFinished(event ->
            {
                dialogVbox.getChildren().remove(ttsOff);

            });
            pause.play();
        }
        else{
            PauseTransition pause = new PauseTransition(Duration.seconds(2));

            Settings.getSettings().turnOnTextToSpeech();

            dialogVbox.getChildren().add(ttsOn);

            pause.setOnFinished(event -> {
                dialogVbox.getChildren().remove(ttsOn);
            });
            pause.play();
        }
    }
}
