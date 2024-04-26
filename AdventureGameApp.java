import AdventureModel.AdventureGame;
import javafx.application.Application;
import javafx.stage.Stage;
import views.AdventureGameView;

import java.io.IOException;

/**
 * Class AdventureGameApp.
 */
public class AdventureGameApp extends  Application {

    AdventureGame model;
    AdventureGameView view;


    /**
     * Default constructor for AdventureGameApp
     */
    public AdventureGameApp() {
    }

    /**
     * Launches the game
     *
     * @param args arguments
     */
    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.model = new AdventureGame("CatHeroes");
        this.view = new AdventureGameView(model, primaryStage);

    }

}
