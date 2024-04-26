package powerups;

import AdventureModel.PassageTable;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;
// import org.hamcrest.core.Is;
import views.AdventureGameView;

/**
 * Level2Decorator class
 */
public class Level2Decorator extends GUIDecorator {
    /**
     * Level2Decorator Constructor
     *
     * @param g the GUISource being wrapped
     */
    public Level2Decorator(GUISource g) {super(g);}

    /**
     * Use power-up in level 2
     */
    @Override
    public void usePowerup() {
        AdventureGameView gui = (AdventureGameView) this.getSource();
        PassageTable motionTable = gui.getModel().getPlayer().getCurrentRoom().getMotionTable();

        if (motionTable.optionExists("SKIP")){
            if (gui.getModel().player.checkIfObjectInInventory("UDisk")){
                gui.getModel().getPlayer().dropObject("UDisk"); // Drop the U disk
                String output = gui.getModel().interpretAction("SKIP");

                if (output == null) {
                    gui.updateScene(output);
                    gui.updateItems();
                    gui.getModel().getPlayer().getCurrentRoom().visit();
                }
            }else {
                gui.updateScene("You don't have the U disk! Cannot skip!");
            }
        }else {
            gui.updateScene("You cannot skip in this room!");
        }
    }
}
