package powerups;

import AdventureModel.AdventureObject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import views.AdventureGameView;

/**
 * Level3Decorator class
 */
public class Level3Decorator extends GUIDecorator {
    /**
     * Level3Decorator Constructor
     *
     * @param g the GUISource being wrapped
     */
    public Level3Decorator(GUISource g) {super(g);}

    /**
     * Use power-up in level 3
     */
    @Override
    public void usePowerup() {
        AdventureGameView gui = (AdventureGameView) this.getSource();

        // count the number of music note powerups the player has
        int powerups = 0;

        if (gui.getModel().player.checkIfObjectInInventory("MUSICNOTE"))
            powerups++;
        if (gui.getModel().player.checkIfObjectInInventory("MUSICNOTES"))
            powerups++;

        // create sheet music image
        Image songImageFile;
        
        if (powerups == 0)
            songImageFile = new Image("file:images/song.jpg");
        else if (powerups == 1)
            songImageFile = new Image("file:images/song_powerup1.jpg");
        else
            songImageFile = new Image("file:images/song_powerup2.jpg");


        ImageView songImageView = new ImageView(songImageFile);
        songImageView.setPreserveRatio(true);
        songImageView.setFitWidth(500);
        songImageView.setFitHeight(300);

        // create piano image
        Image pianoImageFile = new Image("file:images/piano.png");
        ImageView pianoImageView = new ImageView(pianoImageFile);
        pianoImageView.setPreserveRatio(true);
        pianoImageView.setFitWidth(600);
        pianoImageView.setFitHeight(400);

        //set accessible text
        songImageView.setAccessibleRole(AccessibleRole.IMAGE_VIEW);
        songImageView.setFocusTraversable(true);
        pianoImageView.setAccessibleRole(AccessibleRole.IMAGE_VIEW);
        pianoImageView.setFocusTraversable(true);

        // progress label
        Label level3Progress = gui.getLevel3ProgressLabel();
        level3Progress.setMaxWidth(1000);
        level3Progress.setPrefHeight(100);
        level3Progress.setAlignment(Pos.CENTER);
        level3Progress.setFont(new Font(22));
        level3Progress.setTextFill(Color.DARKMAGENTA);

        // add all components to gridPane
        VBox level3Pane = new VBox(songImageView, pianoImageView, level3Progress);
        level3Pane.setPadding(new Insets(10));
        level3Pane.setAlignment(Pos.TOP_CENTER);
        level3Pane.setStyle("-fx-background-color: #FF69B4;");

        gui.getGridPane().add(level3Pane, 1, 1);
        gui.getStage().sizeToScene();
    }
}
